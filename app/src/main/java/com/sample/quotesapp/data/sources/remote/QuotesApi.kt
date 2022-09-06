package com.sample.quotesapp.data.sources.remote

import com.sample.quotesapp.data.models.QuotesApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface QuotesApi {

    @GET(ENDPOINT_QUOTES)
    fun getQuotes(
        @Query(PARAM_PAGE) page: Int
    ): Call<QuotesApiResponse>

    companion object {
        const val ENDPOINT_QUOTES = "quotes"
        const val PARAM_PAGE = "page"
    }

}