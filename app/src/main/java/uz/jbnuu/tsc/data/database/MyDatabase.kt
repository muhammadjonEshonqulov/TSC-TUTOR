package uz.jbnuu.tsc.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uz.jbnuu.tsc.model.send_location.SendLocationBody


@Database(
    entities = [
        SendLocationBody::class
    ], version = 1
)
@TypeConverters(
//    LessonsConverter::class, HandoutConverter::class,
)
abstract class MyDatabase : RoomDatabase() {
    abstract fun dao(): MyDao

    companion object {

        private var instance: MyDatabase? = null

//        fun initDatabase(context: Context) {
//            synchronized(this) {
//                if (instance == null) {
//                    instance = Room
//                        .databaseBuilder(context.applicationContext, MyDatabase::class.java, "tsc_table.db")
//                        .fallbackToDestructiveMigration()
//                        .build()
//                }
//            }
//        }
//
//        fun getDatabase() = instance
    }
}