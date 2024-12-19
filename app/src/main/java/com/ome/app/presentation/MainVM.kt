package com.ome.app.presentation

import androidx.lifecycle.SavedStateHandle
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.ome.app.R
import com.ome.app.data.ConnectionStatusListener
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.model.network.request.CreateUserRequest
import com.ome.app.domain.model.network.request.StoveRequest
import com.ome.app.domain.model.network.response.*
import com.ome.app.domain.model.network.websocket.KnobState
import com.ome.app.domain.model.network.websocket.MacAddress
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.utils.isFalse
import com.ome.app.utils.log
import com.ome.app.utils.orMinusOne
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds


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
    var socketError = MutableSharedFlow<Unit>()

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
    var selectedDirection: Int? = null
    var selectedDualZone: Boolean? = null

    init {
        launch(ioContext) {
            var oldKnobList = userRepository.userFlow.value?.knobMacAddrs
            userRepository.userFlow.filterNotNull().collect {
                it.log("userFlow")
                pref.saveUserData(it)
                savedStateHandle["userInfo"] = it
                if (oldKnobList?.toSet() != it.knobMacAddrs.toSet()) {
                    oldKnobList = it.knobMacAddrs
                    savedStateHandle["knobs"] = emptyList<KnobDto>()
                    stoveRepository.knobsFlow.value = emptyList()
                    stoveRepository.getAllKnobs()
                }
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
        launch(ioContext, false) {
            stoveRepository.getAllKnobs()
        }
    }

    fun getAllKnobsUntilNotEmpty() {
        launch(ioContext) {
            while (stoveRepository.knobsFlow.value.isEmpty()) {
                if(!connectionStatusListener.isConnected) {
                    delay(1.seconds)
                    continue
                }
                stoveRepository.getAllKnobs()
                delay(1.seconds)
            }
        }
    }

    fun setSafetyLockOn() {
        launch(ioContext) {
            stoveRepository.turnOffAllKnobs()
            stoveRepository.setSafetyLockOn(*userRepository.userFlow.value?.knobMacAddrs?.toTypedArray().orEmpty())
            stoveRepository.getAllKnobs()
        }
    }

    fun setSafetyLockOff(vararg macAddress: String = userRepository.userFlow.value?.knobMacAddrs?.toTypedArray().orEmpty()) {
        launch(ioContext) {
            stoveRepository.setSafetyLockOff(*macAddress)
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

                            log("accessToken: $accessToken")
                            if (accessToken != null && userAttributes.attributes != null) {
                                userAttributes.attributes?.forEach { attr ->
                                    if (attr.key.keyString == "sub") {
                                        pref.saveUserId(attr.value)
                                        log("userId: $attr.value")
                                    }
                                }
                                pref.saveAccessToken(accessToken)

                                when (val result = userRepository.getUserData()) {
                                    is ResponseWrapper.Error -> {
                                        if (result.message.startsWith("Not found")) {
                                            amplifyManager.deleteUser()
                                            pref.clearData()
                                            savedStateHandle["startDestination"] =
                                                R.id.launchFragment
                                        } else {
                                            savedStateHandle["startDestination"] =
                                                R.id.dashboardFragment
                                        }
                                    }

                                    is ResponseWrapper.Success -> {
                                        if (result.value.stoveSetupComplete.isFalse()) {
                                            savedStateHandle["startDestination"] =
                                                R.id.welcomeFragment
                                            return@launch
                                        }
                                        savedStateHandle["startDestination"] =
                                            R.id.dashboardFragment
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
        launch(ioContext, needStatus) {
            val knobs = stoveRepository.getAllKnobs()
            savedStateHandle["knobs"] = knobs.toList()
            loadingLiveData.postValue(false)
            try {
                pref.getUserId()?.let { userId ->
                    if (knobs.isNotEmpty())
                        webSocketManager.initKnobWebSocket(knobs, userId)
                }
            } catch (e: Exception) {
                connectToSocket()
            }
        }
    }

    fun signOut(onEnd: () -> Unit) {
        launch(ioContext) {
            userRepository.updateUser(CreateUserRequest(
                deviceTokens = userInfo.value.deviceTokens.toMutableList().apply {
                    remove(Firebase.messaging.token.await())
                }.distinct()
            ))
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

/*
val dummyKnobs = listOf(
    //1
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
    //2
    KnobDto(
        angle = 50,
        battery = 70,
        batteryVolts = 4.5,
        calibrated = true,
        calibration = KnobDto.CalibrationDto(
            offAngle = 60,
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
    //3
    KnobDto(
        angle = 100,
        battery = 70,
        batteryVolts = 4.5,
        calibrated = true,
        calibration = KnobDto.CalibrationDto(
            offAngle = 100,
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

    //4 Off -> High -> Med -> Low
    KnobDto(
        angle = 50,
        battery = 90,
        batteryVolts = 4.5,
        calibrated = true,
        calibration = KnobDto.CalibrationDto(
            offAngle = 90,
            rotationDir = 1,
            zones = listOf(
                KnobDto.CalibrationDto.ZoneDto(
                    highAngle = 40,
                    lowAngle = 80,
                    mediumAngle = 60,
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
    //5
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
                    highAngle = 30,
                    lowAngle = 130,
                    mediumAngle = 170,
                    zoneName = "Single",
                    zoneNumber = 1
                )
            )
        ),
//        highAngle = 40,
//        lowAngle = 120,
//        mediumAngle = 80,

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
*/

