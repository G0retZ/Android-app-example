package com.example.app.presentation.chooseshop

/**
 * Actions to change view of shops list.
 */
interface ChooseShopViewActions {
    /**
     * @param show - show progress indicator or not
     */
    fun showShopListPending(show: Boolean)

    /**
     * @param show - make list of shops visible
     */
    fun showShopList(show: Boolean)

    /**
     * @param chooseShopListItems - a list of items
     */
    fun setShopListItems(chooseShopListItems: List<ChooseShopListItem>)

    /**
     * @param show - make error message visible
     */
    fun showShopListErrorMessage(show: Boolean)

    /**
     * @param message - set error message
     */
    fun setShopListErrorMessage(message: String)

    /**
     * @param show - make retry button visible
     */
    fun showShopListRetryButton(show: Boolean)
}

const val CLOSE_CHOOSE_SHOP = "ShopListSelection.to.Close"