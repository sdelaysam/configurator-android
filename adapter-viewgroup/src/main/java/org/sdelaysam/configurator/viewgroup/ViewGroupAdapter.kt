package org.sdelaysam.configurator.viewgroup

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.transition.Transition
import androidx.transition.TransitionManager
import org.sdelaysam.configurator.adapter.AdapterEntry
import org.sdelaysam.configurator.adapter.createDiffCallback
import org.sdelaysam.configurator.adapter.setList

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

abstract class ViewGroupAdapter(
    private val useDiffer: Boolean = true
) : ListUpdateCallback {

    private var view: ViewGroup? = null

    private val diffOperations = mutableListOf<DiffOperation>()

    private val factories = SparseArray<BasicViewHolder.Factory>()

    private val viewHolders = mutableListOf<BasicViewHolder>()

    private val differ by lazy(LazyThreadSafetyMode.NONE) {
        AsyncListDiffer(
            this,
            AsyncDifferConfig.Builder(createDiffCallback()).build()
        )
    }

    val items: List<AdapterEntry>
        get() = differ.currentList

    fun register(factory: BasicViewHolder.Factory) {
        factories.put(factory.viewType, factory)
    }

    fun reload(items: List<AdapterEntry>, animated: Boolean = true) {
        val fullReload = !useDiffer || !animated || differ.currentList.isEmpty()
        if (fullReload && differ.setList(items)) {
            view?.rebuild()
        } else {
            differ.submitList(items) {
                processDiff(animated)
            }
        }
    }

    fun attach(view: ViewGroup) {
        if (this.view == view) return
        this.view = view
        onAttach(view)
        view.rebuild()
    }

    fun detach(removeViews: Boolean = false) {
        val view = this.view ?: return
        onDetach(view)
        if (removeViews) {
            removeAllViews(view)
        }
        viewHolders.forEach {
            it.onRecycled()
        }
        viewHolders.clear()
        this.view = null
    }

    override fun onInserted(position: Int, count: Int) {
        diffOperations.add(DiffOperation.Insert(position, count))
    }

    override fun onRemoved(position: Int, count: Int) {
        diffOperations.add(DiffOperation.Remove(position, count))
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        diffOperations.add(DiffOperation.Move(fromPosition, toPosition))
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        diffOperations.add(DiffOperation.Change(position, count))
    }

    abstract fun addView(viewGroup: ViewGroup, view: View, position: Int)

    abstract fun moveView(viewGroup: ViewGroup, fromPosition: Int, toPosition: Int)

    abstract fun removeViews(viewGroup: ViewGroup, position: Int, count: Int)

    abstract fun removeAllViews(viewGroup: ViewGroup)

    protected open fun onAttach(viewGroup: ViewGroup) {}

    protected open fun onDetach(viewGroup: ViewGroup) {}

    protected open fun getTransition(viewGroup: ViewGroup): Transition? = null

    private fun processDiff(animated: Boolean) {
        val view = this.view ?: run {
            diffOperations.clear()
            return
        }

        if (diffOperations.size == 1) {
            if (animated) {
                TransitionManager.beginDelayedTransition(view, getTransition(view))
            }
            processOperation(diffOperations.first())
        } else {
            // if you ever want to implement it
            // take a look at OpReorderer first
            // good luck
            view.rebuild()
        }
        diffOperations.clear()
    }

    private fun processOperation(operation: DiffOperation) {
        when (operation) {
            is DiffOperation.Change -> {
                repeat(operation.count) {
                    val pos = operation.position + it
                    val data = differ.currentList[pos]
                    viewHolders[pos].onBind(data)
                }
            }
            is DiffOperation.Move -> {
                val viewHolder = viewHolders.removeAt(operation.fromPosition)
                viewHolders.add(operation.toPosition - 1, viewHolder)
                moveView(view!!, operation.fromPosition, operation.toPosition)
            }
            is DiffOperation.Insert -> {
                repeat(operation.count) { view!!.appendViewAt(operation.position + it) }
            }
            is DiffOperation.Remove -> {
                repeat(operation.count) {
                    val viewHolder = viewHolders.removeAt(operation.position)
                    viewHolder.onRecycled()
                }
                removeViews(view!!, operation.position, operation.count)
            }
        }
    }

    private fun ViewGroup.rebuild() {
        viewHolders.clear()
        removeAllViews(this)
        differ.currentList.indices.forEach {
            appendViewAt(it)
        }
    }

    private fun ViewGroup.appendViewAt(position: Int) {
        val data = differ.currentList[position]
        val factory = factories.get(data.viewType)
            ?: throw IllegalArgumentException("No factory registered for $data")
        val viewHolder = factory.create(this)
        if (position == viewHolders.size) {
            viewHolders.add(viewHolder)
        } else {
            viewHolders.add(position, viewHolder)
        }
        addView(this, viewHolder.view, position)
        viewHolder.onBind(data)
    }

    private sealed class DiffOperation {
        data class Change(val position: Int, val count: Int) : DiffOperation()
        data class Move(val fromPosition: Int, val toPosition: Int) : DiffOperation()
        data class Insert(val position: Int, val count: Int) : DiffOperation()
        data class Remove(val position: Int, val count: Int) : DiffOperation()
    }

}