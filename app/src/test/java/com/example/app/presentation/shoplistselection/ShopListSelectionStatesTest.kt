package com.example.app.presentation.shoplistselection

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.ArgumentMatchers.isNull
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions

class ShopListSelectionStatesTest {
    private lateinit var viewActions: ShopListSelectionViewActions

    @Before
    fun setUp() {
        viewActions = mock(ShopListSelectionViewActions::class.java)
    }

    @Test
    fun testSelectedActions() {
        // Действие:
        ShopListSelectionStateSelected(2).apply(viewActions)

        // Результат:
        verify(viewActions).showAcceptButton(eq(true))
        verify(viewActions).showSelectedIndex(eq(2))
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testReadyEquals() {
        assertEquals(ShopListSelectionStateSelected(2), ShopListSelectionStateSelected(2))
        assertNotEquals(ShopListSelectionStateSelected(2), ShopListSelectionStateSelected(3))
    }

    @Test
    fun testNotSelectedActions() {
        // Действие:
        ShopListSelectionStateNotSelected().apply(viewActions)

        // Результат:
        verify(viewActions).showAcceptButton(eq(false))
        verify(viewActions).showSelectedIndex(isNull())
        verifyNoMoreInteractions(viewActions)
    }
}