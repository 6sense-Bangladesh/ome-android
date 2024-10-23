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
import com.ome.app.utils.isNotEmpty
import com.ome.app.utils.logi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
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
    var userInfo= savedStateHandle.getStateFlow("userInfo", preferencesProvider.getUserData())
    var knobs= savedStateHandle.getStateFlow("knobs", buildList {
        if(BuildConfig.DEBUG){
            add(
                KnobDto(
                    angle = 126,
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
                    connectStatus = "error",
                    firmwareVersion = "ceteros",
                    gasOrElectric = "graeci",
                    ipAddress = "explicari",
                    lastScheduleCommand = "intellegebat",
                    macAddr = "fake_mac",
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
                )
            )
        }
    })

    override var defaultErrorHandler = CoroutineExceptionHandler { _, _ ->
        startDestinationInitialized.postValue(R.id.launchFragment to null)
    }
    val startDestinationInitialized = SingleLiveEvent<Pair<Int, Bundle?>>()

    var isSplashScreenLoading = true
    var initDone = false
    var startDestinationJob: Job? = null

    var stoveData = StoveRequest()

    init {
        launch(ioContext){
            userRepository.userFlow.collect {
                if(it == null) return@collect
                withContext(mainContext){
                    savedStateHandle["userInfo"] = it
                }
            }
        }
    }

    fun getUserInfo(){
        launch(ioContext){
            userRepository.getUserData()
        }
    }

    fun initStartDestination() {
        if (initDone) return
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
                                            startDestinationInitialized.postValue(R.id.launchFragment to null)
                                        } else {
                                            startDestinationInitialized.postValue(R.id.dashboardFragment to null)
                                        }
                                    }
                                    is ResponseWrapper.Success -> {
                                        initDone = true
//                                        withContext(mainContext){
//                                            savedStateHandle["userInfo"] = result.value
//                                        }
                                        if (result.value.stoveMakeModel.isNullOrEmpty() ||
                                            result.value.stoveGasOrElectric.isNullOrEmpty()
                                        ){
                                            startDestinationInitialized.postValue(R.id.myStoveSetupNavGraph to null)
                                            return@launch
                                        }
                                        val knobs = mutableListOf<com.ome.app.domain.model.network.response.KnobDto>()

                                        try {
                                            knobs.addAll(stoveRepository.getAllKnobs())
                                            withContext(mainContext){
                                                knobs.isNotEmpty {
                                                    savedStateHandle["knobs"] = knobs.toList()
                                                }
                                            }
                                        } catch (ex: Exception) {
                                            knobs.clear()
                                        }
                                        preferencesProvider.getUserId()?.let { userId ->
                                            launch(ioContext) {
                                                knobs.map { knob -> knob.macAddr }.isNotEmpty {macs->
                                                    webSocketManager.initWebSocket(macs, userId)
                                                }

                                            }

                                            launch(ioContext) {
                                                webSocketManager.knobConnectStatusFlow.collect {
                                                    val text = ""
                                                }
                                            }

                                        }

                                        startDestinationInitialized.postValue(R.id.dashboardFragment to null)
                                    }
                                }
                            } else {
                                startDestinationInitialized.postValue(R.id.launchFragment to null)
                            }
                        } else {
                            startDestinationInitialized.postValue(R.id.launchFragment to null)
                        }
                    }
                } else {
                    startDestinationInitialized.postValue(R.id.launchFragment to null)
                }
            } ?: run {
                startDestinationInitialized.postValue(R.id.launchFragment to null)
            }
        }
    }

    fun connectToSocket() = launch(ioContext) {
        val knobs = stoveRepository.getAllKnobs()
        withContext(mainContext){
            savedStateHandle["knobs"] = knobs.toList()
        }
        preferencesProvider.getUserId()?.let {userId->
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
