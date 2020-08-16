package com.example.app.presentation

import androidx.lifecycle.LiveData

/**
 * ViewModel that represents the data on the screen.
 */
interface ViewModel<A, N> {
    /**
     * @return - [ViewState] view states to apply their actions to view
     */
    val viewStateLiveData: LiveData<ViewState<A>>

    /**
     * @return - [N] navigation events
     */
    val navigationLiveData: LiveData<N>
}