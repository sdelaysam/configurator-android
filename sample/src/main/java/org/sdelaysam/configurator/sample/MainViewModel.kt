package org.sdelaysam.configurator.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import org.sdelaysam.configurator.adapter.AdapterEntry
import org.sdelaysam.configurator.sample.configurator.*
import org.sdelaysam.configurator.sample.data.DataProvider
import org.sdelaysam.configurator.sample.data.Section
import org.sdelaysam.configurator.sample.data.Tile

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

class MainViewModel : ViewModel() {

    private val tiles = MutableStateFlow(emptyList<AdapterEntry>())

    private val sections = (0..10).map(::Section)

    private val isLoading = MutableStateFlow(false)

    val list = ListConfigurator(tiles)

    val button = ButtonConfigurator(
        textRes = R.string.shuffle,
        isLoading = isLoading,
        onClick = ::onButtonClick
    )

    init {
        shuffle()
    }

    private fun onButtonClick() {
        flow<Unit> {
            isLoading.value = true
            delay(1000)
            isLoading.value = false
            shuffle()
        }.launchIn(viewModelScope)
    }

    private fun onSectionClick(section: Section) {
        section.shuffle().launchIn(viewModelScope)
    }

    private fun shuffle() {
        tiles.value = sections.shuffled()
            .flatMap { listOf(buildHeader(it), buildCarousel(it)) }
            .plus(
                DataProvider.tiles.shuffled().toItems(false)
            )
    }

    private fun buildHeader(section: Section): AdapterEntry {
        return HeaderConfigurator(
            contentId = section.id,
            title = "Section ${section.id}",
            isLoading = section.isLoading,
            onClick = { onSectionClick(section) }
        )
    }

    private fun buildCarousel(section: Section): AdapterEntry {
        return CarouselConfigurator(
            items = section.tiles.map { it.toItems(true) },
            contentId = section.id
        )
    }

    private fun List<Tile>.toItems(isSmall: Boolean): List<AdapterEntry> {
        return map {
            TileConfigurator(tile = it, isSmall = isSmall)
        }
    }
}
