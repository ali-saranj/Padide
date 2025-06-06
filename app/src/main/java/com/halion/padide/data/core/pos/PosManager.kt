package com.halion.padide.data.core.pos

import PspHasPaperModel
import PspPosinfoModel
import android.util.Log
import com.halion.padide.data.core.WebSocketClient
import com.halion.padide.data.core.pos.models.PosResponse
import kotlinx.serialization.json.Json

class PosManager(val webSocketClient: WebSocketClient, val json: Json) {


    init {
        configSocet()
        webSocketClient.setListener(object : WebSocketClient.SocketListener {
            override fun onMessage(message: String) {
                val posRespons = json.decodeFromString<PosResponse>(message)
                if (posRespons.terminalNumber.isNotBlank())
                    Log.d("test", "terminalNumber: ${posRespons.terminalNumber}")
            }

            override fun onConnected() {
                super.onConnected()
                checkPos()
                chackPaper()
            }
        })
    }

    fun configSocet() {
        webSocketClient.setSocketUrl("ws://127.0.0.1:1372/")
        webSocketClient.connect()
    }

    fun checkPos() {
        webSocketClient.sendMessage(json.encodeToString(PspPosinfoModel()))
    }

    fun chackPaper() {
        webSocketClient.sendMessage(json.encodeToString(PspHasPaperModel()))
    }


}