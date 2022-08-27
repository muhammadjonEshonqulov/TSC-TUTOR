package uz.jbnuu.tsc.model

import uz.jbnuu.tsc.model.subjects.SubjectData

data class SubjectResponse(
    val success: Boolean?,
    val error: String?,
    val data: List<SubjectData>?,
    val code: Int?,
)
