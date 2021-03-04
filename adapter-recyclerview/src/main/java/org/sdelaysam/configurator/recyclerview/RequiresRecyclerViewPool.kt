package org.sdelaysam.configurator.recyclerview

import androidx.recyclerview.widget.RecyclerView

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

interface RequiresRecyclerViewPool {
    fun setRecyclerViewPool(recyclerViewPool: RecyclerView.RecycledViewPool)
}