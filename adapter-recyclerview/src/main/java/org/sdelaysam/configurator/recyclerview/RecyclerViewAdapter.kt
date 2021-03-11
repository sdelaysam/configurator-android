package org.sdelaysam.configurator.recyclerview

import android.os.Parcelable
import android.util.SparseArray
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import kotlinx.parcelize.Parcelize
import org.sdelaysam.configurator.adapter.AdapterEntry
import org.sdelaysam.configurator.adapter.createDiffCallback
import org.sdelaysam.configurator.adapter.setList

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */
open class RecyclerViewAdapter(
    private val useDiffer: Boolean = true
) : RecyclerView.Adapter<BasicViewHolder>() {

    private val factories = mutableMapOf<Int, BasicViewHolder.Factory>()

    private var viewStates = SparseArray<Parcelable?>()

    private val differ by lazy(LazyThreadSafetyMode.NONE) {
        AsyncListDiffer(this, createDiffCallback())
    }

    private val sharedPool by lazy(LazyThreadSafetyMode.NONE) {
        RecyclerView.RecycledViewPool()
    }

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    val items: List<AdapterEntry>
        get() = differ.currentList

    fun register(factory: BasicViewHolder.Factory) {
        factories[factory.viewType] = factory
    }

    fun reload(list: List<AdapterEntry>, animated: Boolean = true) {
        val fullReload = !useDiffer || !animated || itemCount == 0
        if (fullReload && differ.setList(list)) {
            notifyDataSetChanged()
        } else {
            differ.submitList(list)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return differ.currentList[position].viewType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicViewHolder {
        val factory = factories[viewType]
            ?: throw IllegalArgumentException("No factory registered for view type: $viewType")
        val viewHolder = factory.create(parent)
        if (viewHolder is RequiresRecyclerViewPool) {
            viewHolder.setRecyclerViewPool(sharedPool)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: BasicViewHolder, position: Int) {
        holder.onBind(differ.currentList[position])
        holder.onRestoreState(viewStates)
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