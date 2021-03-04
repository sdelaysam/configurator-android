package org.sdelaysam.configurator.sample.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

fun <T> CoroutineScope.bind(flow: Flow<T>, collector: suspend (T) -> Unit) = launch {
    flow.collect { collector(it) }
}