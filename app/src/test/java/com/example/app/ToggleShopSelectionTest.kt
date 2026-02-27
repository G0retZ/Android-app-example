package com.example.app

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ToggleShopSelectionTest {

    /**
     * Should pass selection without duplicates.
     */
    @Test
    fun shouldPassSelection() = runTest {
        // Given:
        val values = mutableListOf<Shop?>()
        val selector = ToggleShopSelection(values::add)

        // Action:
        selector.emit(null)
        selector.emit(Shop(id = "0"))
        selector.emit(null)
        selector.emit(null)
        selector.emit(Shop(id = "2"))
        selector.emit(Shop(id = "3"))
        selector.emit(Shop(id = "0"))

        // Effect:
        assertEquals(
            listOf(
                null,
                Shop(id = "0"),
                null,
                Shop(id = "2"),
                Shop(id = "3"),
                Shop(id = "0")
            ),
            values
        )
    }


    /**
     * Should deselect (set to null) on selection repeat.
     */
    @Test
    fun shouldDeselectOnRepeat() = runTest {
        // Given:
        val values = mutableListOf<Shop?>()
        val selector = ToggleShopSelection(values::add)

        // Action:
        selector.emit(null)
        selector.emit(null)
        selector.emit(Shop(id = "0"))
        selector.emit(Shop(id = "0"))
        selector.emit(Shop(id = "1"))
        selector.emit(Shop(id = "1"))
        selector.emit(Shop(id = "2"))
        selector.emit(Shop(id = "2"))

        // Effect:
        assertEquals(
            listOf(
                null,
                Shop(id = "0"),
                null,
                Shop(id = "1"),
                null,
                Shop(id = "2"),
                null
            ),
            values
        )
    }
}
