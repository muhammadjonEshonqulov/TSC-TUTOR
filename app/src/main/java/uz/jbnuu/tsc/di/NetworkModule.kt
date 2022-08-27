package uz.jbnuu.tsc.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import uz.jbnuu.tsc.BuildConfig
import uz.jbnuu.tsc.app.App
import uz.jbnuu.tsc.data.network.ApiService
import uz.jbnuu.tsc.utils.Constants.Companion.BASE_URL
import uz.jbnuu.tsc.utils.Constants.Companion.BASE_URL_HEMIS
import uz.jbnuu.tsc.utils.Prefs
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideSharedPref(@ApplicationContext context: Context) = Prefs(context)

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context) = context

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor()
    }

    @Singleton
    @Provides
    fun provideHttpClient(
        prefs: Prefs
    ): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder().addHeader("Authorization", prefs.get(prefs.token, "...")).build()
                chain.proceed(request)
            }
            .connectTimeout(10000L, TimeUnit.MILLISECONDS)
            .readTimeout(10000L, TimeUnit.MILLISECONDS)
            .writeTimeout(10000L, TimeUnit.MILLISECONDS)

        if (BuildConfig.isDebug) {
            builder.addInterceptor(ChuckerInterceptor.Builder(App.context).collector(ChuckerCollector(App.context)).build())
        }

        return builder.build()
    }

    @Singleton
    @Provides
    @Named("provideRetrofit")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {

        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(BASE_URL)
            .build()
    }

    @Singleton
    @Provides
    @Named("provideRetrofitHemis")
    fun provideRetrofitHemis(okHttpClient: OkHttpClient): Retrofit {

        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(BASE_URL_HEMIS)
            .build()
    }

    @Singleton
    @Provides
    @Named("provideApiService")
    fun provideApiService(@Named("provideRetrofit") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    @Named("provideApiServiceHemis")
    fun provideApiServiceHemis(@Named("provideRetrofitHemis") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}