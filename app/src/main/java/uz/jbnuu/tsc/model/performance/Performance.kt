package uz.jbnuu.tsc.model.performance

import uz.jbnuu.tsc.model.schedule.TrainingType

data class Performance(
    val grade: Int?,
    val max_ball: Int?,
    val label: String?,
    val examType: TrainingType?
)
