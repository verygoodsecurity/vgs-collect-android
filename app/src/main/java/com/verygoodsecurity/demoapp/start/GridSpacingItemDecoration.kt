package com.verygoodsecurity.demoapp.start

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val spanCount: Int = 2,
    private val spacing: Int = 10,
    private val includeEdge: Boolean = false,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position: Int = parent.getChildAdapterPosition(view)

        if (position >= 0) {
            val column = position % spanCount // item column

            if (includeEdge) {
                outRect.left =
                    spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right =
                    (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right =
                    spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        } else {
            outRect.left = 0
            outRect.right = 0
            outRect.top = 0
            outRect.bottom = 0
        }
    }
}
