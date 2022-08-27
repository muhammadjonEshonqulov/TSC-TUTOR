//package uz.jbnuu.tsc.data
//
//import kotlinx.coroutines.flow.Flow
//import uz.jbnuu.tsc.model.category.CategoryData
//import uz.jbnuu.tsc.model.product.ProductsData
//import uz.jbnuu.tsc.data.database.MyDao
//import javax.inject.Inject
//
//class LocalDataSource @Inject constructor(private val dao: MyDao) {
//    suspend fun insertCategoryData(lessons: List<CategoryData>) {
//        return dao.insertCategoryData(lessons)
//    }
//
//    fun getCategoryData(): Flow<List<CategoryData>> = dao.getCategoryData()
//
//    suspend fun clearCategoryData() {
//        return dao.clearCategoryData()
//    }
//
//    suspend fun insertProductsData(products: List<ProductsData>) {
//        return dao.insertProductsData(products)
//    }
//
//    fun getProductsData(categoryId:Int): Flow<List<ProductsData>> = dao.getProductsData(categoryId)
//
//    suspend fun clearProductsData() {
//        return dao.clearProductsData()
//    }
//}