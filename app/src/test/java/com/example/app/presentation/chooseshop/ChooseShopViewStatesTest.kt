package com.example.app.presentation.chooseshop

import com.example.app.Shop
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.MockitoAnnotations

class ChooseShopViewStatesTest {
    @Mock
    private lateinit var viewActions: ChooseShopViewActions

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testErrorActions() {
        // Действие:
        ChooseShopViewStateError("Error").apply(viewActions)

        // Результат:
        verify(viewActions).showShopList(false)
        verify(viewActions).showShopListPending(false)
        verify(viewActions).showShopListErrorMessage(true)
        verify(viewActions).setShopListErrorMessage("Error")
        verify(viewActions).showShopListRetryButton(true)
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testErrorEquals() {
        assertEquals(ChooseShopViewStateError("Error"), ChooseShopViewStateError("Error"))
        assertNotEquals(ChooseShopViewStateError("Error"), ChooseShopViewStateError("or"))
    }

    @Test
    fun testReadyActions() {
        // Действие:
        ChooseShopViewStateReady(listOf()).apply(viewActions)

        // Результат:
        verify(viewActions).showShopList(true)
        verify(viewActions).showShopListPending(false)
        verify(viewActions).showShopListErrorMessage(false)
        verify(viewActions).showShopListRetryButton(false)
        verify(viewActions).setShopListItems(listOf())
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testReadyEquals() {
        assertEquals(ChooseShopViewStateReady(listOf()), ChooseShopViewStateReady(listOf()))
        assertNotEquals(
            ChooseShopViewStateReady(listOf()),
            ChooseShopViewStateReady(listOf(ChooseShopListItem(Shop())))
        )
    }

    @Test
    fun testPendingActions() {
        // Действие:
        ChooseShopViewStatePending().apply(viewActions)

        // Результат:
        verify(viewActions).showShopList(false)
        verify(viewActions).showShopListPending(true)
        verify(viewActions).showShopListErrorMessage(false)
        verify(viewActions).showShopListRetryButton(false)
        verifyNoMoreInteractions(viewActions)
    }
}