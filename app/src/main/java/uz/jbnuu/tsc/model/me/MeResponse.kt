package uz.jbnuu.tsc.model.me

import uz.jbnuu.tsc.model.schedule.ScheduleData

data class MeResponse(
    val success: Boolean?,
    val error: String?,
    val data: MeData?,
    val code: Int?,
)
