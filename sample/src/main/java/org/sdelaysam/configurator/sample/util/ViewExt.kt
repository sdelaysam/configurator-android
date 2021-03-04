package org.sdelaysam.configurator.sample.util

import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

fun RecyclerView.supportsChangeAnimation(value: Boolean) {
    (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = value
}

fun RecyclerView.clearOnDetach() {
    if (isAttachedToWindow) {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) {
            }

            override fun onViewDetachedFromWindow(v: View?) {
                adapter = null
            }
        })
    } else {
        adapter = null
    }
}

fun View.setSize(width: Int, height: Int) {
    val lp = layoutParams ?: run {
        layoutParams = ViewGroup.MarginLayoutParams(width, height)
        return
    }
    if (lp.width != width || lp.height != height) {
        lp.width = width
        lp.height = height
        layoutParams = lp
    }
}

fun View.setHorizontalMargins(margin: Int) {
    if (this.marginStart == margin && this.marginEnd == margin) return
    val lp = layoutParams as ViewGroup.MarginLayoutParams
    lp.marginStart = margin
    lp.marginEnd = margin
    layoutParams = lp
}

fun View.setHorizontalPadding(padding: Int) {
    setPadding(padding, paddingTop, padding, paddingBottom)
}
