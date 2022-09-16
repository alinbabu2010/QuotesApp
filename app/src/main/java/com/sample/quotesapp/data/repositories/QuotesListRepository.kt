package com.sample.quotesapp.data.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import com.sample.quotesapp.data.models.QuoteRemoteKeys
import com.sample.quotesapp.data.models.Quotes
import com.sample.quotesapp.data.paging.QuotesRemoteMediator
import com.sample.quotesapp.data.sources.local.QuotesDatabase
import com.sample.quotesapp.data.sources.remote.QuotesNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuotesListRepository @Inject constructor(
    private val quotesNetworkDataSource: QuotesNetworkDataSource,
    quotesDatabase: QuotesDatabase,
) : QuotesRepository {

    private val quotesDao = quotesDatabase.quotesDao()
    private val remoteKeysDao = quotesDatabase.remoteKeysDao()

    private fun getPagingConfig() = PagingConfig(
        pageSize = 1,
        prefetchDistance = 0,
        maxSize = 100,
        initialLoadSize = 1
    )

    override suspend fun getQuoteList(page: Int) = withContext(Dispatchers.IO) {
        quotesNetworkDataSource.getQuotes(page)
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getQuotes() = Pager(
        config = getPagingConfig(),
        remoteMediator = QuotesRemoteMediator(this),
        pagingSourceFactory = { quotesDao.getQuotes() }
    ).flow

    override fun getQuotesFromDb(): PagingSource<Int, Quotes> = quotesDao.getQuotes()

    override suspend fun addQuotes(quotes: List<Quotes>) {
        quotesDao.addQuotes(quotes)
    }

    override suspend fun deleteQuotes() {
        quotesDao.deleteQuotes()
    }

    override suspend fun getRemoteKeys(id: String): QuoteRemoteKeys =
        remoteKeysDao.getRemoteKeys(id)

    override suspend fun addAllRemoteKeys(keys: List<QuoteRemoteKeys>) {
        remoteKeysDao.addAllRemoteKeys(keys)
    }

    override suspend fun deleteAllRemoteKeys() {
        remoteKeysDao.deleteAllRemoteKeys()
    }

}