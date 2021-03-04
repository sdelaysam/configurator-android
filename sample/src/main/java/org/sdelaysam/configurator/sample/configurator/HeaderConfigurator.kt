package org.sdelaysam.configurator.sample.configurator

import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.sdelaysam.configurator.GenerateViewHolder
import org.sdelaysam.configurator.adapter.AdapterCoroutineConfigurator
import org.sdelaysam.configurator.sample.R
import org.sdelaysam.configurator.sample.VIEW_TYPE_HEADER
import org.sdelaysam.configurator.sample.databinding.ViewHeaderBinding
import org.sdelaysam.configurator.sample.util.bind

/**
 * Created on 04.03.2021.
 * @author sdelaysam
 */

@GenerateViewHolder
class HeaderConfigurator(
    override val contentId: Int,
    private val title: String,
    private val isLoading: Flow<Boolean>,
    private val onClick: () -> Unit
) : AdapterCoroutineConfigurator<ViewHeaderBinding>() {

    override fun CoroutineScope.onConfigure(target: ViewHeaderBinding) {
        target.titleText.text = title
        target.button.setText(R.string.shuffle)
        target.button.setOnClickListener { onClick() }
        bind(isLoading) {
            target.progressBar.isVisible = it
            target.button.isEnabled = !it
        }
    }

    override val viewType: Int = VIEW_TYPE_HEADER
    override val contentHash: Int = title.hashCode()
}