package org.sdelaysam.configurator

import android.os.Parcelable

/**
 * Created on 04.03.2021.
 * @author sdelaysam
 */

interface StatefulConfigurator<T> : Configurator<T> {
    fun onSaveState(target: T): Parcelable?
    fun onRestoreState(target: T, state: Parcelable)
}