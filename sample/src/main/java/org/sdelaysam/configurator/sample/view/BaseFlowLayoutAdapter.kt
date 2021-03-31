package org.sdelaysam.configurator.sample.view

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.helper.widget.Flow
import org.sdelaysam.configurator.sample.R
import org.sdelaysam.configurator.viewgroup.ViewGroupAdapter

/**
 * Created on 08.03.2021.
 * @author sdelaysam
 */
abstract class BaseFlowLayoutAdapter : ViewGroupAdapter() {

    protected abstract fun configureFlow(flow: Flow)

    protected abstract fun configureView(flow: Flow, view: View, position: Int)

    private var flow: Flow? = null

    private var childOffset = 0

    override fun onAttach(viewGroup: ViewGroup) {
        super.onAttach(viewGroup)
        flow = viewGroup.findViewById(R.id.flow)
        flow?.let {
            childOffset = viewGroup.indexOfChild(it) + 1
            configureFlow(it)
        }
    }

    override fun onDetach(viewGroup: ViewGroup) {
        super.onDetach(viewGroup)
        flow = null
        childOffset = 0
    }

    override fun addView(viewGroup: ViewGroup, view: View, position: Int) {
        view.id = View.generateViewId()
        val pos = position + childOffset
        if (pos == viewGroup.childCount) {
            viewGroup.addView(view)
        } else {
            viewGroup.addView(view, pos)
        }
        syncReferenceIds(viewGroup)
        val flow = this.flow ?: return
        configureView(flow, view, position)
    }

    override fun moveView(viewGroup: ViewGroup, fromPosition: Int, toPosition: Int) {
        val from = fromPosition + childOffset
        val to = toPosition + childOffset
        val child = viewGroup.getChildAt(from)
        viewGroup.removeViewAt(from)
        viewGroup.addView(child, to - childOffset)
        syncReferenceIds(viewGroup)
    }

    override fun removeAllViews(viewGroup: ViewGroup) {
        if (viewGroup.childCount == childOffset) return
        viewGroup.removeViews(childOffset, viewGroup.childCount - childOffset)
        val flow = this.flow ?: return
        flow.referencedIds = IntArray(0)
    }

    override fun removeViews(viewGroup: ViewGroup, position: Int, count: Int) {
        val pos = position + childOffset
        repeat(count) {
            viewGroup.removeViewAt(pos)
        }
        syncReferenceIds(viewGroup)
    }

    private fun syncReferenceIds(viewGroup: ViewGroup) {
        val flow = this.flow ?: return
        flow.referencedIds = (childOffset until viewGroup.childCount)
            .map { viewGroup.getChildAt(it).id }
            .toIntArray()
        flow.requestLayout()
    }
}