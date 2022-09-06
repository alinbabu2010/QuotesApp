package com.sample.quotesapp.data.models

data class QuotesApiResponse(
    val count: Int,
    val lastItemIndex: Int,
    val page: Int,
    val results: List<Quotes>,
    val totalCount: Int,
    val totalPages: Int
)