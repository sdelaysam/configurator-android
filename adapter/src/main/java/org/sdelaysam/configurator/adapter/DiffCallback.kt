package org.sdelaysam.configurator.adapter

import androidx.recyclerview.widget.DiffUtil

/**
 * Created on 06.02.2021.
 * @author sdelaysam
 */

fun <T : AdapterEntry> createDiffCallback() = object : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.viewType == newItem.viewType && oldItem.contentId == newItem.contentId
    }
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.contentHash == newItem.contentHash
    }
}
