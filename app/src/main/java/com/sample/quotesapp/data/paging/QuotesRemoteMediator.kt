package com.sample.quotesapp.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.sample.quotesapp.data.models.QuoteRemoteKeys
import com.sample.quotesapp.data.models.Quotes
import com.sample.quotesapp.data.models.QuotesApiResponse
import com.sample.quotesapp.data.models.Resource
import com.sample.quotesapp.data.repositories.QuotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class QuotesRemoteMediator(
    private val quotesRepository: QuotesRepository,
) : RemoteMediator<Int, Quotes>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Quotes>,
    ): MediatorResult {

        return try {

            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosetsToCurrentPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevPage ?: return MediatorResult.Success(
                        remoteKeys != null
                    )
                    prevPage
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextPage ?: return MediatorResult.Success(
                        remoteKeys != null
                    )
                    nextPage
                }
            }

            var endOfPaginationReached = false
            val prevKey = if (currentPage == 1) null else currentPage - 1
            val nextKey = if (endOfPaginationReached) null else currentPage + 1

            if (loadType == LoadType.REFRESH) {
                quotesRepository.deleteQuotes()
                quotesRepository.deleteAllRemoteKeys()
            }

            when (val response = quotesRepository.getQuoteList(currentPage)) {
                is Resource.Error -> {}
                is Resource.Success -> {
                    saveToDatabase(response.data, prevKey, nextKey)
                    endOfPaginationReached = response.data?.totalPages == currentPage
                }
            }

            MediatorResult.Success(endOfPaginationReached)

        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }

    }

    private suspend fun getRemoteKeyClosetsToCurrentPosition(state: PagingState<Int, Quotes>): QuoteRemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let {
                quotesRepository.getRemoteKeys(it)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Quotes>): QuoteRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let {
            quotesRepository.getRemoteKeys(it.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Quotes>): QuoteRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            quotesRepository.getRemoteKeys(it.id)
        }
    }

    private suspend fun saveToDatabase(data: QuotesApiResponse?, prevKey: Int?, nextKey: Int?) {
        withContext(Dispatchers.IO) {
            data?.results?.let { quotesRepository.addQuotes(it) }
            val keys = data?.results?.map { quotes ->
                QuoteRemoteKeys(
                    id = quotes.id,
                    prevPage = prevKey,
                    nextPage = nextKey
                )
            }
            keys?.let { quotesRepository.addAllRemoteKeys(it) }
        }
    }

}