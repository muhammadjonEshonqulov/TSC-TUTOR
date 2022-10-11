package uz.jbnuu.tsc.tutor.model.history_location

data class LocationHistoryResponse(
    val status: Int?,
    val history: List<LocationHistoryData>?,
    val current_page: Int?,
    val total_page: Int?,
)
