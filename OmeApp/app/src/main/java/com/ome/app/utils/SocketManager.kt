package com.ome.app.utils

import android.content.Context
import com.ome.app.ui.dashboard.settings.add_knob.wifi.adapter.model.NetworkItemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.net.Socket
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class SocketManager(val context: Context) {

    companion object {
        const val ipAddress = "10.10.0.1"
        const val port = 8
    }

    private var mOut: DataOutputStream? = null
    private var mIn: InputStream? = null

    private val keyHexBytesArray =
        intArrayOf(95, 198, 252, 116, 84, 55, 171, 199, 153, 89, 44, 208, 22, 251, 47, 161)


    private val ivHexToBytesArray =
        intArrayOf(210, 237, 136, 104, 145, 182, 212, 203, 4, 80, 198, 119, 194, 201, 38, 250)

    //testWifi


    var messageReceived: (messageType: KnobSocketMessage, message: String) -> Unit =
        { type, message -> }

    var onSocketConnect: () -> Unit = {}

    private var mRun = false

    val networksFlow: MutableSharedFlow<List<NetworkItemModel>> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    var socket: Socket = Socket()

    var lastMessageSent: KnobSocketMessage = KnobSocketMessage.GET_MAC

    suspend fun sendMessage(
        message: KnobSocketMessage,
        vararg params: String = arrayOf()
    ) = withContext(Dispatchers.IO) {
        var finalMessage = message.path


        if (message == KnobSocketMessage.TEST_WIFI || message == KnobSocketMessage.SET_WIFI) {
            finalMessage += " \"${params[0]}\" \"${params[1]}\" ${params[2]}"
        }

        if (mOut != null) {
            logi("Sending: $finalMessage")
            var data = encrypt(finalMessage)

            if (message == KnobSocketMessage.TEST_WIFI || message == KnobSocketMessage.SET_WIFI) {
                repeat(128 - data.size){
                    data += 0.toByte()
                }
            }
            lastMessageSent = message
            mOut?.write(data)
            mOut?.flush()
        }
    }

    private suspend fun read() {
        val buffer = ByteArrayOutputStream()

        val data = ByteArray(16384)

        var totalBytesRead = 0
        while (totalBytesRead < 2048) {
            mIn?.let {
                val bytesRead = it.read(data, 0, data.size)
                totalBytesRead += bytesRead
                buffer.write(decrypt(data), 0, bytesRead)
            }
        }

        val decryptedMessage = String(buffer.toByteArray().removePadding(), StandardCharsets.UTF_8)
        decryptedMessage.let {
            logi("ResponseFrom: ${lastMessageSent.path} , Message: $it")
            if (lastMessageSent == KnobSocketMessage.GET_NETWORKS) {
                networksFlow.emit(parseNetworkList(it))
            }
            messageReceived(lastMessageSent, it)
        }
        logi("BytesRead: $totalBytesRead")
    }


    private fun parseNetworkList(message: String): List<NetworkItemModel> {
        val networksList = arrayListOf<NetworkItemModel>()
        val list = message.split("#")
        list.forEach { item ->
            if (item.isNotEmpty()) {
                val network =
                    item.substring(33, item.length).replace("\\r", "").replace("\\n", "").trim()
                if (networksList.firstOrNull { networkItem -> networkItem.ssid == network } != null) {
                    return@forEach
                }
                networksList.add(
                    NetworkItemModel(
                        ssid = network,
                        securityType = "WPA2"
                    )
                )
            }
        }
        return networksList
    }

    private fun ByteArray.removePadding(): ByteArray {
        val filteredArray = arrayListOf<Int>()
        run breaking@{
            this.map { it.toInt() }.forEach {
                if (it == 0) return@breaking
                filteredArray.add(it)
            }
        }
        return filteredArray.map { it.toByte() }.toByteArray()
    }


    private fun encrypt(message: String): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding")
        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(keyHexBytesArray.map { it.toByte() }.toByteArray(), "AES"),
            IvParameterSpec(ivHexToBytesArray.map { it.toByte() }.toByteArray())
        )

        return cipher.doFinal(message.toByteArray())
    }

    private fun decrypt(encrypted: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding", BouncyCastleProvider())
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(keyHexBytesArray.map { it.toByte() }.toByteArray(), "AES"),
            IvParameterSpec(ivHexToBytesArray.map { it.toByte() }.toByteArray())
        )

        return cipher.doFinal(encrypted)
    }


    suspend fun connect() = withContext(Dispatchers.IO) {
        if (!socket.isConnected) {
            socket = Socket(ipAddress, port)
            mRun = true
            mOut = DataOutputStream(socket.getOutputStream())
            mIn = DataInputStream(socket.getInputStream())
        }
        onSocketConnect()
        while (mRun) {
            read()
        }
    }

    fun stopClient() {
        mRun = false
        mOut?.flush()
        mIn?.close()
        mIn = null
        mOut = null
        socket.close()
    }

}

enum class KnobSocketMessage(val path: String) {
    GET_MAC("getmac"),
    WIFI_STATUS("getwifistatus"),
    TEST_WIFI("testwifi"),
    SET_WIFI("setwifi"),
    REBOOT("reboot"),
    GET_NETWORKS("getap \"\""),
    RESEND_SET_WIFI(""),
    RESEND_REBOOT(""),
}
