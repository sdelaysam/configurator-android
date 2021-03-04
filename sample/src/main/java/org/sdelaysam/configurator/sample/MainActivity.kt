package org.sdelaysam.configurator.sample

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import org.sdelaysam.configurator.sample.databinding.ActivityMainBinding
import org.sdelaysam.configurator.sample.util.bind

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bind(viewModel.list, binding.recyclerView)
        bind(viewModel.button, binding.buttonLayout)
    }
}

