package org.sdelaysam.configurator.sample.view

import android.view.View
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * Created on 31.03.2021.
 * @author sdelaysam
 */
class VerticalFlowAdapter : BaseFlowLayoutAdapter() {

    override fun configureFlow(flow: Flow) {
        flow.setOrientation(Flow.VERTICAL)
        flow.setWrapMode(Flow.WRAP_NONE)
    }

    override fun configureView(flow: Flow, view: View, position: Int) {
        val lp = view.layoutParams as ConstraintLayout.LayoutParams
        lp.width = ConstraintLayout.LayoutParams.MATCH_PARENT
        if (lp.height == ConstraintLayout.LayoutParams.MATCH_PARENT) {
            lp.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        }
        view.layoutParams = lp
    }
}