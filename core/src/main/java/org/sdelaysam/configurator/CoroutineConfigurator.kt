package org.sdelaysam.configurator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import java.lang.ref.WeakReference

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

abstract class CoroutineConfigurator<T> : Configurator<T>, RequiresCoroutineScope {

    private var coroutineScope: WeakReference<CoroutineScope>? = null

    final override fun setCoroutineScope(coroutineScope: CoroutineScope) {
        this.coroutineScope = WeakReference(coroutineScope)
    }

    final override fun configure(target: T) {
        val scope = coroutineScope?.get() ?: return
        scope.onConfigure(target)
    }

    final override fun reset(target: T) {
        onReset(target)
        coroutineScope?.get()?.cancel()
        coroutineScope?.clear()
        coroutineScope = null
    }

    protected abstract fun CoroutineScope.onConfigure(target: T)

    protected open fun onReset(target: T) {}
}