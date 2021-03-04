package org.sdelaysam.configurator.sample.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import org.sdelaysam.configurator.Configurator
import org.sdelaysam.configurator.RequiresCoroutineScope

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

fun <T> LifecycleOwner.bind(configurator: Configurator<T>, target: T) {
    if (configurator is RequiresCoroutineScope) {
        configurator.setCoroutineScope(lifecycleScope)
    }
    configurator.configure(target)
    lifecycle.addObserver(object: LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                configurator.reset(target)
                lifecycle.removeObserver(this)
            }
        }
    })
}