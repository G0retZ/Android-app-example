package com.example.app.presentation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel that represents the data on the screen.
 */
interface ViewModel<A, N> {
    /**
     * @return - [ViewState] view states to apply their actions to view
     */
    val viewStates: StateFlow<ViewState<A>>

    /**
     * @return - [N] navigation events
     */
    val navigation: Flow<N>
}