package uz.jbnuu.tsc.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.jbnuu.tsc.app.App
import uz.jbnuu.tsc.data.database.MyDatabase
import uz.jbnuu.tsc.utils.Constants.Companion.DATABASE_NAME
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase() = Room.databaseBuilder(App.context, MyDatabase::class.java, DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideDatabaseDao(myDao: MyDatabase) = myDao.dao()
}