package com.example.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory for [ViewModel] creation
 *
 * @param <V> class or interface, that extended or implemented by [ViewModel]
</V> */
class ViewModelFactory<V>(private val viewModel: V) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        viewModel as? T ?: throw IllegalArgumentException("Unknown class name")

}