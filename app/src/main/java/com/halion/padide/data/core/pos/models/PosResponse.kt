package com.halion.padide.data.core.pos.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PosResponse(
    @SerialName("amount")
    val amount: String,
    @SerialName("amountAffective")
    val amountAffective: String,
    @SerialName("barcode")
    val barcode: String,
    @SerialName("cardNumber")
    val cardNumber: String,
    @SerialName("checkPaper")
    val checkPaper: String,
    @SerialName("dateTime")
    val dateTime: String,
    @SerialName("error")
    val error: String,
    @SerialName("getInfo")
    val getInfo: String,
    @SerialName("MI")
    val mI: String,
    @SerialName("notSentAdviceRefNum")
    val notSentAdviceRefNum: String,
    @SerialName("printStatus")
    val printStatus: String,
    @SerialName("RC")
    val rC: String,
    @SerialName("RN")
    val rN: String,
    @SerialName("referenceNumber")
    val referenceNumber: String,
    @SerialName("responseCode")
    val responseCode: String,
    @SerialName("SC")
    val sC: String,
    @SerialName("terminalNumber")
    val terminalNumber: String,
    @SerialName("timeOut")
    val timeOut: String,
    @SerialName("transactionFailed")
    val transactionFailed: String,
    @SerialName("transactionSuccessful")
    val transactionSuccessful: String,
    @SerialName("trxType")
    val trxType: String
)