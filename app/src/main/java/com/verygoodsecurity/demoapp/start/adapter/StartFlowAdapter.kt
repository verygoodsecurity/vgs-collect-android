package com.verygoodsecurity.demoapp.start.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.verygoodsecurity.demoapp.R

class StartFlowAdapter(
    private val onItemClick: (Int) -> Unit
) : ListAdapter<StartFlowAdapter.FlowItem, StartFlowAdapter.ViewHolder>(DiffCallback) {

    init {
        setHasStableIds(true)
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val mTvTitle: MaterialTextView = view.findViewById(R.id.mTvTitle)
        private val mTvDescription: MaterialTextView = view.findViewById(R.id.mTvDescription)

        fun bind(item: FlowItem, onItemClick: (Int) -> Unit) {
            mTvTitle.text = item.title
            mTvDescription.text = item.description
            view.setOnClickListener { onItemClick(item.id) }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.start_flow_card_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position), onItemClick)
    }

    override fun getItemId(position: Int) = getItem(position).id.toLong()

    private object DiffCallback : DiffUtil.ItemCallback<FlowItem>() {

        override fun areItemsTheSame(oldItem: FlowItem, newItem: FlowItem) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: FlowItem, newItem: FlowItem) = oldItem == newItem
    }

    data class FlowItem(
        val id: Int,
        val title: String,
        val description: String
    )
}