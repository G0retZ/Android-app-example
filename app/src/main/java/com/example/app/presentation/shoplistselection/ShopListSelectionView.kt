package com.example.app.presentation.shoplistselection

/**
 * Actions to change view of shop selection.
 */
interface ShopListSelectionViewActions {

    fun showSelectedIndex(index: Int?)

    fun showAcceptButton(show: Boolean)
}

const val TO_SELECTION_DETAILS = "ShopListSelection.to.SelectionDetails"
