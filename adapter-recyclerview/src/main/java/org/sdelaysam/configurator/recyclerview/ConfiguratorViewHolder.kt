package org.sdelaysam.configurator.recyclerview

import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import org.sdelaysam.configurator.Configurator
import org.sdelaysam.configurator.StatefulConfigurator
import org.sdelaysam.configurator.adapter.AdapterEntry

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

abstract class ConfiguratorViewHolder<T, C: Configurator<T>>(
    private val target: T,
    view: View
) : BasicViewHolder(view) {

    protected var configurator: C? = null

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

    override fun onSaveState(container: SparseArray<Parcelable?>) {
        val entry = configurator as? AdapterEntry ?: return
        @Suppress("UNCHECKED_CAST")
        val stateful = configurator as? StatefulConfigurator<T> ?: return
        container.put(entry.contentId, stateful.onSaveState(target))
    }

    override fun onRestoreState(container: SparseArray<Parcelable?>) {
        val entry = configurator as? AdapterEntry ?: return
        @Suppress("UNCHECKED_CAST")
        val stateful = configurator as? StatefulConfigurator<T> ?: return
        val state = container[entry.contentId] ?: return
        stateful.onRestoreState(target, state)
    }
}