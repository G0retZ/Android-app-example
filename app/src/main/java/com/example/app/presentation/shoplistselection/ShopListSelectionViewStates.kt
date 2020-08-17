package com.example.app.presentation.shoplistselection

import com.example.app.presentation.ViewState

data class ShopListSelectionStateSelected(val index: Int) :
    ViewState<ShopListSelectionViewActions> {
    override fun apply(actions: ShopListSelectionViewActions) {
        actions.showSelectedIndex(index)
        actions.showAcceptButton(true)
    }
}

class ShopListSelectionStateNotSelected : ViewState<ShopListSelectionViewActions> {
    override fun apply(actions: ShopListSelectionViewActions) {
        actions.showSelectedIndex(null)
        actions.showAcceptButton(false)
    }
}
