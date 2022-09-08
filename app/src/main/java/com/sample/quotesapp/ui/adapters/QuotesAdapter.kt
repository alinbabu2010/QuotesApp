package com.sample.quotesapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sample.quotesapp.data.models.Quotes
import com.sample.quotesapp.databinding.ItemQuotesBinding
import javax.inject.Inject

class QuotesAdapter @Inject constructor() :
    PagingDataAdapter<Quotes, QuotesAdapter.QuotesViewHolder>(QuotesDiffUtils()) {

    class QuotesViewHolder(val binding: ItemQuotesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Quotes) {
            binding.quote.text = item.content
        }

    }

    class QuotesDiffUtils : DiffUtil.ItemCallback<Quotes>() {

        override fun areItemsTheSame(oldItem: Quotes, newItem: Quotes): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Quotes, newItem: Quotes): Boolean =
            oldItem == newItem

    }

    override fun onBindViewHolder(holder: QuotesViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuotesViewHolder =
        QuotesViewHolder(
            ItemQuotesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


}