package com.ome.app.data.local

import android.content.Context
import com.ome.app.presentation.dashboard.settings.add_knob.wifi.adapter.model.NetworkItemModel
import com.ome.app.utils.isNotEmpty
import com.ome.app.utils.log
import com.ome.app.utils.loge
import com.ome.app.utils.logi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import okhttp3.ResponseBody
import org.bouncycastle.jce.provider.BouncyCastleProvider
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.net.ConnectException
import java.net.Socket
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class SocketManager(
    val context: Context,
    retrofit: Retrofit
) {
    interface SocketService{
        @GET("http://10.10.0.1/start_scan.cgi")
        suspend fun scanForNetworks(): Response<ResponseBody>
    }

    private val socketService: SocketService = retrofit.create(SocketService::class.java)

    companion object {
        private const val KNOB_IP_ADDRESS = "10.10.0.1"
        private const val KNOB_PORT = 8
    }

    private var mOut: DataOutputStream? = null
    private var mIn: InputStream? = null

    private val keyHexBytesArray =
        intArrayOf(95, 198, 252, 116, 84, 55, 171, 199, 153, 89, 44, 208, 22, 251, 47, 161)


    private val ivHexToBytesArray =
        intArrayOf(210, 237, 136, 104, 145, 182, 212, 203, 4, 80, 198, 119, 194, 201, 38, 250)


    var messageReceived: suspend (messageType: KnobSocketMessageType, message: String) -> Unit = { _, _ -> }

    var onSocketConnect: () -> Unit = {}

    private var mRun = false

    val networksFlow = MutableStateFlow<List<NetworkItemModel>?>(null)

    private var socket: Socket = Socket()

    private var lastMessageSent: KnobSocketMessageType = KnobSocketMessageType.GET_MAC

    suspend fun sendMessage(
        message: KnobSocketMessageType,
        vararg params: String = arrayOf()
    ){
        withContext(Dispatchers.IO) {
//        if (lastMessageSent == KnobSocketMessage.GET_NETWORKS2) {
//            scanForNetworks()
//            return@withContext
//        }
            try {
                // Prepare the message
                var finalMessage = message.path
                if (message == KnobSocketMessageType.TEST_WIFI || message == KnobSocketMessageType.SET_WIFI) {
                    finalMessage += " \"${params[0]}\" \"${params[1]}\" ${params[2]}"
                }

                // Send the message
                if (mOut != null) {
                    logi("Sending: $finalMessage")
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
                }
            } catch (e: Exception) {
                loge("SocketException encountered: ${e.message}. Attempting to reconnect...")
                reconnectSocket()  // Attempt to reconnect
                sendMessage(message, *params)  // Retry sending the message after reconnecting
            }
        }
    }


    private suspend fun read() = withContext(Dispatchers.IO){
        val buffer = ByteArrayOutputStream()

        val data = ByteArray(16384)

        var totalBytesRead = 0
        while (totalBytesRead < 2048) {
            yield()
            val bytesRead = mIn?.read(data, 0, data.size) ?: break  // Break if mIn is null
            if (bytesRead == -1) {
                loge("End of stream reached. Attempting to reconnect...")
                reconnectSocket()  // Attempt to reconnect if the stream ends
                return@withContext
            }
            totalBytesRead += bytesRead
            yield()
            buffer.write(decrypt(data.copyOfRange(0, bytesRead)))
        }

        val decryptedMessage = String(buffer.toByteArray().removePadding(), StandardCharsets.UTF_8)
        decryptedMessage.let {
            logi("ResponseFrom: ${lastMessageSent.path} , Message: $it")
            if (lastMessageSent == KnobSocketMessageType.GET_NETWORKS)
                networksFlow.value = parseNetworkList(it)
            messageReceived(lastMessageSent, it)
        }
        logi("BytesRead: $totalBytesRead")
    }


    private fun parseNetworkList(message: String): List<NetworkItemModel> {
        val networksList = arrayListOf<NetworkItemModel>()
        val regex = Regex("""\d+\s+\d+\s+-\d+\s+([A-F0-9:]{17})\s+\d+\s+([\w\s]+)""")
        regex.findAll(message).forEach { matchResult ->
            matchResult.groupValues.getOrNull(2)?.trim()?.let {
                it.isNotEmpty { ssid ->
                    networksList.add(NetworkItemModel(ssid = ssid, securityType = "WPA2"))
                }
            }
        }
        networksList.log("wifiNetworksList")
//        val list = message.split("#")
//        list.forEach { item ->
//            item.log("scanForNetworks2")
//            if (item.isNotEmpty()) {
//
//                val network =
//                    item.substring(33, item.length).replace("\\r", "").replace("\\n", "").trim()
//                if (networksList.firstOrNull { networkItem -> networkItem.ssid == network } != null) {
//                    return@forEach
//                }
//                networksList.add(
//                    NetworkItemModel(
//                        ssid = network,
//                        securityType = "WPA2"
//                    )
//                )
//            }
//        }
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

    private suspend fun scanForNetworks(): List<NetworkItemModel> {
        val networksList = mutableListOf<NetworkItemModel>()
        try {
            val response = socketService.scanForNetworks()
            if(response.isSuccessful){

                // Parse response body
                val data = response.body()?.string()
                data.log("scanForNetworks")
                if (data != null) {
                    val strEntries = data.split(Regex("[{}]"))
                    for ((index, entry) in strEntries.withIndex()) {
                        if (index % 2 != 0) {
                            val entries = entry.split("\"")
                            networksList.add(NetworkItemModel(
                                ssid = entries[3],
                                securityType = entries[11]
                            ))
                        }
                    }
                    networksFlow.emit(networksList.toList())
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return networksList
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

    private suspend fun reconnectSocket(retryCount: Int = 3, delayMillis: Long = 2000): Unit = withContext(Dispatchers.IO) {
        var attempts = 0
        var connected = false

        while (attempts < retryCount && !connected) {
            stopClient()
            try {
                // Attempt to reconnect
                socket = Socket(KNOB_IP_ADDRESS, KNOB_PORT)
                socket.soTimeout = 5000  // Read timeout
                mOut = DataOutputStream(socket.getOutputStream())
                mIn = DataInputStream(socket.getInputStream())
                onSocketConnect()  // Call this to handle any setup needed on connect
                mRun = true
                connected = true
                logi("Reconnected successfully on attempt ${attempts + 1}")

                // Start reading again
                while (mRun) {
                    yield()
                    read()
                }
            } catch (e: Exception) {
                attempts++
                loge("Reconnect attempt $attempts failed: ${e.message}")
                delay(delayMillis)  // Wait before the next attempt
            }
        }

        if (!connected) {
            loge("Failed to reconnect after $retryCount attempts.")
            logi("ResponseFrom: ${lastMessageSent.path} , Message: failed")
            throw ConnectException("Unable to reconnect to socket after multiple attempts.")
        }
    }



    suspend fun connect() = withContext(Dispatchers.IO) {
        stopClient()
        try {
            socket = Socket(KNOB_IP_ADDRESS, KNOB_PORT)
            socket.soTimeout = 5000  // Read timeout
            mRun = true
            mOut = DataOutputStream(socket.getOutputStream())
            mIn = DataInputStream(socket.getInputStream())
            onSocketConnect()
            while (mRun) {
                yield()
                read()
            }
        } catch (e: Exception) {
            // Handle the connection error
            reconnectSocket()  // Attempt to reconnect
//            throw ConnectException("Error with socket connection.")
        }
    }

    private fun stopClient() {
        mRun = false
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
    GET_MAC("getmac"),
    WIFI_STATUS("getwifistatus"),
    TEST_WIFI("testwifi"),
    SET_WIFI("setwifi"),
    REBOOT("reboot"),
    GET_NETWORKS("getap \"\"")
}
