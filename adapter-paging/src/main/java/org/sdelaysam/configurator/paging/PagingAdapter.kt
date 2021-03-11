package org.sdelaysam.configurator.paging

import android.os.Parcelable
import android.util.SparseArray
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import kotlinx.parcelize.Parcelize
import org.sdelaysam.configurator.adapter.AdapterEntry
import org.sdelaysam.configurator.adapter.createDiffCallback
import org.sdelaysam.configurator.recyclerview.BasicViewHolder

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

open class PagingAdapter(
    private val emptyItemViewType: Int
) : PagingDataAdapter<AdapterEntry, BasicViewHolder>(createDiffCallback()) {

    private val factories = mutableMapOf<Int, BasicViewHolder.Factory>()

    private var viewStates = SparseArray<Parcelable?>()

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    val items: List<AdapterEntry?>
        get() = snapshot()

    fun register(factory: BasicViewHolder.Factory) {
        factories[factory.viewType] = factory
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.viewType ?: emptyItemViewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicViewHolder {
        val factory = factories[viewType]
            ?: throw IllegalArgumentException("No factory registered for view type: $viewType")
        return factory.create(parent)
    }

    override fun onBindViewHolder(holder: BasicViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.onBind(item)
            holder.onRestoreState(viewStates)
        }
    }

    override fun onViewAttachedToWindow(holder: BasicViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttach()
    }

    override fun onViewDetachedFromWindow(holder: BasicViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetach()
    }

    override fun onViewRecycled(holder: BasicViewHolder) {
        super.onViewRecycled(holder)
        holder.onSaveState(viewStates)
        holder.onRecycled()
    }

    fun onSaveInstanceState(): Parcelable {
        return SavedState(viewStates)
    }

    fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            viewStates = state.viewStates
        }
    }

    @Parcelize
    class SavedState(
        val viewStates: SparseArray<Parcelable?>
    ) : Parcelable

}