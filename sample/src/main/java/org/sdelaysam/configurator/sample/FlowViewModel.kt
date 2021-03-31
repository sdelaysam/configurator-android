package org.sdelaysam.configurator.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import org.sdelaysam.configurator.adapter.AdapterEntry
import org.sdelaysam.configurator.sample.configurator.ButtonConfigurator
import org.sdelaysam.configurator.sample.configurator.TileConfigurator
import org.sdelaysam.configurator.sample.configurator.VerticalFlowConfigurator
import org.sdelaysam.configurator.sample.data.DataProvider

/**
 * Created on 31.03.2021.
 * @author sdelaysam
 */
class FlowViewModel : ViewModel() {

    private val tiles = MutableStateFlow(emptyList<AdapterEntry>())

    private val isLoading = MutableStateFlow(false)

    val configurator = VerticalFlowConfigurator(tiles)

    val button = ButtonConfigurator(
        textRes = R.string.insert,
        isLoading = isLoading,
        onClick = ::onButtonClick
    )

    private fun onButtonClick() {
        flow<Unit> {
            isLoading.value = true
            delay(1000)
            isLoading.value = false
            insert()
        }.launchIn(viewModelScope)
    }

    private fun insert() {
        val tiles = this.tiles.value
        val index = tiles.size % DataProvider.tiles.size
        val tile = DataProvider.tiles[index]
        val entry = TileConfigurator(tile, false)
        this.tiles.value = tiles.toMutableList().apply { add(0, entry) }
    }

}