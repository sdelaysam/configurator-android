package org.sdelaysam.configurator.adapter

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

interface AdapterEntry {
    val viewType: Int
    val contentId: Int
    val contentHash: Int
}

val Iterable<AdapterEntry>.contentHash: Int
    get() = fold(0) { acc, it -> acc * 31 + it.contentHash}