package com.example.app.presentation

/**
 * State that is applied to View using provided actions. Can be used to restore previous state
 * or to apply difference.
 *
 * @param <A> actions to apply to View
</A> */
interface ViewState<A> {
    /**
     * @param [actions] View actions, to apply the state
     */
    fun apply(actions: A)
}