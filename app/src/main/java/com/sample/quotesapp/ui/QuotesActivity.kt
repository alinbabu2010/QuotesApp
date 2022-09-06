package com.sample.quotesapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.quotesapp.databinding.ActivityQuotesBinding
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
            adapter = this@QuotesActivity.adapter
            layoutManager = LinearLayoutManager(this@QuotesActivity)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getQuotes().collectLatest {
                    adapter.submitData(it)
                }
            }
        }

    }

}