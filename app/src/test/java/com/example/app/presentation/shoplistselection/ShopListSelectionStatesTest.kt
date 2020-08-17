package com.example.app.presentation.shoplistselection

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.MockitoAnnotations

class ShopListSelectionStatesTest {
    @Mock
    private lateinit var viewActions: ShopListSelectionViewActions

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testSelectedActions() {
        // Действие:
        ShopListSelectionStateSelected(2).apply(viewActions)

        // Результат:
        verify(viewActions).showAcceptButton(true)
        verify(viewActions).showSelectedIndex(2)
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
        verify(viewActions).showAcceptButton(false)
        verify(viewActions).showSelectedIndex(null)
        verifyNoMoreInteractions(viewActions)
    }
}