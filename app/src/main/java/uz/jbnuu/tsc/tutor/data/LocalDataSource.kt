package uz.jbnuu.tsc.tutor.data

import kotlinx.coroutines.flow.Flow
import uz.jbnuu.tsc.tutor.data.database.MyDao
import uz.jbnuu.tsc.tutor.model.send_location.SendLocationBody
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val dao: MyDao) {
    suspend fun insertSendLocationBody(data: SendLocationBody) {
        return dao.insertSendLocationBodyData(data)
    }

    fun getSendLocationBodyData(): Flow<List<SendLocationBody>> = dao.getSendLocationBodyData()

    suspend fun clearSendLocationBodyData() {
        return dao.clearSendLocationBodyData()
    }

}
