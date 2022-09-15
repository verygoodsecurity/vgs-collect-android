package com.verygoodsecurity.demoapp.payopt.decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

internal class MarginItemDecoration(
    private val marginTop: Int,
    private val marginStart: Int,
    private val marginEnd: Int,
    private val marginBottom: Int
) : RecyclerView.ItemDecoration() {

    constructor(margin: Int) : this(margin, margin, margin, margin)

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = marginTop
            }
            left = marginStart
            right = marginEnd
            bottom = marginBottom
        }
    }
}