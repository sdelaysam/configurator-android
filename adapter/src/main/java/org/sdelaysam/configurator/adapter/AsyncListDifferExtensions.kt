package org.sdelaysam.configurator.adapter

import androidx.recyclerview.widget.AsyncListDiffer
import java.util.*

/**
 * Created on 07.02.2021.
 * @author sdelaysam
 */

fun <T> AsyncListDiffer<T>.setList(list: List<T>): Boolean {
    return try {
        var field = AsyncListDiffer::class.java.getDeclaredField("mList")
        field.isAccessible = true
        field.set(this, list)
        field.isAccessible = false

        field = AsyncListDiffer::class.java.getDeclaredField("mReadOnlyList")
        field.isAccessible = true
        field.set(this, Collections.unmodifiableList(list))
        field.isAccessible = false
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}