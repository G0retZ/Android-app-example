package com.example.app

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ToggleIndexSelectionTest {

    /**
     * Should pass selection without duplicates.
     */
    @Test
    fun shouldPassSelection() = runTest {
        // Given:
        val values = mutableListOf<Int>()
        val selector = ToggleIndexSelection(values::add)

        // Action:
        selector.emit(-1)
        selector.emit(0)
        selector.emit(-1)
        selector.emit(-1)
        selector.emit(2)
        selector.emit(3)
        selector.emit(0)

        // Effect:
        assertEquals(listOf(-1, 0, -1, 2, 3, 0), values)
    }


    /**
     * Should deselect (set to -1) on selection repeat.
     */
    @Test
    fun shouldDeselectOnRepeat() = runTest {
        // Given:
        val values = mutableListOf<Int>()
        val selector = ToggleIndexSelection(values::add)

        // Action:
        selector.emit(-1)
        selector.emit(-1)
        selector.emit(0)
        selector.emit(0)
        selector.emit(1)
        selector.emit(1)
        selector.emit(2)
        selector.emit(2)

        // Effect:
        assertEquals(listOf(-1, 0, -1, 1, -1, 2, -1), values)
    }
}
