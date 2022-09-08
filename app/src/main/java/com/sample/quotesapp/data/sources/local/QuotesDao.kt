package com.sample.quotesapp.data.sources.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sample.quotesapp.data.models.Quotes

@Dao
interface QuotesDao {

    @Query("SELECT * FROM Quotes")
    fun getQuotes(): PagingSource<Int, Quotes>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addQuotes(quotes: List<Quotes>)

    @Query("DELETE FROM Quotes")
    suspend fun deleteQuotes()

}