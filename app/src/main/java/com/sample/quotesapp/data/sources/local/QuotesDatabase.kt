package com.sample.quotesapp.data.sources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sample.quotesapp.data.models.QuoteRemoteKeys
import com.sample.quotesapp.data.models.Quotes

@Database(
    entities = [Quotes::class, QuoteRemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class QuotesDatabase : RoomDatabase() {

    abstract fun quotesDao(): QuotesDao

    abstract fun remoteKeysDao(): RemoteKeysDao

}