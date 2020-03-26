package com.mustahsan

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalSpacingDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column

        if (includeEdge) {
            outRect.top = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.bottom = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.left = spacing
            }
            outRect.right = spacing // item bottom
        } else {
            outRect.top = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
            outRect.bottom = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.left = spacing // item top
            }
        }
    }
}