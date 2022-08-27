package uz.jbnuu.tsc.model.login.tyuter

data class LoginTyuterResponse(
    val status: Int?,
    val token: String?,
    val ism: String?,
    val familya: String?,
    val role_id: Int?,
)
