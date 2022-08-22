package com.verygoodsecurity.demoapp.payopt.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.verygoodsecurity.demoapp.R

internal class CardsAdapter : ListAdapter<Card, RecyclerView.ViewHolder>(DIFF_UTILS) {

    private val selected: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            CARD_VIEW_TYPE -> CardViewHolder(
                inflater.inflate(
                    R.layout.payment_optimization_card_item,
                    parent,
                    false
                )
            )
            NEW_CARD_VIEW_TYPE -> NewCardViewHolder(
                inflater.inflate(
                    R.layout.payment_optimization_new_card_item,
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException("Not implemented!")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CardViewHolder -> holder.bind(getItem(position))
            is NewCardViewHolder -> holder.bind()
        }
    }

    override fun getItemCount() = super.getItemCount() + 1

    override fun getItemViewType(position: Int): Int {
        Log.d("Test", "getItemViewType, position = $position")
        return if (position >= currentList.count()) NEW_CARD_VIEW_TYPE else CARD_VIEW_TYPE
    }

    inner class CardViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(card: Card) {
            Log.d("Test", "bind card item [$card]")
        }
    }

    inner class NewCardViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {

        fun bind() {
            Log.d("Test", "bind new card item")
        }
    }

    private companion object {

        private const val CARD_VIEW_TYPE = 0
        private const val NEW_CARD_VIEW_TYPE = 1

        val DIFF_UTILS = object : DiffUtil.ItemCallback<Card>() {

            override fun areItemsTheSame(oldItem: Card, newItem: Card) = oldItem == newItem

            override fun areContentsTheSame(oldItem: Card, newItem: Card) = oldItem == newItem
        }
    }
}