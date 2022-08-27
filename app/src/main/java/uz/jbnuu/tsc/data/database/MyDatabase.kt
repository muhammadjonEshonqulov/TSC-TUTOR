//package uz.jbnuu.tsc.data.database
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.room.TypeConverters
//import uz.jbnuu.tsc.model.category.CategoryData
//import uz.jbnuu.tsc.model.product.ProductsData
//
//
//@Database(
//    entities = [
//        ProductsData::class, CategoryData::class
//    ], version = 2
//)
//@TypeConverters(
////    LessonsConverter::class, HandoutConverter::class,
//)
//abstract class MyDatabase : RoomDatabase() {
//    abstract fun dao(): MyDao
//
//    companion object {
//
//        private var instance: MyDatabase? = null
//
//        fun initDatabase(context: Context) {
//            synchronized(this) {
//                if (instance == null) {
//                    instance = Room
//                        .databaseBuilder(context.applicationContext, MyDatabase::class.java, "panoramic.db")
//                        .fallbackToDestructiveMigration()
//                        .build()
//                }
//            }
//        }
//
//        fun getDatabase() = instance
//    }
//}