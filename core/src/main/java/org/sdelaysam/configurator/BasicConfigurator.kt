package org.sdelaysam.configurator

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

abstract class BasicConfigurator<T> : Configurator<T> {

    final override fun configure(target: T) = onConfigure(target)

    final override fun reset(target: T) = onReset(target)

    protected abstract fun onConfigure(target: T)

    protected open fun onReset(target: T) {}
}