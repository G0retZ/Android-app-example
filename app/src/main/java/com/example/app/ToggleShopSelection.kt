package com.example.app

import kotlinx.coroutines.flow.FlowCollector

class ToggleShopSelection(private val collector: FlowCollector<Shop?>) : FlowCollector<Shop?> {
    private var oldValue: Shop? = null
    private var initialized = false

    override suspend fun emit(value: Shop?) {
        val newValue = if (value == oldValue) null else value
        if (newValue != oldValue || !initialized) {
            initialized = true
            oldValue = newValue
            collector.emit(newValue)
        }
    }
}
