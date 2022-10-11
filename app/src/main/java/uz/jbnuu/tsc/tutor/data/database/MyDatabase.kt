package uz.jbnuu.tsc.tutor.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.jbnuu.tsc.tutor.model.send_location.SendLocationBody


@Database(
    entities = [
        SendLocationBody::class
    ], version = 1
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