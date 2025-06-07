import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PspPosinfoModel(
    @SerialName("CompanyName") val companyName: String = "SEP",
    @SerialName("RequestType") val requestType: String = "getMerchantInfo",
)

@Serializable
data class PspHasPaperModel(
    @SerialName("CompanyName") val companyName: String = "SEP",
    @SerialName("RequestType") val requestType: String = "checkPaper"
)

@Serializable
data class PspPaymentModel(
    @SerialName("CompanyName") val companyName: String = "SEP",
    @SerialName("RequestType") val requestType: String = "Sale",
    @SerialName("AM") val amount: String = "2000",
    @SerialName("paymentType") val paymentType: String = "CARD",
    @SerialName("GovID") val govId: String,
    @SerialName("SC") val sc: String,
    @SerialName("ID") val id: String
)

@Serializable
data class PspReceiptBitMapModel(
    @SerialName("CompanyName") val companyName: String = "SEP",
    @SerialName("RequestType") val requestType: String = "receiptBitMap",
    @SerialName("invoice") val invoice: InvoiceModel? = null
)

@Serializable
data class InvoiceModel(
    @SerialName("num") val num: Long = 0,
    @SerialName("price") val price: Long = 0,
    @SerialName("sumpty") val sumpty: Long = 0,
    @SerialName("sumPrice") val sumPrice: Long = 0,
    @SerialName("discountAmount") val discountAmount: Long = 0,
    @SerialName("serviceAmount") val serviceAmount: Long = 0,
    @SerialName("taxAmount") val taxAmount: Long = 0,
    @SerialName("phoneNumber") val phoneNumber: String,
    @SerialName("description") val description: String,
    @SerialName("tableName") val tableName: String,
    @SerialName("customerName") val customerName: String,
    @SerialName("waiterName") val waiterName: String,
    @SerialName("guestQty") val guestQty: Long = 0,
    @SerialName("creationTime") val creationTime: String,
    @SerialName("farsibate") val farsibate: String,
    @SerialName("creditPrice") val creditPrice: Long = 0,
    @SerialName("restaurantName") val restaurantName: String,
    @SerialName("roundedAmount") val roundedAmount: Long = 0,
    @SerialName("invoiceDetails") val invoiceDetails: List<InvoiceDetail> = emptyList()
)

@Serializable
data class InvoiceDetail(
    @SerialName("sumUnitPrice") val sumUnitPrice: Long = 0,
    @SerialName("name") val name: String,
    @SerialName("qty") val qty: Long = 0,
    @SerialName("toz") val toz: String
)

@Serializable
data class PspDoVerify(
    @SerialName("CompanyName") val companyName: String = "SEP",
    @SerialName("RequestType") val requestType: String = "VerifyTransaction",
    @SerialName("RefNum") val refNum: String,
    @SerialName("AppID") val appId: String = "1"
)

@Serializable
data class PspDoReverse(
    @SerialName("CompanyName") val companyName: String = "SEP",
    @SerialName("RequestType") val requestType: String = "ReverseTransaction",
    @SerialName("RefNum") val refNum: String,
    @SerialName("AppID") val appId: String = "1"
)

@Serializable
data class PspDoBalance(
    @SerialName("CompanyName") val companyName: String = "SEP",
    @SerialName("RequestType") val requestType: String = "Balance",
    @SerialName("AppID") val appId: String = "1"
)

@Serializable
data class PspDoChargeTopUp(
    @SerialName("CompanyName") val companyName: String = "SEP",
    @SerialName("RequestType") val requestType: String = "ChargeTopUp",
    @SerialName("AppID") val appId: String = "1"
)

@Serializable
data class PspDoChargePin(
    @SerialName("CompanyName") val companyName: String = "SEP",
    @SerialName("RequestType") val requestType: String = "ChargePin",
    @SerialName("AppID") val appId: String = "1"
)

@Serializable
data class PspBillPayment(
    @SerialName("CompanyName") val companyName: String = "SEP",
    @SerialName("RequestType") val requestType: String = "BillPayment",
    @SerialName("BillId") val billId: String,
    @SerialName("PayId") val payId: String,
    @SerialName("AppID") val appId: String = "2"
)

@Serializable
data class PspSaleshenase(
    @SerialName("CompanyName") val companyName: String = "SEP",
    @SerialName("RequestType") val requestType: String = "Saleshenase",
    @SerialName("Shenase") val shenase: String,
    @SerialName("AM") val amount: String,
    @SerialName("AppID") val appId: String = "2"
)

@Serializable
data class PspPrintRefNum(
    @SerialName("CompanyName") val companyName: String = "SEP",
    @SerialName("RequestType") val requestType: String = "PrintReferenceNumber",
    @SerialName("RefNum") val refNum: String,
    @SerialName("AppID") val appId: String = "2"
)

@Serializable
data class PspPrintString(
    @SerialName("CompanyName") val companyName: String = "SEP",
    @SerialName("RequestType") val requestType: String = "PrintString",
    @SerialName("Text") val text: String,
    @SerialName("AppID") val appId: String = "1"
)

@Serializable
data class PspPrintBitmap(
    @SerialName("CompanyName") val companyName: String = "SEP",
    @SerialName("RequestType") val requestType: String = "PrintBitmap",
    @SerialName("Bitmap") val bitmap: String,
    @SerialName("AppID") val appId: String = "1"
)

@Serializable
data class PspSaleTashim(
    @SerialName("CompanyName") val companyName: String = "SEP",
    @SerialName("RequestType") val requestType: String = "Tashim",
    @SerialName("AM") val amount: String,
    @SerialName("TashimParam") val tashimParam: String,
    @SerialName("paymentType") val paymentType: String = "CARD",
    @SerialName("GovID") val govId: String,
    @SerialName("SC") val sc: String,
    @SerialName("AppID") val appId: String = "1"
)

// نمونه استفاده از کلاس‌ها و تبدیل به JSON
fun main() {
    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    val posInfo = PspPosinfoModel()
    println(json.encodeToString(posInfo))

    val verify = PspDoVerify(refNum = "123456789")
    println(json.encodeToString(verify))
}