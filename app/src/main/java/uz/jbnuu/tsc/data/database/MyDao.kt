package uz.jbnuu.tsc.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uz.jbnuu.tsc.model.send_location.SendLocationBody


@Dao
interface MyDao {
    // SendLocationBody
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSendLocationBodyData(data: SendLocationBody)

    @Query("select *  from SendLocationBody")
    fun getSendLocationBodyData(): Flow<List<SendLocationBody>>

    @Query("delete from SendLocationBody")
    suspend fun clearSendLocationBodyData()
}