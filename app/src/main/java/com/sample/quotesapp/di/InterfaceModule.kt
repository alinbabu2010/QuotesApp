@file:Suppress("unused")

package com.sample.quotesapp.di

import com.sample.quotesapp.data.repositories.QuotesListRepository
import com.sample.quotesapp.data.repositories.QuotesRepository
import com.sample.quotesapp.data.sources.remote.QuotesInternetDataSource
import com.sample.quotesapp.data.sources.remote.QuotesNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface InterfaceModule {

    @Binds
    fun bindQuotesNetworkDataSource(
        quotesInternetDataSource: QuotesInternetDataSource,
    ): QuotesNetworkDataSource

    @Binds
    fun bindQuotesRepository(
        quotesListRepository: QuotesListRepository,
    ): QuotesRepository


}