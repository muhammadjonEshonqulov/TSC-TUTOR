package uz.jbnuu.tsc.model.subjects

data class StudentTaskActivity(
    val id: Int?,
    val comment: String?,
    val _task: Int?,
    val send_date: Int?,
    val files: List<FilesData>?,
    val mark: Int?,
    val marked_comment: String?,
    val marked_date: Int?,
)
