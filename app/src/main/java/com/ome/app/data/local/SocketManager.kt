package com.ome.app.data.local

import android.content.Context
import com.ome.app.presentation.dashboard.settings.add_knob.wifi.adapter.model.NetworkItemModel
import com.ome.app.utils.isTrue
import com.ome.app.utils.log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.*
import java.net.Socket
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.minutes


class SocketManager(val context: Context) {

    companion object {
        private const val KNOB_IP_ADDRESS = "10.10.0.1"
        private const val KNOB_PORT = 8
        private const val TAG = "KnobSocketLocal"
    }

    private var mOut: DataOutputStream? = null
    private var mIn: InputStream? = null

    private val keyHexBytesArray = byteArrayOf(
        95, -58, -4, 116, 84, 55, -85, -57, -103, 89, 44, -48, 22, -5, 47, -95
    )

    private val ivHexToBytesArray = byteArrayOf(
        -46, -19, -120, 104, -111, -74, -44, -53, 4, 80, -58, 119, -62, -55, 38, -6
    )


    var messageReceived: (messageType: KnobSocketMessageType, message: String) -> Unit = { _, _ -> }

    var onSocketConnect: (Boolean) -> Unit = {}

    var isConnected = false

    val networksFlow = MutableStateFlow<List<NetworkItemModel>?>(null)

    private var socket: Socket = Socket()

    private var lastMessageSent: KnobSocketMessageType = KnobSocketMessageType.GET_MAC

    suspend fun sendMessage(
        message: KnobSocketMessageType,
        vararg params: String = arrayOf()
    ){
        withContext(coroutineContext) {
            try {
                // Prepare the message
                var finalMessage = message.path
                if (message == KnobSocketMessageType.TEST_WIFI || message == KnobSocketMessageType.SET_WIFI) {
                    finalMessage += " \"${params[0]}\" \"${params[1]}\" ${params[2]}"
                }
                "Sending: $finalMessage, isConnected: $isConnected".log(TAG)
                // Send the message
                if (mOut != null && isConnected) {
                    var data = encrypt(finalMessage)

                    // Pad data if needed
                    if (message == KnobSocketMessageType.TEST_WIFI || message == KnobSocketMessageType.SET_WIFI) {
                        repeat(128 - data.size) {
                            data += 0.toByte()
                        }
                    }
                    mOut?.write(data)
                    mOut?.flush()
                    lastMessageSent = message
                    if(message == KnobSocketMessageType.REBOOT)
                        stopClient()
                } else onSocketConnect(false)
            //else reconnectSocket()
            } catch (e: Exception) {
                isConnected = false
                "SocketException encountered: ${e.message}".log(TAG)
                reconnectSocket()  // Attempt to reconnect
//                sendMessage(message, *params)  // Retry sending the message after reconnecting
            }
        }
    }


