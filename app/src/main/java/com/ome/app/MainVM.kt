package com.ome.app

import androidx.lifecycle.SavedStateHandle
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.ome.app.data.ConnectionStatusListener
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.model.network.request.StoveRequest
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.domain.model.network.response.UserResponse
import com.ome.app.domain.model.network.response.asBurnerState
import com.ome.app.domain.model.network.response.asKnobState
import com.ome.app.domain.model.network.websocket.KnobState
import com.ome.app.domain.model.network.websocket.MacAddress
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.utils.isFalse
import com.ome.app.utils.log
import com.ome.app.utils.logi
import com.ome.app.utils.orMinusOne
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class MainVM @Inject constructor(
    private val amplifyManager: AmplifyManager,
    private val pref: PreferencesProvider,
    private val userRepository: UserRepository,
    private val stoveRepository: StoveRepository,
    val webSocketManager: WebSocketManager,
    private val savedStateHandle: SavedStateHandle,
    val connectionStatusListener: ConnectionStatusListener
) : BaseViewModel() {
    var userInfo = savedStateHandle.getStateFlow("userInfo", pref.getUserData())
    var knobState =
        savedStateHandle.getStateFlow("knobState", mutableMapOf<MacAddress, KnobState>())
    var knobs = savedStateHandle.getStateFlow("knobs", listOf<KnobDto>())

    override var defaultErrorHandler = CoroutineExceptionHandler { _, throwable ->
        if (isSplashScreenLoading)
            savedStateHandle["startDestination"] = R.id.launchFragment
        else
            defaultErrorLiveData.postValue(throwable.message)
    }
    var startDestinationInitialized : Boolean
        get() = savedStateHandle["startDestinationInitialized"] ?: false
        set(value) { savedStateHandle["startDestinationInitialized"] = value }

    val startDestination = savedStateHandle.getStateFlow<Int?>("startDestination", null)

    val socketConnected = MutableSharedFlow<Boolean>()

    var isSplashScreenLoading = true
    var startDestinationJob: Job? = null

    var stoveData = StoveRequest()

    var selectedBurnerIndex: Int? = null

    init {
        launch(ioContext) {
            val oldKnobListSize = userRepository.userFlow.value?.knobMacAddrs?.size.orMinusOne()
            userRepository.userFlow.filterNotNull().collect {
                it.log("userFlow")
                pref.saveUserData(it)
                savedStateHandle["userInfo"] = it
                if (oldKnobListSize != it.knobMacAddrs.size) {
                    savedStateHandle["knobs"] = emptyList<KnobDto>()
                    stoveRepository.knobsFlow.value = emptyList()
                }
                stoveRepository.getAllKnobs()
            }
        }
        launch(ioContext) {
            stoveRepository.knobsFlow.filterNotNull().collect { lst ->
                savedStateHandle["knobs"] = lst
                webSocketManager.knobState.value =
                    lst.associateByTo(mutableMapOf(), { it.macAddr }, { it.asKnobState })
            }
        }
        launch(ioContext) {
            webSocketManager.knobState.collect {
                savedStateHandle["knobState"] = it
            }
        }
    }

    fun getUserInfo() {
        launch(ioContext) {
            userRepository.getUserData()
        }
    }

    fun getAllKnobs() {
        launch(ioContext) {
            stoveRepository.getAllKnobs()
        }
    }

    fun setSafetyLockOn() {
        launch(ioContext) {
            stoveRepository.turnOffAllKnobs()
            userRepository.userFlow.value?.knobMacAddrs?.forEach {
                stoveRepository.setSafetyLockOn(it)
            }
            stoveRepository.getAllKnobs()
        }
    }

    fun setSafetyLockOff() {
        launch(ioContext) {
            userRepository.userFlow.value?.knobMacAddrs?.forEach {
                stoveRepository.setSafetyLockOff(it)
            }
            stoveRepository.getAllKnobs()
        }
    }

    fun turnOffAllKnobs() {
        launch(ioContext) {
            stoveRepository.turnOffAllKnobs()
            stoveRepository.getAllKnobs()
        }
    }

    fun getKnobByMac(macAddress: MacAddress) = knobs.value.find { it.macAddr == macAddress }
    fun getKnobStateByMac(macAddress: MacAddress) = knobState.map { it[macAddress] }
    fun getStovePositionByMac(macAddress: MacAddress) =
        knobs.value.find { it.macAddr == macAddress }?.stovePosition.orMinusOne()

    fun getKnobBurnerStatesByMac(macAddress: MacAddress) =
        knobs.value.find { it.macAddr == macAddress }?.asBurnerState.orEmpty()

    fun initStartDestination() {
        if (startDestination.value != null) return
        startDestinationJob?.cancel()
        startDestinationJob = launch(ioContext) {
            val authSession =
                withContext(Dispatchers.Default + defaultErrorHandler) { amplifyManager.fetchAuthSession() }
            authSession.session?.let {
                if (it.isSignedIn) {
                    if (it is AWSCognitoAuthSession) {
                        if (it.awsCredentials.error == null) {
                            val userAttributes =
                                withContext(Dispatchers.Default + defaultErrorHandler) { amplifyManager.fetchUserAttributes() }


                            val accessToken =
                                it.userPoolTokens.value?.accessToken

                            logi("accessToken: $accessToken")
                            if (accessToken != null && userAttributes.attributes != null) {
                                userAttributes.attributes?.forEach { attr ->
                                    if (attr.key.keyString == "sub") {
                                        pref.saveUserId(attr.value)
                                        logi("userId: $attr.value")
                                    }
                                }
                                pref.saveAccessToken(accessToken)

                                when (val result = userRepository.getUserData()) {
                                    is ResponseWrapper.Error -> {
                                        if (result.message.startsWith("Not found")) {
                                            amplifyManager.deleteUser()
                                            pref.clearData()
                                            savedStateHandle["startDestination"] = R.id.launchFragment
                                        } else {
                                            savedStateHandle["startDestination"] = R.id.dashboardFragment
                                        }
                                    }

                                    is ResponseWrapper.Success -> {
                                        if (result.value.stoveSetupComplete.isFalse()) {
                                            savedStateHandle["startDestination"] = R.id.welcomeFragment
                                            return@launch
                                        }
                                        savedStateHandle["startDestination"] = R.id.dashboardFragment
                                        connectToSocket()
                                    }
                                }
                            } else {
                                savedStateHandle["startDestination"] = R.id.launchFragment
                            }
                        } else {
                            savedStateHandle["startDestination"] = R.id.launchFragment
                        }
                    }
                } else {
                    savedStateHandle["startDestination"] = R.id.launchFragment
                }
            } ?: run {
                savedStateHandle["startDestination"] = R.id.launchFragment
            }
        }
    }

    fun connectToSocket(needStatus: Boolean = false) {
        launch(ioContext) {
//            webSocketManager.onSocketConnect = {
//                if (needStatus) {
//                    "onSocketConnect $it".loge()
//                    if (it) {
//                        delay(5.seconds)
//                        socketConnected.emit(true)
//                    } else
//                        socketConnected.emit(false)
//                }
//            }
            val knobs = stoveRepository.getAllKnobs()
            savedStateHandle["knobs"] = knobs.toList()
            try {
                pref.getUserId()?.let { userId ->
                    if (knobs.isNotEmpty())
                        webSocketManager.initWebSocket(knobs, userId)
                }
            } catch (e: Exception) {
                if (needStatus) error("Error with socket connection.")
                else connectToSocket()
            }
        }
    }

    fun signOut(onEnd: () -> Unit) {
        launch(ioContext) {
            amplifyManager.signUserOut()
            pref.clearData()
            userRepository.userFlow.emit(null)
            savedStateHandle["userInfo"] = UserResponse()
            amplifyManager.signOutFlow.emit(true)
            withContext(mainContext) {
                onEnd()
            }
        }
    }

    fun registerConnectionListener() {
        connectionStatusListener.registerListener()
    }
}

