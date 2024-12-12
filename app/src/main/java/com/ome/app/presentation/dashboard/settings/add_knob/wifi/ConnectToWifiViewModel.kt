package com.ome.app.presentation.dashboard.settings.add_knob.wifi

import androidx.lifecycle.SavedStateHandle
import com.ome.app.R
import com.ome.app.data.ConnectionStatusListener
import com.ome.app.data.local.KnobSocketMessageType
import com.ome.app.data.local.NetworkManager
import com.ome.app.data.local.ResourceProvider
import com.ome.app.data.local.SocketManager
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.utils.isFalse
import com.ome.app.utils.isNotEmpty
import com.ome.app.utils.isTrue
import com.ome.app.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds


@HiltViewModel
class ConnectToWifiViewModel @Inject constructor(
    private val networkManager: NetworkManager,
    private val socketManager: SocketManager,
    private val stoveRepository: StoveRepository,
    private val savedStateHandle: SavedStateHandle,
    private val resourceProvider: ResourceProvider,
    connectionStatusListener: ConnectionStatusListener
) : BaseViewModel() {

    val params by lazy { ConnectToWifiFragmentArgs.fromSavedStateHandle(savedStateHandle).params }

    var connectClicked = false

    val wifiConnectedFlow = MutableSharedFlow<Unit>()

    private var getListTryCount = 0

    init {
        connectionStatusListener.shouldReactOnChanges = false
        setupWifi()
        initListeners()
        launch(ioContext, showLoading = false) {
            socketManager.networksFlow.filterNotNull().collect { list ->
                list.log("wifiNetworksList")
                if (list.isNotEmpty() || getListTryCount > 0) {
                    wifiConnectedFlow.emit(Unit)
                }else{
                    getListTryCount++
                    delay(500)
                    sendMessage(KnobSocketMessageType.GET_NETWORKS)
                }
            }
        }
    }


    private fun setupWifi() {
        params.macAddrs.isNotEmpty {
            networkManager.setup(it)
        }
    }

    private fun initListeners(){
        socketManager.messageReceived = { type, message ->
            if (type == KnobSocketMessageType.GET_MAC) {
                if (message == params.macAddrs) {
                    sendMessage(KnobSocketMessageType.GET_NETWORKS)
                }
            }
        }
        socketManager.onSocketConnect = {
            it.isTrue{
                launch {
                    sendMessage(KnobSocketMessageType.GET_MAC)
                }
            }.isFalse{
                defaultErrorLiveData.postValue(resourceProvider.getString(R.string.something_went_wrong_when_setting_the_knob))
            }
        }
    }

    private fun sendMessage(message: KnobSocketMessageType) = launch(ioContext) {
        socketManager.sendMessage(message)
    }


    fun connectToWifi(){
        connectClicked = false
        launch(ioContext){
            if(params.isEditMode){
                stoveRepository.clearWifi(params.macAddrs)
                delay(6.seconds)
            }
            val result = networkManager.connectToKnobHotspot()
            result.log("connectToWifi")
            //Check whether device connected to wifi or not
            if (result.first) {
                socketManager.connect()
            } else {
                result.second?.also { message ->
                    error(message)
                } ?: connectToWifi()
            }
        }
    }
}
