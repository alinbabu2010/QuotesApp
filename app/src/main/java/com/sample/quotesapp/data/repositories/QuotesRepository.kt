package com.sample.quotesapp.data.repositories

import androidx.paging.PagingData
import com.sample.quotesapp.data.models.Quotes
import com.sample.quotesapp.data.models.QuotesApiResponse
import com.sample.quotesapp.data.models.Resource
import kotlinx.coroutines.flow.Flow

interface QuotesRepository {
    suspend fun getQuoteList(page: Int): Resource<QuotesApiResponse>
    fun getQuotes(): Flow<PagingData<Quotes>>
}