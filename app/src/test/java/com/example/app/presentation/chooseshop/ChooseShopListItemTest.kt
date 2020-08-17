package com.example.app.presentation.chooseshop

import com.example.app.Address
import com.example.app.Location
import com.example.app.Photo
import com.example.app.Shop
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ChooseShopListItemTest {

    @Test
    fun testName() {
        // Given
        val named = ChooseShopListItem(Shop(name = "Super shop"))
        val unNamed = ChooseShopListItem(Shop())
        val nullNamed = ChooseShopListItem(Shop(name = null))

        // Then
        assertEquals("Super shop", named.name)
        assertEquals("No name", unNamed.name)
        assertEquals("No name", nullNamed.name)
    }

    @Test
    fun testPicture() {
        // Given
        val withoutPhotos = ChooseShopListItem(Shop())
        val withZeroPhotos = ChooseShopListItem(Shop(photos = listOf()))
        val withEmptyPhoto = ChooseShopListItem(Shop(photos = listOf(Photo())))
        val withBlankPhoto = ChooseShopListItem(Shop(photos = listOf(Photo(thumbnail = " "))))
        val withPhoto = ChooseShopListItem(Shop(photos = listOf(Photo(thumbnail = "some url"))))
        val withPhotos = ChooseShopListItem(
            Shop(
                photos = listOf(
                    Photo(thumbnail = "some url"),
                    Photo(thumbnail = "other url")
                )
            )
        )

        // Then
        assertNull(withoutPhotos.thumbnail)
        assertNull(withZeroPhotos.thumbnail)
        assertNull(withEmptyPhoto.thumbnail)
        assertNull(withBlankPhoto.thumbnail)
        assertEquals("some url", withPhoto.thumbnail)
        assertEquals("some url", withPhotos.thumbnail)
    }

    @Test
    fun testAddress() {
        // Given
        val withoutAddress = ChooseShopListItem(Shop())
        val withEmptyAddress = ChooseShopListItem(Shop(address = Address()))
        val withBlankAddress =
            ChooseShopListItem(
                Shop(
                    address = Address(
                        street = " ",
                        street2 = " ",
                        city = " ",
                        state = " ",
                        zip = " "
                    )
                )
            )
        val withBlankAndNullAddress =
            ChooseShopListItem(
                Shop(
                    address = Address(
                        street = " ",
                        street2 = null,
                        city = " ",
                        state = " ",
                        zip = " "
                    )
                )
            )
        val withEmptyStreet = ChooseShopListItem(
            Shop(
                address = Address(
                    street2 = "street 2",
                    city = "city",
                    state = "US",
                    zip = "000"
                )
            )
        )
        val withEmptyStreet2 = ChooseShopListItem(
            Shop(
                address = Address(
                    street = "street",
                    city = "city",
                    state = "US",
                    zip = "000"
                )
            )
        )
        val withNullStreet2 = ChooseShopListItem(
            Shop(
                address = Address(
                    street = "street",
                    street2 = null,
                    city = "city",
                    state = "US",
                    zip = "000"
                )
            )
        )
        val withEmptyStreets = ChooseShopListItem(
            Shop(
                address = Address(
                    city = "city",
                    state = "US",
                    zip = "000"
                )
            )
        )
        val withEmptyCity = ChooseShopListItem(
            Shop(
                address = Address(
                    street = "street",
                    state = "US",
                    zip = "000"
                )
            )
        )
        val withEmptyState = ChooseShopListItem(
            Shop(
                address = Address(
                    street = "street",
                    city = "city",
                    zip = "000"
                )
            )
        )
        val withEmptyZip = ChooseShopListItem(
            Shop(
                address = Address(
                    street = "street",
                    city = "city",
                    state = "US"
                )
            )
        )
        val withEmptyCityStateAndZip = ChooseShopListItem(
            Shop(address = Address(street = "street"))
        )
        val fullAddress = ChooseShopListItem(
            Shop(
                address = Address(
                    street = "street",
                    street2 = "street 2",
                    city = "city",
                    state = "US",
                    zip = "000"
                )
            )
        )

        // Then
        assertEquals("Unknown address", withoutAddress.address)
        assertEquals("Unknown address", withEmptyAddress.address)
        assertEquals("Unknown address", withBlankAddress.address)
        assertEquals("Unknown address", withBlankAndNullAddress.address)
        assertEquals("street 2\ncity, US 000", withEmptyStreet.address)
        assertEquals("street\ncity, US 000", withEmptyStreet2.address)
        assertEquals("street\ncity, US 000", withNullStreet2.address)
        assertEquals("\ncity, US 000", withEmptyStreets.address)
        assertEquals("street\nUS 000", withEmptyCity.address)
        assertEquals("street\ncity, 000", withEmptyState.address)
        assertEquals("street\ncity, US", withEmptyZip.address)
        assertEquals("street\n", withEmptyCityStateAndZip.address)
        assertEquals("street\ncity, US 000", fullAddress.address)
    }


    @Test
    fun testNavigationQuery() {
        // Given
        val noLocationAndAddress = ChooseShopListItem(Shop())
        val emptyLocationAndAddress =
            ChooseShopListItem(Shop(location = Location(), address = Address()))
        val nullLocationAndNullAddress2 = ChooseShopListItem(
            Shop(
                location = Location(lat = null, long = null),
                address = Address(street2 = null)
            )
        )
        val zeroLocationAndBlankAddress = ChooseShopListItem(
            Shop(
                location = Location(lat = 0.0, long = 0.0),
                address = Address(
                    street = " ",
                    street2 = " ",
                    city = " ",
                    state = " ",
                    zip = " "
                )
            )
        )
        val noAddress =
            ChooseShopListItem(
                Shop(location = Location(lat = 1.0, long = 2.0))
            )
        val emptyAddress =
            ChooseShopListItem(
                Shop(
                    location = Location(lat = 1.0, long = 2.0),
                    address = Address()
                )
            )
        val nullAddress2 = ChooseShopListItem(
            Shop(
                location = Location(lat = 1.0, long = 2.0),
                address = Address(street2 = null)
            )
        )
        val blankAddress = ChooseShopListItem(
            Shop(
                location = Location(lat = 1.0, long = 2.0),
                address = Address(
                    street = " ",
                    street2 = " ",
                    city = " ",
                    state = " ",
                    zip = " "
                )
            )
        )
        val noLocation =
            ChooseShopListItem(
                Shop(
                    address = Address(
                        street = "street",
                        street2 = "street 2",
                        city = "city",
                        state = "US",
                        zip = "000"
                    )
                )
            )
        val noLocationAndNoStreet =
            ChooseShopListItem(
                Shop(
                    address = Address(
                        city = "city",
                        state = "US",
                        zip = "000"
                    )
                )
            )
        val noLocationAndStreet2 =
            ChooseShopListItem(
                Shop(
                    address = Address(
                        street2 = "street 2",
                        city = "city",
                        state = "US",
                        zip = "000"
                    )
                )
            )
        val noLocationAndNoCity =
            ChooseShopListItem(
                Shop(
                    address = Address(
                        street = "street",
                        street2 = "street 2",
                        state = "US",
                        zip = "000"
                    )
                )
            )
        val noLocationAndStreetOnly =
            ChooseShopListItem(
                Shop(
                    address = Address(street = "street")
                )
            )
        val noLocationAndNoState =
            ChooseShopListItem(
                Shop(
                    address = Address(
                        street = "street",
                        street2 = "street 2",
                        city = "city",
                        zip = "000"
                    )
                )
            )
        val noLocationAndNoZip =
            ChooseShopListItem(
                Shop(
                    address = Address(
                        street = "street",
                        street2 = "street 2",
                        city = "city",
                        state = "US"
                    )
                )
            )
        val emptyLocation =
            ChooseShopListItem(
                Shop(
                    location = Location(),
                    address = Address(
                        street = "street",
                        street2 = "street 2",
                        city = "city",
                        state = "US",
                        zip = "000"
                    )
                )
            )
        val nullLocation = ChooseShopListItem(
            Shop(
                location = Location(lat = null, long = null),
                address = Address(
                    street = "street",
                    street2 = "street 2",
                    city = "city",
                    state = "US",
                    zip = "000"
                )
            )
        )
        val zeroLocation = ChooseShopListItem(
            Shop(
                location = Location(lat = 0.0, long = 0.0),
                address = Address(
                    street = "street",
                    street2 = "street 2",
                    city = "city",
                    state = "US",
                    zip = "000"
                )
            )
        )

        // Then
        assertNull(noLocationAndAddress.navigationQuery)
        assertNull(emptyLocationAndAddress.navigationQuery)
        assertNull(nullLocationAndNullAddress2.navigationQuery)
        assertNull(zeroLocationAndBlankAddress.navigationQuery)
        assertEquals("google.navigation:q=1.0,2.0", noAddress.navigationQuery)
        assertEquals("google.navigation:q=1.0,2.0", emptyAddress.navigationQuery)
        assertEquals("google.navigation:q=1.0,2.0", nullAddress2.navigationQuery)
        assertEquals("google.navigation:q=1.0,2.0", blankAddress.navigationQuery)
        assertEquals("google.navigation:q=a+street+city, US 000", noLocation.navigationQuery)
        assertEquals("google.navigation:q=a++city, US 000", noLocationAndNoStreet.navigationQuery)
        assertEquals(
            "google.navigation:q=a+street 2+city, US 000",
            noLocationAndStreet2.navigationQuery
        )
        assertEquals("google.navigation:q=a+street+US 000", noLocationAndNoCity.navigationQuery)
        assertEquals("google.navigation:q=a+street+", noLocationAndStreetOnly.navigationQuery)
        assertEquals("google.navigation:q=a+street+city, 000", noLocationAndNoState.navigationQuery)
        assertEquals("google.navigation:q=a+street+city, US", noLocationAndNoZip.navigationQuery)
        assertEquals("google.navigation:q=a+street+city, US 000", emptyLocation.navigationQuery)
        assertEquals("google.navigation:q=a+street+city, US 000", nullLocation.navigationQuery)
        assertEquals("google.navigation:q=a+street+city, US 000", zeroLocation.navigationQuery)
    }
}