package com.halion.padide.data.core

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class WebSocketClient private constructor() {

    private var webSocket: WebSocket? = null
    private var socketListener: SocketListener? = null
    private var socketUrl: String = ""
    private var shouldReconnect = true
    private val reconnectDelay = 3000L
    private val handler = Handler(Looper.getMainLooper())
    private var isReconnecting = false

    private val client: OkHttpClient = OkHttpClient.Builder()
        .pingInterval(15, TimeUnit.SECONDS) // optional: keep-alive
        .build()

    companion object {
        val instance: WebSocketClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { WebSocketClient() }
        const val TAG = "WebSocketClient"
    }

    fun setListener(listener: SocketListener) {
        this.socketListener = listener
    }

    fun setSocketUrl(url: String) {
        this.socketUrl = url
    }

    fun connect() {
        if (socketUrl.isBlank()) {
            Log.e(TAG, "Socket URL is not set!")
            return
        }

        shouldReconnect = true
        createWebSocket()
    }

    fun disconnect() {
        shouldReconnect = false
        isReconnecting = false
        webSocket?.close(1000, "Client disconnected")
        webSocket = null
        Log.i(TAG, "WebSocket manually disconnected.")
    }


    // interface
    interface SendMassage {
        fun callback(result: Boolean)
    }

    fun sendMessage(message: String, callback: SendMassage) {
        if (webSocket != null) {
            val result = webSocket!!.send(message)
            Log.d(TAG, "WebSocket message sent: $message")
        } else {
            Log.w(TAG, "WebSocket not connected, message not sent.")
        }
    }

    fun sendMessage(message: String) {
        if (webSocket != null) {
            val result = webSocket!!.send(message)
            Log.d(TAG, "WebSocket message sent: $message")
            if (!result) {
                Log.w(TAG, "WebSocket message failed to send.")
            }
        } else {
            Log.w(TAG, "WebSocket not connected, message not sent.")
        }
    }

    private fun createWebSocket() {
        Log.i(TAG, "Creating WebSocket connection to $socketUrl")
        val request = Request.Builder().url(socketUrl).build()
        webSocket = client.newWebSocket(request, socketListenerImpl)
    }

    private fun scheduleReconnect() {
        if (isReconnecting) return
        isReconnecting = true

        handler.postDelayed({
            if (shouldReconnect) {
                Log.i(TAG, "Attempting to reconnect WebSocket...")
                createWebSocket()
                isReconnecting = false
            }
        }, reconnectDelay)
    }

    private val socketListenerImpl = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.i(TAG, "WebSocket connected.")
            socketListener?.onConnected()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "WebSocket message received: $text")
            socketListener?.onMessage(text)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.w(TAG, "WebSocket closing: $code / $reason")
            socketListener?.onClosing(code, reason)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.i(TAG, "WebSocket closed: $code / $reason")
            socketListener?.onDisconnected()
            if (shouldReconnect) scheduleReconnect()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.e(TAG, "WebSocket error: ${t.message}", t)
            socketListener?.onError(t)
            if (shouldReconnect) scheduleReconnect()
        }
    }

    interface SocketListener {
        fun onConnected() {}
        fun onDisconnected() {}
        fun onClosing(code: Int, reason: String) {}
        fun onMessage(message: String)
        fun onError(t: Throwable) {}
    }

}