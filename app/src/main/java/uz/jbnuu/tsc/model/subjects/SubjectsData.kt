package uz.jbnuu.tsc.model.subjects

import uz.jbnuu.tsc.model.schedule.Subject
import uz.jbnuu.tsc.model.schedule.TrainingType

data class SubjectsData(
    val subject: Subject?,
    val subjectType: TrainingType?,
    val _semester: String?,
    val total_acload: Int?,
    val credit: Int?
)
