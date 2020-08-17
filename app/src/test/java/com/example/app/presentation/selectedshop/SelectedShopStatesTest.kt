package com.example.app.presentation.selectedshop

import com.example.app.Address
import com.example.app.Photo
import com.example.app.Shop
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.MockitoAnnotations

class SelectedShopStatesTest {
    @Mock
    private lateinit var viewActions: SelectedShopViewActions

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun testSelectedWithoutDataActions() {
        // Действие:
        SelectedShopStateSelected(Shop(id = "2")).apply(viewActions)

        // Результат:
        verify(viewActions).setName("No name")
        verify(viewActions).setPicture(null)
        verify(viewActions).setStreet("")
        verify(viewActions).setCity("")
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testSelectedWithNullDataActions() {
        // Действие:
        SelectedShopStateSelected(
            Shop(
                name = null,
                photos = listOf(),
                address = null
            )
        ).apply(viewActions)

        // Результат:
        verify(viewActions).setName("No name")
        verify(viewActions).setPicture(null)
        verify(viewActions).setStreet("")
        verify(viewActions).setCity("")
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testSelectedWithNullDataInAddressActions() {
        // Действие:
        SelectedShopStateSelected(
            Shop(
                name = null,
                photos = listOf(),
                address = Address(street2 = null)
            )
        ).apply(viewActions)

        // Результат:
        verify(viewActions).setName("No name")
        verify(viewActions).setPicture(null)
        verify(viewActions).setStreet("")
        verify(viewActions).setCity("")
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testSelectedWithEmptyDataActions() {
        // Действие:
        SelectedShopStateSelected(
            Shop(
                name = "",
                photos = listOf(Photo(url = "")),
                address = Address(street = "", street2 = "", city = "")
            )
        ).apply(viewActions)

        // Результат:
        verify(viewActions).setName("No name")
        verify(viewActions).setPicture(null)
        verify(viewActions).setStreet("")
        verify(viewActions).setCity("")
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testSelectedWithBlankDataActions() {
        // Действие:
        SelectedShopStateSelected(
            Shop(
                name = " ",
                photos = listOf(Photo(url = " ")),
                address = Address(street = " ", street2 = " ", city = " ")
            )
        ).apply(viewActions)

        // Результат:
        verify(viewActions).setName("No name")
        verify(viewActions).setPicture(null)
        verify(viewActions).setStreet("")
        verify(viewActions).setCity("")
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testSelectedWithDataActions() {
        // Действие:
        SelectedShopStateSelected(
            Shop(
                name = "Super shop",
                photos = listOf(Photo(url = "url")),
                address = Address(street = "street", street2 = null, city = "city")
            )
        ).apply(viewActions)

        // Результат:
        verify(viewActions).setName("Super shop")
        verify(viewActions).setPicture("url")
        verify(viewActions).setStreet("street")
        verify(viewActions).setCity("city")
        verifyNoMoreInteractions(viewActions)
    }

    @Test
    fun testSelectedWithStreet2DataActions() {
        // Действие:
        SelectedShopStateSelected(
            Shop(
                name = "Super shop",
                photos = listOf(Photo(url = "url")),
                address = Address(street = " ", street2 = "street 2", city = "city")
            )
        ).apply(viewActions)

        // Результат:
        verify(viewActions).setName("Super shop")
        verify(viewActions).setPicture("url")
        verify(viewActions).setStreet("street 2")
        verify(viewActions).setCity("city")
        verifyNoMoreInteractions(viewActions)
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
        // Действие:
        SelectedShopStateNotSelected().apply(viewActions)

        // Результат:
        verify(viewActions).setName("Not selected")
        verify(viewActions).setPicture(null)
        verify(viewActions).setCity("")
        verify(viewActions).setStreet("")
        verifyNoMoreInteractions(viewActions)
    }
}