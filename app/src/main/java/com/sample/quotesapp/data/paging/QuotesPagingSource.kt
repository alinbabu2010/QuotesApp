package com.sample.quotesapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sample.quotesapp.data.models.Quotes
import com.sample.quotesapp.data.models.Resource
import com.sample.quotesapp.data.repositories.QuotesRepository

class QuotesPagingSource(
    private val quotesRepository: QuotesRepository
): PagingSource<Int, Quotes>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Quotes> {
        return try {
            val position = params.key ?: 1
            getLoadResult(position)
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

    private suspend fun getLoadResult(position: Int): LoadResult<Int, Quotes> {
        return when (val response = quotesRepository.getQuoteList(position)) {
            is Resource.Error -> LoadResult.Error(response.exception)
            is Resource.Success -> LoadResult.Page(
                data = response.data?.results ?: emptyList(),
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (position == response.data?.totalPages) null else position + 1
            )
        }
    }

}