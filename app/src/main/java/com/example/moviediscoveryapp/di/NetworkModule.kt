package com.example.moviediscovery.di

import com.example.moviediscovery.data.remote.TMDBService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * NetworkModule is a Hilt "Module". 
 * In simple terms, it's a "Toolbox" where we define how to create complex tools 
 * like Retrofit and the API Service.
 *
 * @Module: Tells Hilt that this is a place to find "recipes" for creating objects.
 * @InstallIn(SingletonComponent::class): Tells Hilt that these tools should live 
 * as long as the entire app lives (Singleton).
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // The base web address for the Movie Database API
    private const val BASE_URL = "https://api.themoviedb.org/"

    /**
     * Moshi is a "Translator". 
     * It converts JSON text from the internet into Kotlin Data Classes.
     */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory()) // Helps Moshi understand Kotlin classes
            .build()
    }

    /**
     * OkHttpClient is the "Engine" that actually sends the request over the internet.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // Logging interceptor lets us see the network traffic in the Logcat (debugging)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS) // Wait 30s for a connection
            .readTimeout(30, TimeUnit.SECONDS)    // Wait 30s to read data
            .build()
    }

    /**
     * Retrofit is the "Manager" that coordinates the Engine (OkHttp) and the Translator (Moshi).
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Use our engine
            .addConverterFactory(MoshiConverterFactory.create(moshi)) // Use our translator
            .build()
    }

    /**
     * This creates the actual "Phone" (TMDBService) that we use to call the API.
     */
    @Provides
    @Singleton
    fun provideTMDBService(retrofit: Retrofit): TMDBService {
        return retrofit.create(TMDBService::class.java)
    }
}