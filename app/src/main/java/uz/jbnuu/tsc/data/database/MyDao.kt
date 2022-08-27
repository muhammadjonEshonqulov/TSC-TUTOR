//package uz.jbnuu.tsc.data.database
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import kotlinx.coroutines.flow.Flow
//import uz.jbnuu.tsc.model.category.CategoryData
//import uz.jbnuu.tsc.model.product.ProductsData
//
//
//@Dao
//interface MyDao {
////    // ProductsData
////    @Insert(onConflict = OnConflictStrategy.REPLACE)
////    suspend fun insertProductsData(products: List<ProductsData>)
////
////    @Query("select *  from ProductsData where category_id=:categoryId" )
////    fun getProductsData(categoryId:Int): Flow<List<ProductsData>>
////
////    @Query("delete from ProductsData")
////    suspend fun clearProductsData()
////
////    // CategoryData
////    @Insert(onConflict = OnConflictStrategy.REPLACE)
////    suspend fun insertCategoryData(lessons: List<CategoryData>)
////
////    @Query("select *  from CategoryData")
////    fun getCategoryData(): Flow<List<CategoryData>>
////
////    @Query("delete from CategoryData")
////    suspend fun clearCategoryData()
//}
//
