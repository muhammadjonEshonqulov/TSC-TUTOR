package uz.jbnuu.tsc.model.schedule

data class ScheduleData(
    val subject: Subject?,
    val _week: Int?,
    val weekStartTime: Int?,
    val weekEndTime: Int?,
    val lesson_date: Int?,
    val additional: String?,
    val auditorium: Auditorium?,
    val trainingType: Auditorium?,
    val lessonPair: LessonPair?,
    val employee: Employee?,
)
