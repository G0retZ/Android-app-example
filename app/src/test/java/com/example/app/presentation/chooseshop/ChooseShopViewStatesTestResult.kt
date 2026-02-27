package com.example.app.presentation.chooseshop

import com.example.app.Shop
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class ChooseShopViewStatesTestResult {
    private lateinit var viewActions: ChooseShopViewActionsMock

    @Before
    fun setUp() {
        viewActions = ChooseShopViewActionsMock()
    }

    @Test
    fun testErrorActions() {
        // Action:
        ChooseShopViewStateError("Error").apply(viewActions)

        // Effect:
        assertEquals(
            ChooseShopViewStateResult(
                showShopList = false,
                showShopListPending = false,
                showShopListErrorMessage = true,
                shopListErrorMessage = "Error",
                showShopListRetryButton = true
            ),
            viewActions.result
        )
    }

    @Test
    fun testErrorEquals() {
        assertEquals(ChooseShopViewStateError("Error"), ChooseShopViewStateError("Error"))
        assertNotEquals(ChooseShopViewStateError("Error"), ChooseShopViewStateError("or"))
    }

    @Test
    fun testReadyActions() {
        // Action:
        ChooseShopViewStateReady(listOf()).apply(viewActions)

        // Effect:
        assertEquals(
            ChooseShopViewStateResult(
                showShopList = true,
                showShopListPending = false,
                showShopListErrorMessage = false,
                showShopListRetryButton = false,
                shopListItems = listOf()
            ),
            viewActions.result
        )
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
        // Action:
        ChooseShopViewStatePending.apply(viewActions)

        // Effect:
        assertEquals(
            ChooseShopViewStateResult(
                showShopList = false,
                showShopListPending = true,
                showShopListErrorMessage = false,
                showShopListRetryButton = false
            ),
            viewActions.result
        )
    }
}

data class ChooseShopViewStateResult(
    val showShopList: Boolean? = null,
    val showShopListPending: Boolean? = null,
    val showShopListErrorMessage: Boolean? = null,
    val shopListErrorMessage: String? = null,
    val showShopListRetryButton: Boolean? = null,
    val shopListItems: List<ChooseShopListItem>? = null
)

class ChooseShopViewActionsMock : ChooseShopViewActions {
    var result = ChooseShopViewStateResult()
        private set

    override fun showShopList(show: Boolean) {
        result = result.copy(showShopList = show)
    }

    override fun showShopListPending(show: Boolean) {
        result = result.copy(showShopListPending = show)
    }

    override fun showShopListErrorMessage(show: Boolean) {
        result = result.copy(showShopListErrorMessage = show)
    }

    override fun setShopListErrorMessage(message: String) {
        result = result.copy(shopListErrorMessage = message)
    }

    override fun showShopListRetryButton(show: Boolean) {
        result = result.copy(showShopListRetryButton = show)
    }

    override fun setShopListItems(chooseShopListItems: List<ChooseShopListItem>) {
        result = result.copy(shopListItems = chooseShopListItems)
    }
}