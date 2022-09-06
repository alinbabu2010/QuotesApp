package com.sample.quotesapp.data.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sample.quotesapp.data.models.Quotes
import com.sample.quotesapp.data.models.QuotesApiResponse
import com.sample.quotesapp.data.models.Resource
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

    private fun getPagingSource() = object : PagingSource<Int, Quotes>() {

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Quotes> {
            return try {
                val position = params.key ?: 1
                val response = getQuoteList(position)
                LoadResult.Page(
                    data = response?.results ?: emptyList(),
                    prevKey = if (position == 1) null else position - 1,
                    nextKey = if (position == response?.totalPages) null else position + 1
                )
            } catch (exception: Exception) {
                LoadResult.Error(exception)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, Quotes>): Int? {
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

    }

    override suspend fun getQuoteList(page: Int): QuotesApiResponse? {
        return when (
            val response = withContext(Dispatchers.IO) {
                quotesNetworkDataSource.getQuotes(page)
            }
        ) {
            is Resource.Success -> response.data
            is Resource.Error -> throw response.exception

        }
    }

    override fun getQuotes() = Pager(
        config = getPagingConfig(),
        pagingSourceFactory = { getPagingSource() }
    ).flow

}