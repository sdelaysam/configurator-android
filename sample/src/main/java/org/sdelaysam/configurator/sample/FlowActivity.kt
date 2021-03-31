package org.sdelaysam.configurator.sample

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import org.sdelaysam.configurator.sample.databinding.ActivityFlowBinding
import org.sdelaysam.configurator.sample.databinding.ActivityMainBinding
import org.sdelaysam.configurator.sample.util.bind

/**
 * Created on 31.03.2021.
 * @author sdelaysam
 */
class FlowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFlowBinding

    private val viewModel: FlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bind(viewModel.configurator, binding.root)
        bind(viewModel.button, binding.buttonLayout)
    }
}

