package com.sample.quotesapp.data.repositories

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.sample.quotesapp.data.models.QuoteRemoteKeys
import com.sample.quotesapp.data.models.Quotes
import com.sample.quotesapp.data.models.QuotesApiResponse
import com.sample.quotesapp.data.models.Resource
import kotlinx.coroutines.flow.Flow

interface QuotesRepository {
    suspend fun getQuoteList(page: Int): Resource<QuotesApiResponse>
    fun getQuotes(): Flow<PagingData<Quotes>>
    suspend fun addQuotes(quotes: List<Quotes>)
    fun getQuotesFromDb() : PagingSource<Int, Quotes>
    suspend fun deleteQuotes()
    suspend fun getRemoteKeys(id:String) : QuoteRemoteKeys
    suspend fun addAllRemoteKeys(keys: List<QuoteRemoteKeys>)
    suspend fun deleteAllRemoteKeys()
}