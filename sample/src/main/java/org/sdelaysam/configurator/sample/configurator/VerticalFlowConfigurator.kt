package org.sdelaysam.configurator.sample.configurator

import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.sdelaysam.configurator.CoroutineConfigurator
import org.sdelaysam.configurator.adapter.AdapterEntry
import org.sdelaysam.configurator.sample.configurator.viewgroup.TileViewHolder
import org.sdelaysam.configurator.sample.util.bind
import org.sdelaysam.configurator.sample.view.VerticalFlowAdapter

/**
 * Created on 31.03.2021.
 * @author sdelaysam
 */
class VerticalFlowConfigurator(
    private val items: Flow<List<AdapterEntry>>
) : CoroutineConfigurator<ConstraintLayout>() {

    private val adapter = VerticalFlowAdapter()

    init {
        adapter.register(TileViewHolder.Factory())
    }

    override fun CoroutineScope.onConfigure(target: ConstraintLayout) {
        adapter.attach(target)
        bind(items) { adapter.reload(it) }
    }

    override fun onReset(target: ConstraintLayout) {
        super.onReset(target)
        adapter.detach()
    }
}