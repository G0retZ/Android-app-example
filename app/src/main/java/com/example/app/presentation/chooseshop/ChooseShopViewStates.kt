package com.example.app.presentation.chooseshop

import com.example.app.presentation.ViewState

data class ChooseShopViewStateError(val errorMessage: String) :
    ViewState<ChooseShopViewActions> {
    override fun apply(actions: ChooseShopViewActions) {
        actions.showShopList(false)
        actions.showShopListPending(false)
        actions.showShopListErrorMessage(true)
        actions.setShopListErrorMessage(errorMessage)
        actions.showShopListRetryButton(true)
    }
}

class ChooseShopViewStatePending : ViewState<ChooseShopViewActions> {
    override fun apply(actions: ChooseShopViewActions) {
        actions.showShopList(false)
        actions.showShopListErrorMessage(false)
        actions.showShopListRetryButton(false)
        actions.showShopListPending(true)
    }
}

data class ChooseShopViewStateReady(private val chooseShopListItems: List<ChooseShopListItem>) :
    ViewState<ChooseShopViewActions> {
    override fun apply(actions: ChooseShopViewActions) {
        actions.showShopList(true)
        actions.showShopListPending(false)
        actions.showShopListErrorMessage(false)
        actions.showShopListRetryButton(false)
        actions.setShopListItems(chooseShopListItems)
    }
}
