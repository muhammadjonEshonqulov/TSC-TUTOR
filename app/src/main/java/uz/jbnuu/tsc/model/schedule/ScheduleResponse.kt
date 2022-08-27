package uz.jbnuu.tsc.model.schedule

import uz.jbnuu.tsc.model.login.hemis.LoginHemisData

data class ScheduleResponse(
    val success: Boolean?,
    val error: String?,
    val data: List<ScheduleData>?,
    val code: Int?,
)
