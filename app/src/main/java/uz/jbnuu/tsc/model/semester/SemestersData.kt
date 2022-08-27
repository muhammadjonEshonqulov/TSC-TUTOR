package uz.jbnuu.tsc.model.semester


data class SemestersData(
    val id: Int?,
    val code: String?,
    val name: String?,
    val current: Boolean?,
    val education_year: EducationYear?,
    val weeks: List<WeekData>?
)
