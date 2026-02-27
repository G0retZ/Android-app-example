package com.example.app.presentation.shoplistselection

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class ShopListSelectionStatesTest {
    private lateinit var viewActions: ShopListSelectionViewActionsMock

    @Before
    fun setUp() {
        viewActions = ShopListSelectionViewActionsMock()
    }

    @Test
    fun testSelectedActions() {
        // Action:
        ShopListSelectionStateSelected(2).apply(viewActions)

        // Effect:
        assertEquals(
            ShopListSelectionViewStateResult(
                showAcceptButton = true,
                selectedIndex = 2
            ),
            viewActions.result
        )
    }

    @Test
    fun testReadyEquals() {
        assertEquals(ShopListSelectionStateSelected(2), ShopListSelectionStateSelected(2))
        assertNotEquals(ShopListSelectionStateSelected(2), ShopListSelectionStateSelected(3))
    }

    @Test
    fun testNotSelectedActions() {
        // Action:
        ShopListSelectionStateNotSelected.apply(viewActions)

        // Effect:
        assertEquals(
            ShopListSelectionViewStateResult(
                showAcceptButton = false,
                selectedIndex = null
            ),
            viewActions.result
        )
    }
}

data class ShopListSelectionViewStateResult(
    val showAcceptButton: Boolean = false,
    val selectedIndex: Int? = null
)

class ShopListSelectionViewActionsMock : ShopListSelectionViewActions {
    var result: ShopListSelectionViewStateResult = ShopListSelectionViewStateResult()
        private set

    override fun showAcceptButton(show: Boolean) {
        result = result.copy(showAcceptButton = show)
    }

    override fun showSelectedIndex(index: Int?) {
        result = result.copy(selectedIndex = index)
    }
}