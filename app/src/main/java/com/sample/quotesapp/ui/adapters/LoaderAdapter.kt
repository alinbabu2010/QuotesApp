package com.sample.quotesapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sample.quotesapp.databinding.LoaderItemBinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoaderAdapter @Inject constructor() : LoadStateAdapter<LoaderAdapter.LoadStateViewHolder>() {

    var isRefreshing: Boolean = false

    @Inject
    lateinit var adapter: QuotesAdapter

    inner class LoadStateViewHolder(private val binding: LoaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(loadState: LoadState) {
            binding.progressBar.isVisible = loadState is LoadState.Loading && !isRefreshing

            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible =
                !(loadState as? LoadState.Error)?.error?.message.isNullOrBlank()
            binding.errorMsg.text = (loadState as? LoadState.Error)?.error?.message

            binding.retryButton.setOnClickListener {
                adapter.retry()
            }
        }
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) = LoadStateViewHolder(
        LoaderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

}