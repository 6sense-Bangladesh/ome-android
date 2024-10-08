package com.ome.app

import android.os.Bundle
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.ome.Ome.R
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.data.local.PreferencesProvider
import com.ome.app.data.remote.AmplifyManager
import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.data.remote.websocket.WebSocketManager
import com.ome.app.model.base.ResponseWrapper
import com.ome.app.model.network.response.KnobDto
import com.ome.app.utils.WifiHandler
import com.ome.app.utils.logi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class MainVM @Inject constructor(
    val amplifyManager: AmplifyManager,
    val preferencesProvider: PreferencesProvider,
    val userRepository: UserRepository,
    val stoveRepository: StoveRepository,
    val wifiHandler: WifiHandler,
    val webSocketManager: WebSocketManager
) : BaseViewModel() {


    override var defaultErrorHandler = CoroutineExceptionHandler { _, throwable ->
        startDestinationInitialized.postValue(R.id.launchFragment to null)
    }
    val startDestinationInitialized = SingleLiveEvent<Pair<Int, Bundle?>?>()

    val _isSplashScreenLoading = MutableStateFlow(true)
    val isSplashScreenLoading = _isSplashScreenLoading.asStateFlow()
    var startDestinationJob: Job? = null

    fun initStartDestination() {
        startDestinationJob = launch(dispatcher = ioContext) {
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
                                    is ResponseWrapper.NetworkError -> {
                                        val text = ""
                                    }
                                    is ResponseWrapper.GenericError -> {
                                        result.response?.message?.let { message ->
                                            if (message.contains("Not found")) {
                                                amplifyManager.deleteUser()
                                                preferencesProvider.clearData()
                                                startDestinationInitialized.postValue(R.id.launchFragment to null)
                                            } else {
                                                startDestinationInitialized.postValue(R.id.dashboardFragment to null)
                                            }
                                        } ?: run {
                                            startDestinationInitialized.postValue(R.id.launchFragment to null)
                                        }
                                    }
                                    is ResponseWrapper.Success -> {
                                        //  if (result.value.knobMacAddrs.isNotEmpty()) {

                                        val knobs = arrayListOf<KnobDto>()

                                        try {
                                            knobs.addAll(stoveRepository.getAllKnobs())
                                        } catch (ex: Exception) {
                                            knobs.clear()
                                        }
                                        preferencesProvider.getUserId()?.let { userId ->
                                            launch(dispatcher = ioContext) {
                                                webSocketManager.initWebSocket(
                                                    knobs.map { knob -> knob.macAddr },
                                                    userId
                                                )
                                            }

                                            launch(dispatcher = ioContext) {
                                                webSocketManager.knobConnectStatusFlow.collect {
                                                    val text = ""
                                                }
                                            }

                                        }

                                        startDestinationInitialized.postValue(R.id.dashboardFragment to null)
                                    }
                                    else -> {}
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

    fun connectToSocket() = launch(dispatcher = ioContext) {
        val macAddrs = stoveRepository.getAllKnobs()
        preferencesProvider.getUserId()?.let {
            webSocketManager.initWebSocket(macAddrs.map { it.macAddr }, it)
        }
    }
}