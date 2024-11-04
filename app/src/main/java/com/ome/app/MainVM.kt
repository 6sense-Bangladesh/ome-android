package com.ome.app

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.model.network.request.StoveRequest
import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.domain.model.network.response.UserResponse
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.utils.WifiHandler
import com.ome.app.utils.isFalse
import com.ome.app.utils.isNotEmpty
import com.ome.app.utils.logi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject


@HiltViewModel
class MainVM @Inject constructor(
    private val amplifyManager: AmplifyManager,
    private val preferencesProvider: PreferencesProvider,
    private val userRepository: UserRepository,
    private val stoveRepository: StoveRepository,
    private val wifiHandler: WifiHandler,
    private val webSocketManager: WebSocketManager,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    var userInfo = savedStateHandle.getStateFlow("userInfo", preferencesProvider.getUserData())
    var knobs = savedStateHandle.getStateFlow("knobs", buildList<KnobDto> {
        if (BuildConfig.DEBUG) addAll(dummyKnobs)
    })

    override var defaultErrorHandler = CoroutineExceptionHandler { _, _ ->
        startDestinationInitialized.postValue(R.id.launchFragment to null)
//        savedStateHandle["startDestination"] = R.id.launchFragment
    }
    val startDestinationInitialized = SingleLiveEvent<Pair<Int, Bundle?>>()
    val startDestination = savedStateHandle.getStateFlow<Int?>("startDestination", null)

    var isSplashScreenLoading = true
    var startDestinationJob: Job? = null

    var stoveData = StoveRequest()

    var selectedBurnerIndex: Int? = null

    init {
        launch(ioContext) {
            userRepository.userFlow.filterNotNull().collect {
                savedStateHandle["userInfo"] = it
            }
        }
    }

    fun getUserInfo() {
        launch(ioContext) {
            userRepository.getUserData()
        }
    }

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
                                        preferencesProvider.saveUserId(attr.value)
                                        logi("userId: $attr.value")
                                    }
                                }
                                preferencesProvider.saveAccessToken(accessToken)

                                when (val result = userRepository.getUserData()) {
                                    is ResponseWrapper.Error -> {
                                        if (result.message.contains("Not found")) {
                                            amplifyManager.deleteUser()
                                            preferencesProvider.clearData()
                                            savedStateHandle["startDestination"] =
                                                R.id.launchFragment
                                        } else {
                                            savedStateHandle["startDestination"] =
                                                R.id.dashboardFragment
                                        }
                                    }

                                    is ResponseWrapper.Success -> {
//                                        initDone = true
//                                        withContext(mainContext){
//                                            savedStateHandle["userInfo"] = result.value
//                                        }
                                        if (result.value.stoveSetupComplete.isFalse()) {
                                            savedStateHandle["startDestination"] =
                                                R.id.myStoveSetupNavGraph
                                            return@launch
                                        }
                                        savedStateHandle["startDestination"] =
                                            R.id.dashboardFragment
                                        val knobs = mutableListOf<KnobDto>()

                                        launch(ioContext) {

                                        }

                                        try {
                                            if (BuildConfig.DEBUG) knobs.addAll(dummyKnobs)
                                            knobs.addAll(stoveRepository.getAllKnobs())
                                            knobs.isNotEmpty {
                                                savedStateHandle["knobs"] = knobs.toList()
                                            }
                                        } catch (ex: Exception) {
                                            knobs.clear()
                                        }
                                        preferencesProvider.getUserId()?.let { userId ->
                                            launch(ioContext) {
                                                knobs.map { knob -> knob.macAddr }
                                                    .isNotEmpty { macs ->
                                                        webSocketManager.initWebSocket(macs, userId)
                                                    }

                                            }

                                            launch(ioContext) {
                                                webSocketManager.knobConnectStatusFlow.collect {
                                                    val text = ""
                                                }
                                            }

                                        }
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

    fun connectToSocket() = launch(ioContext) {
        val knobs = stoveRepository.getAllKnobs()
        savedStateHandle["knobs"] = knobs.toList()
        preferencesProvider.getUserId()?.let { userId ->
            knobs.map { it.macAddr }.isNotEmpty {
                webSocketManager.initWebSocket(it, userId)
            }
        }
    }

    fun signOut(onEnd: () -> Unit) {
        launch(ioContext) {
            amplifyManager.signUserOut()
            preferencesProvider.clearData()
            userRepository.userFlow.emit(null)
            savedStateHandle.remove<UserResponse>("userInfo")
            amplifyManager.signOutFlow.emit(true)
            withContext(mainContext) {
                onEnd()
            }
        }
    }
}

val dummyKnobs = listOf(
    KnobDto(
        angle = 0,
        battery = 30,
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
        connectStatus = "offline",
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
        angle = 0,
        battery = 70,
        batteryVolts = 4.5,
        calibrated = null,
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
        connectStatus = "charging",
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
    )
)

