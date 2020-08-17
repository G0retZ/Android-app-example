package com.example.app.presentation.selectedshop

/**
 * Actions to change view of selected shop.
 */
interface SelectedShopViewActions {

    fun setName(name: String)

    fun setPicture(url: String?)

    fun setStreet(address: String)

    fun setCity(city: String)

}

const val CLOSE_SELECTION_DETAILS = "SelectionDetails.to.Close"
