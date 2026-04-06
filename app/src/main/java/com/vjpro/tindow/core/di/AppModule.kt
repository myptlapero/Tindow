package com.vjpro.tindow.core.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.vjpro.tindow.data.local.AppDatabase
import com.vjpro.tindow.data.local.dao.SuggestionHistoryDao
import com.vjpro.tindow.data.local.dao.WeeklyPlanDao
import com.vjpro.tindow.data.remote.PexelsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "tindow_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideSuggestionHistoryDao(db: AppDatabase): SuggestionHistoryDao =
        db.suggestionHistoryDao()

    @Provides
    fun provideWeeklyPlanDao(db: AppDatabase): WeeklyPlanDao =
        db.weeklyPlanDao()

    @Provides
    @Singleton
    fun providePexelsApi(): PexelsApi =
        Retrofit.Builder()
            .baseUrl("https://api.pexels.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PexelsApi::class.java)
}
