package com.sample.quotesapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.quotesapp.databinding.ActivityQuotesBinding
import com.sample.quotesapp.ui.adapters.LoaderAdapter
import com.sample.quotesapp.ui.adapters.QuotesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class QuotesActivity : AppCompatActivity() {

    private val viewModel: QuotesViewModel by viewModels()
    private var binding: ActivityQuotesBinding? = null

    @Inject
    lateinit var adapter: QuotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuotesBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.rvQuotes?.run {
            adapter = this@QuotesActivity.adapter.withLoadStateFooter(LoaderAdapter())
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
                    adapter.loadStateFlow.collectLatest { loadState ->
                        binding?.progressBar?.isVisible =
                            loadState.source.refresh is LoadState.Loading && binding?.refreshLayout?.isRefreshing == false
                        if (binding?.refreshLayout?.isRefreshing == true) {
                            binding?.refreshLayout?.isRefreshing =
                                loadState.source.refresh is LoadState.Loading
                        }
                    }
                }
            }
        }

        binding?.refreshLayout?.setOnRefreshListener {
            adapter.refresh()
        }

    }

}