    private fun read() {
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
            "ResponseFrom: ${lastMessageSent.path} , Message: $it".log(TAG)
            if (lastMessageSent == KnobSocketMessageType.GET_NETWORKS) {
                networksFlow.value = parseNetworkList(it)
            }
            messageReceived(lastMessageSent, it)
        }
    }


    private fun parseNetworkList(message: String): List<NetworkItemModel> {
        val networksList = arrayListOf<NetworkItemModel>()
        val lines = message.split("#")  // Split the message into lines
        lines.forEach {
            val parts = it.trim().replace("  ", " ").split(" ")  // Split each line into parts

            // Check if the line contains valid information
            if (parts.size >= 5) {
                val ssid = parts.drop(5).joinToString(" ")  // Join the parts from the 5th element onward to get the SSID
                if (ssid.isNotEmpty()) {  // Only add non-empty SSIDs that are not just "1"
                    networksList.add(NetworkItemModel(ssid = ssid, securityType = "WPA2"))
                }
            }
        }
        networksList.log("$TAG - wifiNetworksList")
        return networksList.distinctBy { it.ssid }
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
            SecretKeySpec(keyHexBytesArray, "AES"),
            IvParameterSpec(ivHexToBytesArray)
        )

        return cipher.doFinal(message.toByteArray())
    }

    private fun decrypt(encrypted: ByteArray): ByteArray {
        val cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding", BouncyCastleProvider())
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(keyHexBytesArray, "AES"),
            IvParameterSpec(ivHexToBytesArray)
        )

        return cipher.doFinal(encrypted)
    }

    private fun reconnectSocket(retryCount: Int = 2, initialDelayMillis: Long = 3000){
        CoroutineScope(Dispatchers.IO).launch {
            var attempts = 0
            var delayMillis = initialDelayMillis

            while (attempts < retryCount && !isConnected) {
                stopClient()
                try {
                    // Attempt to reconnect
                    socket = Socket(KNOB_IP_ADDRESS, KNOB_PORT)
                    mOut = DataOutputStream(socket.getOutputStream())
                    mIn = DataInputStream(socket.getInputStream())
                    onSocketConnect(true)  // Call this to handle any setup needed on connect
                    isConnected = true
                    "Reconnected successfully on attempt ${attempts + 1}".log(TAG)
//                    sendMessage(lastMessageSent)

                    // Start reading again
                    while (isConnected) {
                        read()
                    }
                } catch (e: Exception) {
                    attempts++
                    isConnected = false
                    if(lastMessageSent == KnobSocketMessageType.REBOOT) return@launch
                    "Reconnect attempt $attempts failed: ${e.message}".log(TAG)
                    if (!isConnected && attempts == retryCount)
                        onSocketConnect(false)
                    if(e.message?.contains("reset").isTrue() || e.message?.contains("closed").isTrue()) {
                        messageReceived(lastMessageSent, "reset")
                        stopClient()
                        break
                    }
                    delay(delayMillis)  // Wait before the next attempt
                    delayMillis = minOf(delayMillis * 2, 1.minutes.inWholeMilliseconds) // Exponential backoff with a maximum delay
                }
            }
        }
    }



    fun connect(){
        "Connect to socket, isConnected: $isConnected".log(TAG)
        if(!isConnected || socket.isClosed) {
            stopClient()
            try {
                socket = Socket(KNOB_IP_ADDRESS, KNOB_PORT)
                isConnected = true
                lastMessageSent = KnobSocketMessageType.NONE
                mOut = DataOutputStream(socket.getOutputStream())
                mIn = DataInputStream(socket.getInputStream())
                onSocketConnect(true)
                "Socket Connected".log(TAG)
                while (isConnected) {
                    read()
                }
            } catch (e: Exception) {
                isConnected = false
                if(lastMessageSent == KnobSocketMessageType.REBOOT) return
                e.log(TAG + "connect")
                stopClient()
                // Handle the connection error
                reconnectSocket()  // Attempt to reconnect
//            throw ConnectException("Error with socket connection.")
            }
        }

    }

    private fun stopClient() {
        "Stopping client".log(TAG)
        isConnected = false
        networksFlow.value = null
        try {
            mOut?.flush()
            mOut?.close()
            mIn?.close()
            socket.close()
        }finally {
            mIn = null
            mOut = null
        }
    }

}

enum class KnobSocketMessageType(val path: String) {
    NONE(""),
    GET_MAC("getmac"),
    WIFI_STATUS("getwifistatus"),
    TEST_WIFI("testwifi"),
    SET_WIFI("setwifi"),
    REBOOT("reboot"),
    GET_NETWORKS("getap \"\"")
}

fun main() {
    //demo
    fun parseNetworkList(message: String): List<NetworkItemModel> {
        val networksList = arrayListOf<NetworkItemModel>()

        val lines = message.split("#")  // Split the message into lines
        lines.forEach {
            val parts = it.trim().replace("  ", " ").split(" ")  // Split each line into parts

            // Check if the line contains valid information
            if (parts.size >= 5) {
                val ssid = parts.drop(5).joinToString(" ")  // Join the parts from the 5th element onward to get the SSID
                if (ssid.isNotEmpty()) {  // Only add non-empty SSIDs that are not just "1"
                    networksList.add(NetworkItemModel(ssid = ssid, securityType = "WPA2"))
                }
            }
        }

        return networksList.distinctBy { it.ssid }  // Return distinct SSIDs
    }

    val data = "#  1  1  -58 50:2C:C6:68:FD:B0 1  c668fdb0\n" +
            "#  4  6  -50 84:D8:1B:C6:24:8E 1  6sense-3\n" +
            "#  7  6  -53 8A:D8:1B:C6:24:8E 1  \n" +
            "#  8 11  -74 50:C7:BF:7B:16:90 1  6sense - 4\n" +
            "# 10 11  -88 8C:DE:F9:77:E1:71 1  phenix it\n" +
            "# 11  2  -54 C0:C9:E3:82:96:7A 1  6sense - 2\n"

    println(parseNetworkList(data))
}
