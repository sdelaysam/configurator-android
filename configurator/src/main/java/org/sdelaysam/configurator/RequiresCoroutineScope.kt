package org.sdelaysam.configurator

import kotlinx.coroutines.CoroutineScope

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

interface RequiresCoroutineScope {
    fun setCoroutineScope(coroutineScope: CoroutineScope)
}