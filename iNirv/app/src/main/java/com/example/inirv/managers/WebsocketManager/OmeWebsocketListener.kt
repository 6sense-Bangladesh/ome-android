package com.example.inirv.managers.WebsocketManager

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

interface OmeWebsocketListenerDelegate{
    fun receivedWebsocketMsg(responseText: String = "", responseBytes: ByteString?)
    fun websocketClosing(code: Int, reason: String)
    fun websocketFailed(throwable: Throwable, response: Response?)
}

class OmeWebsocketListener(delegate: OmeWebsocketListenerDelegate): WebSocketListener() {

    var delegate: OmeWebsocketListenerDelegate  // Delegate we'll be sending information to

    init {
        this.delegate = delegate
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
//        webSocket.send("hello world")
//        delegate.receivedWebsocketMsg("Opened connection to websocket", null)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
//        println("onMessage text: $text")
        delegate.receivedWebsocketMsg(text, null)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
//        println("onMessage bytes: $bytes")
        delegate.receivedWebsocketMsg(responseBytes =  bytes)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
//        println("onlosing closing WebSocket")
        delegate.websocketClosing(code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
//        println("onFailure ${t.message}")
        delegate.websocketFailed(t, response)
    }
}