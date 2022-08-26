package com.verygoodsecurity.demoapp.payopt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.payopt.model.Card
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer
import kotlinx.android.synthetic.main.payment_optimization_card_item.view.*
import kotlinx.android.synthetic.main.payment_optimization_new_card_item.view.*
import kotlinx.android.synthetic.main.payment_optimization_new_card_item.view.mrbIsSelected

internal class CardsAdapter constructor(
    private val bindListener: NewCardBindListener
) : ListAdapter<Card, RecyclerView.ViewHolder>(DIFF_UTILS) {

    private var selected: Int = 0
        set(value) {
            notifyItemChanged(field)
            field = value
            notifyItemChanged(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        fun inflate(@LayoutRes id: Int): View {
            return LayoutInflater.from(parent.context).inflate(id, parent, false)
        }

        return when (viewType) {
            Type.EXISTING.value -> CardViewHolder(inflate(R.layout.payment_optimization_card_item))
            Type.NEW.value -> NewCardViewHolder(inflate(R.layout.payment_optimization_new_card_item))
            else -> throw IllegalStateException("Not implemented!")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val isSelected = position == selected
        when (holder) {
            is CardViewHolder -> holder.bind(getItem(position), isSelected)
            is NewCardViewHolder -> holder.bind(isSelected)
        }
    }

    override fun getItemCount() = super.getItemCount() + 1

    override fun getItemViewType(position: Int): Int {
        return if (position >= currentList.count()) Type.NEW.value else Type.EXISTING.value
    }

    fun getSelected(): Card? = if (selected >= currentList.count()) null else getItem(selected)

    fun addItem(card: Card) {
        submitList(listOf(card) + currentList) {
            selected = selected.inc()
        }
    }

    inner class CardViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener { selected = adapterPosition }
        }

        fun bind(card: Card, isSelected: Boolean) {
            itemView.isSelected = isSelected
            itemView.mrbIsSelected.isChecked = isSelected
            itemView.ivBrand.setImageResource(getBrandIcon(card.brand))
            itemView.mtvName.text = card.holderName
            itemView.mtvLastFourAndDate.text = getFormattedLastFourAndDate(card)
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

        init {
            itemView.vgsTiedExpiry?.setSerializer(
                VGSExpDateSeparateSerializer(
                    "card.exp_month",
                    "card.exp_year"
                )
            )
            itemView.setOnClickListener { selected = adapterPosition }
        }

        fun bind(isSelected: Boolean) {
            itemView.isSelected = isSelected
            itemView.mrbIsSelected.isChecked = isSelected
            itemView.llInput.isVisible = isSelected
            notifyListener(isSelected)
        }

        private fun notifyListener(isSelected: Boolean) {
            val inputs = arrayOf(
                itemView.vgsTiedCardHolder,
                itemView.vgsTiedCardNumber,
                itemView.vgsTiedExpiry,
                itemView.vgsTiedCvc
            )
            if (isSelected) bindListener.bind(inputs) else bindListener.unbind(inputs)
        }
    }

    private companion object {

        val DIFF_UTILS = object : DiffUtil.ItemCallback<Card>() {

            override fun areItemsTheSame(oldItem: Card, newItem: Card) = oldItem == newItem

            override fun areContentsTheSame(oldItem: Card, newItem: Card) = oldItem == newItem
        }
    }

    enum class Type constructor(val value: Int) {

        EXISTING(1),
        NEW(2)
    }

    interface NewCardBindListener {

        fun bind(inputs: Array<InputFieldView>)

        fun unbind(inputs: Array<InputFieldView>)
    }
}