package org.sdelaysam.configurator.sample.configurator

import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.sdelaysam.configurator.CoroutineConfigurator
import org.sdelaysam.configurator.sample.databinding.ViewButtonBinding
import org.sdelaysam.configurator.sample.util.bind

/**
 * Created on 03.03.2021.
 * @author sdelaysam
 */

class ButtonConfigurator(
    private val textRes: Int,
    private val isLoading: Flow<Boolean>,
    private val onClick: () -> Unit
) : CoroutineConfigurator<ViewButtonBinding>() {

    override fun CoroutineScope.onConfigure(target: ViewButtonBinding) {
        target.button.setText(textRes)
        target.button.setOnClickListener { onClick() }
        bind(isLoading) {
            target.button.isEnabled = !it
            target.button.isActivated = it
            target.progressBar.isVisible = it
        }
    }
}