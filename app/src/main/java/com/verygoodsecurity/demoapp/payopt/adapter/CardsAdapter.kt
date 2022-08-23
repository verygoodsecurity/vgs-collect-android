package com.verygoodsecurity.demoapp.payopt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textview.MaterialTextView
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.vgscollect.view.card.CardType

internal class CardsAdapter : ListAdapter<Card, RecyclerView.ViewHolder>(DIFF_UTILS) {

    private var selected: Int = 0
        set(value) {
            notifyItemChanged(field)
            field = value
            notifyItemChanged(value)
        }

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
        return if (position >= currentList.count()) NEW_CARD_VIEW_TYPE else CARD_VIEW_TYPE
    }

    inner class CardViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {

        private val ivBrand = view.findViewById<AppCompatImageView>(R.id.ivBrand)
        private val mrbIsSelected = view.findViewById<MaterialRadioButton>(R.id.mrbIsSelected)
        private val mtvName = view.findViewById<MaterialTextView>(R.id.mtvName)
        private val mtvLastFourAndDate =
            view.findViewById<MaterialTextView>(R.id.mtvLastFourAndDate)

        init {
            view.setOnClickListener { selected = adapterPosition }
        }

        fun bind(card: Card) {
            val isSelected = selected == adapterPosition
            itemView.isSelected = isSelected
            mrbIsSelected.isChecked = isSelected
            ivBrand.setImageResource(getBrandIcon(card.brand))
            mtvName.text = card.holderName
            mtvLastFourAndDate.text = getFormattedLastFourAndDate(card)
        }

        private fun getFormattedLastFourAndDate(card: Card): String {
            return "•••• ${card.last4} | ${card.expiryMonth}/${card.expiryYear}"
        }

        @DrawableRes
        private fun getBrandIcon(brand: String): Int {
            return CardType.values().find { it.name.lowercase() == brand.lowercase() }?.resId
                ?: CardType.UNKNOWN.resId
        }
    }

    inner class NewCardViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {

        private val mrbIsSelected = view.findViewById<MaterialRadioButton>(R.id.mrbIsSelected)
        private val llInput = view.findViewById<LinearLayoutCompat>(R.id.llInput)

        init {
            view.setOnClickListener { selected = adapterPosition }
        }

        fun bind() {
            val isSelected = selected == adapterPosition
            itemView.isSelected = isSelected
            mrbIsSelected.isChecked = isSelected
            llInput.isVisible = isSelected
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