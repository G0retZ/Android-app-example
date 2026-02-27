package com.example.app.presentation.selectedshop

import com.example.app.Address
import com.example.app.Photo
import com.example.app.Shop
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class SelectedShopStatesTest {
    private lateinit var viewActions: SelectedShopViewActionsMock

    @Before
    fun setUp() {
        viewActions = SelectedShopViewActionsMock()
    }

    @Test
    fun testSelectedWithoutDataActions() {
        // Action:
        SelectedShopStateSelected(Shop(id = "2")).apply(viewActions)

        // Effect:
        assertEquals(
            SelectedShopViewStateResult(
                name = "No name",
                url = null,
                address = "",
                city = ""
            ),
            viewActions.result
        )
    }

    @Test
    fun testSelectedWithNullDataActions() {
        // Action:
        SelectedShopStateSelected(
            Shop(
                name = null,
                photos = listOf(),
                address = null
            )
        ).apply(viewActions)

        // Effect:
        assertEquals(
            SelectedShopViewStateResult(
                name = "No name",
                url = null,
                address = "",
                city = ""
            ),
            viewActions.result
        )
    }

    @Test
    fun testSelectedWithNullDataInAddressActions() {
        // Action:
        SelectedShopStateSelected(
            Shop(
                name = null,
                photos = listOf(),
                address = Address(street2 = null)
            )
        ).apply(viewActions)

        // Effect:
        assertEquals(
            SelectedShopViewStateResult(
                name = "No name",
                url = null,
                address = "",
                city = ""
            ),
            viewActions.result
        )
    }

    @Test
    fun testSelectedWithEmptyDataActions() {
        // Action:
        SelectedShopStateSelected(
            Shop(
                name = "",
                photos = listOf(Photo(url = "")),
                address = Address(street = "", street2 = "", city = "")
            )
        ).apply(viewActions)

        // Effect:
        assertEquals(
            SelectedShopViewStateResult(
                name = "No name",
                url = null,
                address = "",
                city = ""
            ),
            viewActions.result
        )
    }

    @Test
    fun testSelectedWithBlankDataActions() {
        // Action:
        SelectedShopStateSelected(
            Shop(
                name = " ",
                photos = listOf(Photo(url = " ")),
                address = Address(street = " ", street2 = " ", city = " ")
            )
        ).apply(viewActions)

        // Effect:
        assertEquals(
            SelectedShopViewStateResult(
                name = "No name",
                url = null,
                address = "",
                city = ""
            ),
            viewActions.result
        )
    }

    @Test
    fun testSelectedWithDataActions() {
        // Action:
        SelectedShopStateSelected(
            Shop(
                name = "Super shop",
                photos = listOf(Photo(url = "url")),
                address = Address(street = "street", street2 = null, city = "city")
            )
        ).apply(viewActions)

        // Effect:
        assertEquals(
            SelectedShopViewStateResult(
                name = "Super shop",
                url = "url",
                address = "street",
                city = "city"
            ),
            viewActions.result
        )
    }

    @Test
    fun testSelectedWithStreet2DataActions() {
        // Action:
        SelectedShopStateSelected(
            Shop(
                name = "Super shop",
                photos = listOf(Photo(url = "url")),
                address = Address(street = " ", street2 = "street 2", city = "city")
            )
        ).apply(viewActions)

        // Effect:
        assertEquals(
            SelectedShopViewStateResult(
                name = "Super shop",
                url = "url",
                address = "street 2",
                city = "city"
            ),
            viewActions.result
        )
    }

    @Test
    fun testReadyEquals() {
        assertEquals(
            SelectedShopStateSelected(Shop(id = "2")),
            SelectedShopStateSelected(Shop(id = "2"))
        )
        assertNotEquals(
            SelectedShopStateSelected(Shop(id = "2")),
            SelectedShopStateSelected(Shop(id = "3"))
        )
    }

    @Test
    fun testNotSelectedActions() {
        // Action:
        SelectedShopStateNotSelected.apply(viewActions)

        // Effect:
        assertEquals(
            SelectedShopViewStateResult(
                name = "Not selected",
                url = null,
                address = "",
                city = ""
            ),
            viewActions.result
        )
    }
}

data class SelectedShopViewStateResult(
    var name: String? = null,
    var url: String? = null,
    var address: String? = null,
    var city: String? = null
)

class SelectedShopViewActionsMock : SelectedShopViewActions {
    var result = SelectedShopViewStateResult()

    override fun setName(name: String) {
        result = result.copy(name = name)
    }

    override fun setPicture(url: String?) {
        result = result.copy(url = url)
    }

    override fun setStreet(address: String) {
        result = result.copy(address = address)
    }

    override fun setCity(city: String) {
        result = result.copy(city = city)
    }
}