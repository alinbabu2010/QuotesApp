package com.sample.quotesapp.data.sources.remote

import com.sample.quotesapp.data.models.QuotesApiResponse
import com.sample.quotesapp.data.models.Resource

interface QuotesNetworkDataSource {
    suspend fun getQuotes(page: Int): Resource<QuotesApiResponse>
}