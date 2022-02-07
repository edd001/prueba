package com.example.moviedb.di

import com.example.web.ApiService
import com.example.web.WebConstants
import com.example.web.serializers.BooleanDeserializer
import com.example.web.serializers.BooleanSerializer
import com.example.web.serializers.DateDeserializaer
import com.example.web.serializers.DateSerializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor())
            .build()
    }

    @Provides
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Provides
    fun provideRetrofit(
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WebConstants.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson()))
            .client(provideHttpClient())
            .build()
    }

    @Provides
    fun gson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(Boolean::class.java, BooleanSerializer())
            .registerTypeAdapter(Boolean::class.java, BooleanDeserializer())
            .registerTypeAdapter(Boolean::class.javaPrimitiveType, BooleanSerializer())
            .registerTypeAdapter(Boolean::class.javaPrimitiveType, BooleanDeserializer())
            .registerTypeAdapter(Date::class.java, DateSerializer())
            .registerTypeAdapter(Date::class.java, DateDeserializaer())
            .create()
    }

    @Provides
    fun loggingInterceptor():HttpLoggingInterceptor{
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor

    }

    @Provides
    fun provideCurrencyService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

}