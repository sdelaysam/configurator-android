package org.sdelaysam.configurator.sample.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

/**
 * Created on 04.03.2021.
 * @author sdelaysam
 */

class Section(val id: Int) {

    val tiles = MutableStateFlow(DataProvider.tiles.shuffled())

    val isLoading = MutableStateFlow(false)

    fun shuffle(): Flow<Unit> {
        return flow {
            isLoading.value = true
            delay(1000L)
            isLoading.value = false
            tiles.value = DataProvider.tiles.shuffled()
        }
    }
}