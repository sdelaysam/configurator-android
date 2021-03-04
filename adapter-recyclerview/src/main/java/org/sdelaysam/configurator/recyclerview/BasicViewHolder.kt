package org.sdelaysam.configurator.recyclerview

import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.sdelaysam.configurator.RequiresCoroutineScope
import org.sdelaysam.configurator.adapter.AdapterEntry

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

abstract class BasicViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private var coroutineScope: CoroutineScope? = null

    @CallSuper
    open fun onBind(data: AdapterEntry) {
        coroutineScope?.cancel()
        if (data is RequiresCoroutineScope) {
            coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
            data.setCoroutineScope(coroutineScope!!)
        } else {
            coroutineScope = null
        }
    }

    open fun onAttach() {}

    open fun onDetach() {}

    open fun onSaveState(container: SparseArray<Parcelable?>) {}

    open fun onRestoreState(container: SparseArray<Parcelable?>) {}

    @CallSuper
    open fun onRecycled() {
        coroutineScope?.cancel()
        coroutineScope = null
    }

    interface Factory {
        val viewType: Int
        fun create(parent: ViewGroup): BasicViewHolder
    }
}