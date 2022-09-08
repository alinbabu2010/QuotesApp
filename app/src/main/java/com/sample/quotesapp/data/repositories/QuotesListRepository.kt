package com.sample.quotesapp.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.sample.quotesapp.data.paging.QuotesPagingSource
import com.sample.quotesapp.data.sources.remote.QuotesNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuotesListRepository @Inject constructor(
    private val quotesNetworkDataSource: QuotesNetworkDataSource,
) : QuotesRepository {

    private fun getPagingConfig() = PagingConfig(
        pageSize = 10,
        prefetchDistance = 1,
        maxSize = 100
    )

    override suspend fun getQuoteList(page: Int) = withContext(Dispatchers.IO) {
        quotesNetworkDataSource.getQuotes(page)
    }

    override fun getQuotes() = Pager(
        config = getPagingConfig(),
        pagingSourceFactory = { QuotesPagingSource(this) }
    ).flow

}