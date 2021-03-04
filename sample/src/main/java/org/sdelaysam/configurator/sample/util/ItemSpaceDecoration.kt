package org.sdelaysam.configurator.sample.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * https://stackoverflow.com/a/30701422
 */
class ItemSpaceDecoration(private val spacing: Int, private val includeEdge: Boolean = true) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        val spanCount = (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 1
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column
        val isVertical = (parent.layoutManager as? LinearLayoutManager)?.orientation != RecyclerView.HORIZONTAL

        if (includeEdge) {
            setLeft(outRect, spacing - column * spacing / spanCount, isVertical)
            setRight(outRect, (column + 1) * spacing / spanCount, isVertical)

            if (position < spanCount) {
                setTop(outRect, spacing, isVertical)
            }
            setBottom(outRect, spacing, isVertical)
        } else {
            setLeft(outRect, column * spacing / spanCount, isVertical)
            setRight(outRect, spacing - (column + 1) * spacing / spanCount, isVertical)
            if (position >= spanCount) {
                setTop(outRect, spacing, isVertical)
            }
        }
    }

    private fun setTop(outRect: Rect, value: Int, isVertical: Boolean) {
        if (isVertical) {
            outRect.top = value
        } else {
            outRect.left = value
        }
    }

    private fun setBottom(outRect: Rect, value: Int, isVertical: Boolean) {
        if (isVertical) {
            outRect.bottom = value
        } else {
            outRect.right = value
        }
    }

    private fun setLeft(outRect: Rect, value: Int, isVertical: Boolean) {
        if (isVertical) {
            outRect.left = value
        } else {
            outRect.top = value
        }
    }

    private fun setRight(outRect: Rect, value: Int, isVertical: Boolean) {
        if (isVertical) {
            outRect.right = value
        } else {
            outRect.bottom = value
        }
    }

}