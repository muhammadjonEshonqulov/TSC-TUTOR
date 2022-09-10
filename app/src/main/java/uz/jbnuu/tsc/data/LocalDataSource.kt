package uz.jbnuu.tsc.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import uz.jbnuu.tsc.data.database.MyDao
import uz.jbnuu.tsc.model.send_location.SendLocationBody
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val dao: MyDao) {
    suspend fun insertCategoryData(data: SendLocationBody) {
        return dao.insertSendLocationBodyData(data)
    }

    fun getSendLocationBodyData(): Flow<List<SendLocationBody>> = dao.getSendLocationBodyData()

    suspend fun clearSendLocationBodyData() {
        return dao.clearSendLocationBodyData()
    }
}
