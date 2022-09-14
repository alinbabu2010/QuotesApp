package com.sample.quotesapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.quotesapp.databinding.ActivityQuotesBinding
import com.sample.quotesapp.ui.adapters.LoaderAdapter
import com.sample.quotesapp.ui.adapters.QuotesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuotesActivity : AppCompatActivity() {

    private val viewModel: QuotesViewModel by viewModels()
    private var binding: ActivityQuotesBinding? = null

    @Inject
    lateinit var adapter: QuotesAdapter

    @Inject
    lateinit var loaderAdapter: LoaderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuotesBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.rvQuotes?.run {
            adapter = this@QuotesActivity.adapter.withLoadStateFooter(loaderAdapter)
            layoutManager = LinearLayoutManager(this@QuotesActivity)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.getQuotes().collectLatest {
                        adapter.submitData(it)
                    }
                }
                launch {
                    adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                        .filter { it.refresh is LoadState.NotLoading }
                        .collectLatest { configureNotLoadingStates() }
                }
                launch {
                    adapter.loadStateFlow.distinctUntilChangedBy { it.refresh }
                        .filter { it.refresh is LoadState.Loading }
                        .collectLatest { configureLoadingState(it) }
                }
            }
        }

        binding?.refreshLayout?.setOnRefreshListener {
            loaderAdapter.isRefreshing = true
            adapter.refresh()
        }

    }

    /**
     * Method to configure view in case of loading state
     */
    private fun configureLoadingState(loadState: CombinedLoadStates) {
        if (binding?.refreshLayout?.isRefreshing == false) {
            loaderAdapter.isRefreshing = false
            binding?.rvQuotes?.isVisible = false
            binding?.progressBar?.isVisible = loadState.refresh is LoadState.Loading
        }
    }

    /**
     * Method to configure view in case of not loading state
     */
    private fun configureNotLoadingStates() {
        if (binding?.refreshLayout?.isRefreshing == true) {
            loaderAdapter.isRefreshing = false
            binding?.progressBar?.isVisible = false
            binding?.refreshLayout?.isRefreshing = false
        } else {
            binding?.rvQuotes?.isVisible = true
            binding?.progressBar?.isVisible = false
        }
        binding?.rvQuotes?.scrollToPosition(0)
    }

}