val dummyKnobs = listOf(
    KnobDto(
        angle = 200,
        battery = 30,
        batteryVolts = 4.5,
        calibrated = true,
        calibration = KnobDto.CalibrationDto(
            offAngle = 0,
            rotationDir = 2,
            zones = listOf(
                KnobDto.CalibrationDto.ZoneDto(
                    lowAngle = 45,
                    mediumAngle = 95,
                    highAngle = 150,
                    zoneName = "Single",
                    zoneNumber = 1
                ),
                KnobDto.CalibrationDto.ZoneDto(
                    lowAngle = 310,
                    mediumAngle = 265,
                    highAngle = 220,
                    zoneName = "Single",
                    zoneNumber = 2
                )
            )
        ),
        connectStatus = "online",
        firmwareVersion = "ceteros",
        gasOrElectric = "graeci",
        ipAddress = "explicari",
        lastScheduleCommand = "intellegebat",
        macAddr = "fake_mac1",
        mountingSurface = "dolor",
        rssi = 1597,
        safetyLock = false,
        scheduleFinishTime = 9022,
        schedulePauseRemainingTime = 3846,
        scheduleStartTime = 4237,
        stoveId = "pri",
        stovePosition = 1,
        temperature = 6.7,
        updated = "explicari",
        userId = "enim"
    ),
    KnobDto(
        angle = 0,
        battery = 70,
        batteryVolts = 4.5,
        calibrated = true,
        calibration = KnobDto.CalibrationDto(
            offAngle = 0,
            rotationDir = 2,
            zones = listOf(
                KnobDto.CalibrationDto.ZoneDto(
                    lowAngle = 130,
                    mediumAngle = 85,
                    highAngle = 45,
                    zoneName = "Single",
                    zoneNumber = 1
                ),
                KnobDto.CalibrationDto.ZoneDto(
                    lowAngle = 220,
                    mediumAngle = 265,
                    highAngle = 320,
                    zoneName = "Single",
                    zoneNumber = 2
                )
            )
        ),
        connectStatus = "online",
        firmwareVersion = "ceteros",
        gasOrElectric = "graeci",
        ipAddress = "explicari",
        lastScheduleCommand = "intellegebat",
        macAddr = "fake_mac2",
        mountingSurface = "dolor",
        rssi = 1597,
        safetyLock = false,
        scheduleFinishTime = 9022,
        schedulePauseRemainingTime = 3846,
        scheduleStartTime = 4237,
        stoveId = "pri",
        stovePosition = 2,
        temperature = 6.7,
        updated = "explicari",
        userId = "enim"
    ),
    KnobDto(
        angle = 100,
        battery = 70,
        batteryVolts = 4.5,
        calibrated = true,
        calibration = KnobDto.CalibrationDto(
            offAngle = 0,
            rotationDir = 2,
            zones = listOf(
                KnobDto.CalibrationDto.ZoneDto(
                    lowAngle = 45,
                    mediumAngle = 85,
                    highAngle = 130,
                    zoneName = "Single",
                    zoneNumber = 1
                ),
                KnobDto.CalibrationDto.ZoneDto(
                    lowAngle = 220,
                    mediumAngle = 265,
                    highAngle = 320,
                    zoneName = "Single",
                    zoneNumber = 2
                )
            )
        ),
        connectStatus = "online",
        firmwareVersion = "ceteros",
        gasOrElectric = "graeci",
        ipAddress = "explicari",
        lastScheduleCommand = "intellegebat",
        macAddr = "fake_mac3",
        mountingSurface = "dolor",
        rssi = -20,
        safetyLock = false,
        scheduleFinishTime = 9022,
        schedulePauseRemainingTime = 3846,
        scheduleStartTime = 4237,
        stoveId = "pri",
        stovePosition = 3,
        temperature = 6.7,
        updated = "explicari",
        userId = "enim"
    ),
    KnobDto(
        angle = 50,
        battery = 90,
        batteryVolts = 4.5,
        calibrated = true,
        calibration = KnobDto.CalibrationDto(
            offAngle = 0,
            rotationDir = 1,
            zones = listOf(
                KnobDto.CalibrationDto.ZoneDto(
                    highAngle = 300,
                    lowAngle = 100,
                    mediumAngle = 200,
                    zoneName = "Single",
                    zoneNumber = 1
                )
            )
        ),
        connectStatus = "online",
        firmwareVersion = "ceteros",
        gasOrElectric = "graeci",
        ipAddress = "explicari",
        lastScheduleCommand = "intellegebat",
        macAddr = "fake_mac4",
        mountingSurface = "dolor",
        rssi = -70,
        safetyLock = false,
        scheduleFinishTime = 9022,
        schedulePauseRemainingTime = 3846,
        scheduleStartTime = 4237,
        stoveId = "pri",
        stovePosition = 4,
        temperature = 6.7,
        updated = "explicari",
        userId = "enim"
    ),
    KnobDto(
        angle = 0,
        battery = 90,
        batteryVolts = 4.5,
        calibrated = false,
        calibration = KnobDto.CalibrationDto(
            offAngle = 0,
            rotationDir = 1,
            zones = listOf(
                KnobDto.CalibrationDto.ZoneDto(
                    highAngle = 300,
                    lowAngle = 100,
                    mediumAngle = 200,
                    zoneName = "Single",
                    zoneNumber = 1
                )
            )
        ),
        connectStatus = "online",
        firmwareVersion = "ceteros",
        gasOrElectric = "graeci",
        ipAddress = "explicari",
        lastScheduleCommand = "intellegebat",
        macAddr = "fake_mac5",
        mountingSurface = "dolor",
        rssi = -70,
        safetyLock = false,
        scheduleFinishTime = 9022,
        schedulePauseRemainingTime = 3846,
        scheduleStartTime = 4237,
        stoveId = "pri",
        stovePosition = 5,
        temperature = 6.7,
        updated = "explicari",
        userId = "enim"
    )
)

