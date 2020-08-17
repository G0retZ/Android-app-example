package com.example.app.presentation.selectedshop

import com.example.app.Shop
import com.example.app.presentation.ViewState

data class SelectedShopStateSelected(val shop: Shop) :
    ViewState<SelectedShopViewActions> {
    override fun apply(actions: SelectedShopViewActions) {
        actions.setName(shop.name?.ifBlank { null } ?: "No name")
        actions.setPicture(shop.photos.getOrNull(0)?.url?.ifBlank { null })
        actions.setStreet(
            shop.address?.street?.ifBlank { null }
                ?: shop.address?.street2?.trim().orEmpty()
        )
        actions.setCity(shop.address?.city?.ifBlank { null } ?: "")
    }
}

class SelectedShopStateNotSelected : ViewState<SelectedShopViewActions> {
    override fun apply(actions: SelectedShopViewActions) {
        actions.setName("Not selected")
        actions.setPicture(null)
        actions.setStreet("")
        actions.setCity("")
    }
}
