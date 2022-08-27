package uz.jbnuu.tsc.model.subjects

import uz.jbnuu.tsc.model.schedule.Employee
import uz.jbnuu.tsc.model.schedule.Subject
import uz.jbnuu.tsc.model.schedule.TrainingType

data class Task(
    val id: Int?,
    val subject: Subject?,
    val name: String?,
    val comment: String?,
    val max_ball: Int?,
    val deadline: Int?,
    val trainingType: TrainingType?,
    val attempt_limit: Int?,
    val attempt_count: Int?,
    val filters: List<FilterData>?,
    val taskType: TrainingType?,
    val taskStatus: TrainingType?,
    val employee: Employee?,
    val updated_at: Int?,
    val studentTaskActivity: StudentTaskActivity?,

)
