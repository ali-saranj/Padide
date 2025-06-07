package com.halion.padide.data.core.pos

import PspHasPaperModel
import PspPosinfoModel
import PspPrintString
import android.util.Log
import com.halion.padide.data.core.WebSocketClient
import com.halion.padide.data.core.pos.models.PosResponse
import kotlinx.serialization.json.Json

class PosManager(
    val webSocketClient: WebSocketClient,
    val json: Json,
) {
    lateinit var onCallBack: (PosResponse) -> Unit

    var listOnCallBack: ArrayList<(PosResponse) -> Unit> = ArrayList()

    
    init {
        configSocet()
        webSocketClient.setListener(object : WebSocketClient.SocketListener {
            override fun onMessage(message: String) {
                val posRespons = json.decodeFromString<PosResponse>(message)
                listOnCallBack.toList().first().invoke(posRespons)
                listOnCallBack.removeAt(0)
            }

            override fun onConnected() {
                super.onConnected()
                checkPos {
                    Log.d("test", "terminalNumber:${it.terminalNumber} ")
                }
                checkPaper {
                    Log.d("test", "massage:${it.getInfo} ")
                }
            }
        })
    }

    fun configSocet() {
        webSocketClient.setSocketUrl("ws://127.0.0.1:1372/")
        webSocketClient.connect()
    }

    fun checkPos(onCallBack: (PosResponse) -> Unit) {
        listOnCallBack.add(onCallBack)
        webSocketClient.sendMessage(json.encodeToString(PspPosinfoModel()))
    }

    fun checkPaper(onCallBack: (PosResponse) -> Unit) {
        listOnCallBack.add(onCallBack)
        webSocketClient.sendMessage(json.encodeToString(PspHasPaperModel()))
    }

    fun printString(text: String, onCallBack: (PosResponse) -> Unit) {
        listOnCallBack.add(onCallBack)
        webSocketClient.sendMessage(json.encodeToString(PspPrintString(text = text)))
    }


}