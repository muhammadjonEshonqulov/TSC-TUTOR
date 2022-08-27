package uz.jbnuu.tsc.model.me

import uz.jbnuu.tsc.model.group.GroupData
import uz.jbnuu.tsc.model.schedule.TrainingType
import uz.jbnuu.tsc.model.semester.SemestersData

data class MeData(
    val first_name: String?,
    val second_name: String?,
    val third_name: String?,
    val student_id_number: String?,
    val image: String?,
    val birth_date: Int?,
    val phone: String?,
    val group: GroupData?,
    val level: TrainingType?,
    val semester: SemestersData?,
)
