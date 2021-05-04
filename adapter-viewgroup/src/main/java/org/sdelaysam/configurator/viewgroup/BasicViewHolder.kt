package org.sdelaysam.configurator.viewgroup

import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import org.sdelaysam.configurator.RequiresCoroutineScope
import org.sdelaysam.configurator.adapter.AdapterEntry

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

abstract class BasicViewHolder(val view: View) {

    private var coroutineScope: CoroutineScope? = null

    @CallSuper
    open fun onBind(data: AdapterEntry) {
        coroutineScope?.coroutineContext?.cancelChildren()
        if (data is RequiresCoroutineScope) {
            if (coroutineScope == null) {
                coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
            }
            data.setCoroutineScope(coroutineScope!!)
        } else {
            coroutineScope = null
        }
    }

    @CallSuper
    open fun onRecycled() {
        coroutineScope?.coroutineContext?.cancelChildren()
    }

    interface Factory {
        val viewType: Int
        fun create(parent: ViewGroup): BasicViewHolder
    }
}