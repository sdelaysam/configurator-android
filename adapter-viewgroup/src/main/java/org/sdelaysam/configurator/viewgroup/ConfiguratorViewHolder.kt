package org.sdelaysam.configurator.viewgroup

import android.view.View
import org.sdelaysam.configurator.Configurator
import org.sdelaysam.configurator.adapter.AdapterEntry

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

abstract class ConfiguratorViewHolder<T, C: Configurator<T>>(
    private val target: T,
    view: View
) : BasicViewHolder(view) {

    private var configurator: C? = null

    override fun onBind(data: AdapterEntry) {
        super.onBind(data)
        @Suppress("UNCHECKED_CAST")
        configurator = data as? C
        configurator?.configure(target)
    }

    override fun onRecycled() {
        super.onRecycled()
        configurator?.reset(target)
        configurator = null
    }
}