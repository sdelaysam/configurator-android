package org.sdelaysam.configurator

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

interface Configurator<T> {
    fun configure(target: T)
    fun reset(target: T)
}