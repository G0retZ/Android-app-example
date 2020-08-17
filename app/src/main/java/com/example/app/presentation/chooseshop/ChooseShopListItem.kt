package com.example.app.presentation.chooseshop

import com.example.app.Address
import com.example.app.Location
import com.example.app.Shop

data class ChooseShopListItem internal constructor(private val shop: Shop) {

    val name = shop.name?.ifBlank { null } ?: "No name"
    val thumbnail = shop.photos.getOrNull(0)?.thumbnail?.ifBlank { null }
    val address = shop.address
        ?.nullIfEmpty()
        ?.let { "${it.formattedStreet()}\n${it.formattedAddress()}" }
        ?: "Unknown address"
    val navigationQuery = (shop.location?.formattedLocation() ?: shop.address?.formattedLocation())
        ?.let { "google.navigation:q=$it" }

    private fun Address.nullIfEmpty(): Address? =
        if (street.isBlank()
            && street2.isNullOrBlank()
            && city.isBlank()
            && state.isBlank()
            && zip.isBlank()
        ) {
            null
        } else {
            this
        }

    private fun Address.formattedStreet(): String = street.ifBlank { null } ?: street2.orEmpty()
    private fun Address.formattedAddress(): String =
        "${formattedCity()}${formattedState()}${formattedZip()}".trim()

    private fun Address.formattedCity(): String = city.ifBlank { null }?.let { "$it," } ?: ""
    private fun Address.formattedState(): String = state.ifBlank { null }?.let { " $it" } ?: ""
    private fun Address.formattedZip(): String = zip.ifBlank { null }?.let { " $it" } ?: ""

    private fun Location.formattedLocation(): String? =
        if (lat != null && long != null && lat != 0.0 && long != 0.0) {
            "$lat,$long"
        } else {
            null
        }

    private fun Address.formattedLocation(): String? = nullIfEmpty()
        ?.let { formattedStreet().ifBlank { null } to formattedAddress().ifBlank { null } }
        ?.let {
            if (it.first.isNullOrBlank() && it.second.isNullOrBlank()) {
                null
            } else {
                "a+${it.first ?: ""}+${it.second ?: ""}"
            }
        }
}
