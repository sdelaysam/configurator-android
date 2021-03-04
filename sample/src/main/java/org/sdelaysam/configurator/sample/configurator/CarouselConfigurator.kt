package org.sdelaysam.configurator.sample.configurator

import android.os.Parcelable
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.sdelaysam.configurator.GenerateViewHolder
import org.sdelaysam.configurator.StatefulConfigurator
import org.sdelaysam.configurator.adapter.AdapterCoroutineConfigurator
import org.sdelaysam.configurator.adapter.AdapterEntry
import org.sdelaysam.configurator.recyclerview.RecyclerViewAdapter
import org.sdelaysam.configurator.sample.VIEW_TYPE_CAROUSEL
import org.sdelaysam.configurator.sample.configurator.recyclerview.TileViewHolder
import org.sdelaysam.configurator.sample.util.*

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

@GenerateViewHolder
class CarouselConfigurator(
    private val items: Flow<List<AdapterEntry>>,
    override val contentId: Int
) : AdapterCoroutineConfigurator<RecyclerView>(), StatefulConfigurator<RecyclerView> {

    override val viewType: Int = VIEW_TYPE_CAROUSEL
    override val contentHash: Int = 0

    private val adapter = RecyclerViewAdapter()

    init {
        adapter.register(TileViewHolder.Factory())
    }

    override fun CoroutineScope.onConfigure(target: RecyclerView) {
        bind(items) { adapter.reload(it) }
        if (target.layoutManager == null) {
            target.layoutManager =
                LinearLayoutManager(target.context, RecyclerView.HORIZONTAL, false)
            target.addItemDecoration(ItemSpaceDecoration(dp(18), false))
            target.supportsChangeAnimation(false)
            target.setSize(MATCH_PARENT, dp(200))
            target.setHorizontalMargins(dp(-18))
            target.setHorizontalPadding(dp(18))
            target.clipToPadding = false
        }
        if (target.adapter != adapter) {
            target.adapter = adapter
        }
    }

    override fun onReset(target: RecyclerView) {
        super.onReset(target)
        target.adapter = null
    }

    override fun onSaveState(target: RecyclerView): Parcelable? {
        return (target.layoutManager as? LinearLayoutManager)?.onSaveInstanceState()
    }

    override fun onRestoreState(target: RecyclerView, state: Parcelable) {
        (target.layoutManager as? LinearLayoutManager)?.onRestoreInstanceState(state)
    }
}