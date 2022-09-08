package com.sample.quotesapp.di

import android.content.Context
import androidx.room.Room
import com.sample.quotesapp.data.sources.local.QuotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): QuotesDatabase {
        return Room.databaseBuilder(
            context,
            QuotesDatabase::class.java,
            "quotes_db"
        ).fallbackToDestructiveMigration().build()
    }

}