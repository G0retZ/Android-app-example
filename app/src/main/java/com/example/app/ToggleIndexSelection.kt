package com.example.app

import kotlinx.coroutines.flow.FlowCollector

class ToggleIndexSelection(private val collector: FlowCollector<Int>) : FlowCollector<Int> {
    private var oldValue = -1
    private var initialized = false

    override suspend fun emit(value: Int) = (if (value == oldValue) -1 else value)
        .takeIf { it != oldValue || !initialized }
        ?.also {
            initialized = true
            oldValue = it
        }
        ?.let { collector.emit(it) }
        ?: Unit
}
