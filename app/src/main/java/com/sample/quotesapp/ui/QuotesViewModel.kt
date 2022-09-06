package com.sample.quotesapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sample.quotesapp.data.repositories.QuotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QuotesViewModel @Inject constructor(
    private val quotesRepository: QuotesRepository
) : ViewModel() {

    fun getQuotes() = quotesRepository.getQuotes().cachedIn(viewModelScope)

}