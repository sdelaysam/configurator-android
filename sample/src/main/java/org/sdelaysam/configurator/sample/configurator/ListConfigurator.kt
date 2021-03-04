package org.sdelaysam.configurator.sample.configurator

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.sdelaysam.configurator.CoroutineConfigurator
import org.sdelaysam.configurator.adapter.AdapterEntry
import org.sdelaysam.configurator.recyclerview.RecyclerViewAdapter
import org.sdelaysam.configurator.sample.configurator.recyclerview.CarouselViewHolder
import org.sdelaysam.configurator.sample.configurator.recyclerview.HeaderViewHolder
import org.sdelaysam.configurator.sample.configurator.recyclerview.TileViewHolder
import org.sdelaysam.configurator.sample.util.*

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

class ListConfigurator(
    private val items: Flow<List<AdapterEntry>>
) : CoroutineConfigurator<RecyclerView>() {

    private val adapter = RecyclerViewAdapter()

    init {
        adapter.register(HeaderViewHolder.Factory())
        adapter.register(CarouselViewHolder.Factory())
        adapter.register(TileViewHolder.Factory())
    }

    override fun CoroutineScope.onConfigure(target: RecyclerView) {
        target.layoutManager = LinearLayoutManager(target.context)
        target.addItemDecoration(ItemSpaceDecoration(dp(18), false))
        target.supportsChangeAnimation(false)
        bind(items) { adapter.reload(it) }
        target.adapter = adapter
    }

    override fun onReset(target: RecyclerView) {
        super.onReset(target)
        target.clearOnDetach()
    }
}