package uz.jbnuu.tsc.app

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App:Application() {


    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
    }

    companion object{
        lateinit var context: Context
    }
}