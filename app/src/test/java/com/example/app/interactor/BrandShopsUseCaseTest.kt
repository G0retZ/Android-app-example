package com.example.app.interactor

import com.example.app.*
import io.reactivex.Observer
import io.reactivex.Single
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.io.IOException

class BrandShopsUseCaseTest {

    private lateinit var useCase: BrandShopsUseCase

    @Mock
    private lateinit var api: LocationsApi

    @Mock
    private lateinit var choiceObserver: Observer<Int>

    @Mock
    private lateinit var selectedShopObserver: Observer<Shop>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        useCase = BrandShopsUseCaseImpl(api, choiceObserver, selectedShopObserver)
        Mockito.`when`(api.loadHome()).thenReturn(Single.never())
    }

    /* Check interaction with API */
    /**
     * Should ask API for data only once.
     */
    @Test
    fun askApiForBrandDataOnlyOnce() {
        // Action:
        useCase.brandShops.test().isDisposed
        useCase.brandShops.test().isDisposed
        useCase.brandShops.test().isDisposed

        // Effect:
        verify(api, Mockito.only()).loadHome()
    }

    /* Check answers on API data request */
    /**
     * Should answer with error.
     */
    @Test
    fun answerIOException() {
        // Given:
        Mockito.`when`(api.loadHome()).thenReturn(Single.error(IOException()))

        // Action:
        val test = useCase.brandShops.test()

        // Effect:
        test.assertError(IOException::class.java)
    }

    /**
     * Should answer without distortion.
     */
    @Test
    fun answerWithVehiclesList() {
        // Given:
        Mockito.`when`(api.loadHome()).thenReturn(
            Single.just(
                LocationsResponse(
                    brand = Brand(
                        listOf(
                            Shop(id = "0"),
                            Shop(id = "1"),
                            Shop(id = "2"),
                            Shop(id = "3"),
                            Shop(id = "4")
                        )
                    )
                )
            )
        )

        // Action:
        val test = useCase.brandShops.test()

        // Effect:
        test.assertValue(
            listOf(
                Shop(id = "0"),
                Shop(id = "1"),
                Shop(id = "2"),
                Shop(id = "3"),
                Shop(id = "4")
            )
        )
    }

    /* Check interaction with choice data observer */
    /**
     * Should clear touch choice data observer on result and error.
     */
    @Test
    fun shouldClearChoiceObserverOnError() {
        // Given:
        Mockito.`when`(api.loadHome()).thenReturn(Single.error(IOException()))

        // Action:
        useCase.brandShops.test().isDisposed

        // Effect:
        verify(choiceObserver, Mockito.only()).onNext(-1)
    }

    /**
     * Should clear touch choice data observer on result.
     */
    @Test
    fun shouldClearChoiceObserverOnResult() {
        // Given:
        Mockito.`when`(api.loadHome()).thenReturn(
            Single.just(
                LocationsResponse(
                    brand = Brand(
                        listOf(
                            Shop(id = "0"),
                            Shop(id = "1"),
                            Shop(id = "2"),
                            Shop(id = "3"),
                            Shop(id = "4")
                        )
                    )
                )
            )
        )

        // Action:
        useCase.brandShops.test().isDisposed

        // Effect:
        verify(choiceObserver, Mockito.only()).onNext(-1)
    }

    /**
     * Should provide the choice.
     */
    @Test
    fun shouldProvideSelectionToChoiceObserver() {
        // Given:
        Mockito.`when`(api.loadHome()).thenReturn(
            Single.just(
                LocationsResponse(
                    brand = Brand(
                        listOf(
                            Shop(id = "0"),
                            Shop(id = "1"),
                            Shop(id = "2"),
                            Shop(id = "3"),
                            Shop(id = "4")
                        )
                    )
                )
            )
        )

        // Action:
        useCase.brandShops.test().isDisposed
        useCase.selectShopAt(3).test().isDisposed

        // Effect:
        val inOrder = Mockito.inOrder(choiceObserver)
        inOrder.verify(choiceObserver).onNext(-1)
        inOrder.verify(choiceObserver).onNext(3)
        Mockito.verifyNoMoreInteractions(choiceObserver)
    }

    /**
     * Should remove the choice.
     */
    @Test
    fun shouldRemoveSelectionFromChoiceObserver() {
        // Given:
        Mockito.`when`(api.loadHome()).thenReturn(
            Single.just(
                LocationsResponse(
                    brand = Brand(
                        listOf(
                            Shop(id = "0"),
                            Shop(id = "1"),
                            Shop(id = "2"),
                            Shop(id = "3"),
                            Shop(id = "4")
                        )
                    )
                )
            )
        )

        // Action:
        useCase.brandShops.test().isDisposed
        useCase.selectShopAt(null).test().isDisposed

        // Effect:
        verify(choiceObserver, Mockito.times(2)).onNext(-1)
    }

    /**
     * Should not touch choice observer if selection is wrong.
     */
    @Test
    fun doNotTouchChoiceObserverIfSelectionInvalid() {
        // Given:
        Mockito.`when`(api.loadHome()).thenReturn(
            Single.just(
                LocationsResponse(
                    brand = Brand(
                        listOf(
                            Shop(id = "0"),
                            Shop(id = "1"),
                            Shop(id = "2"),
                            Shop(id = "3"),
                            Shop(id = "4")
                        )
                    )
                )
            )
        )

        // Action:
        useCase.brandShops.test().isDisposed
        useCase.selectShopAt(-1).test().isDisposed
        useCase.selectShopAt(7).test().isDisposed

        // Effect:
        verify(choiceObserver, Mockito.only()).onNext(-1)
    }

    /* Check interaction with selected shop data observer */
    /**
     * Should clear touch choice data observer on result and error.
     */
    @Test
    fun shouldClearSelectedShopObserverOnError() {
        // Given:
        Mockito.`when`(api.loadHome()).thenReturn(Single.error(IOException()))

        // Action:
        useCase.brandShops.test().isDisposed

        // Effect:
        verify(selectedShopObserver, Mockito.only()).onComplete()
    }

    /**
     * Should clear touch choice data observer on result.
     */
    @Test
    fun shouldClearSelectedShopObserverOnResult() {
        // Given:
        Mockito.`when`(api.loadHome()).thenReturn(
            Single.just(
                LocationsResponse(
                    brand = Brand(
                        listOf(
                            Shop(id = "0"),
                            Shop(id = "1"),
                            Shop(id = "2"),
                            Shop(id = "3"),
                            Shop(id = "4")
                        )
                    )
                )
            )
        )

        // Action:
        useCase.brandShops.test().isDisposed

        // Effect:
        verify(selectedShopObserver, Mockito.only()).onComplete()
    }

    /**
     * Should provide the choice.
     */
    @Test
    fun shouldProvideSelectionToSelectedShopObserver() {
        // Given:
        Mockito.`when`(api.loadHome()).thenReturn(
            Single.just(
                LocationsResponse(
                    brand = Brand(
                        listOf(
                            Shop(id = "0"),
                            Shop(id = "1"),
                            Shop(id = "2"),
                            Shop(id = "3"),
                            Shop(id = "4")
                        )
                    )
                )
            )
        )

        // Action:
        useCase.brandShops.test().isDisposed
        useCase.selectShopAt(3).test().isDisposed

        // Effect:
        val inOrder = Mockito.inOrder(selectedShopObserver)
        inOrder.verify(selectedShopObserver).onComplete()
        inOrder.verify(selectedShopObserver).onNext(Shop(id = "3"))
        Mockito.verifyNoMoreInteractions(selectedShopObserver)
    }

    /**
     * Should remove the choice.
     */
    @Test
    fun shouldRemoveSelectionFromSelectedShopObserver() {
        // Given:
        Mockito.`when`(api.loadHome()).thenReturn(
            Single.just(
                LocationsResponse(
                    brand = Brand(
                        listOf(
                            Shop(id = "0"),
                            Shop(id = "1"),
                            Shop(id = "2"),
                            Shop(id = "3"),
                            Shop(id = "4")
                        )
                    )
                )
            )
        )

        // Action:
        useCase.brandShops.test().isDisposed
        useCase.selectShopAt(null).test().isDisposed

        // Effect:
        verify(selectedShopObserver, Mockito.times(2)).onComplete()
    }

    /**
     * Should not touch choice observer if selection is wrong.
     */
    @Test
    fun doNotTouchSelectedShopObserverIfSelectionInvalid() {
        // Given:
        Mockito.`when`(api.loadHome()).thenReturn(
            Single.just(
                LocationsResponse(
                    brand = Brand(
                        listOf(
                            Shop(id = "0"),
                            Shop(id = "1"),
                            Shop(id = "2"),
                            Shop(id = "3"),
                            Shop(id = "4")
                        )
                    )
                )
            )
        )

        // Action:
        useCase.brandShops.test().isDisposed
        useCase.selectShopAt(-1).test().isDisposed
        useCase.selectShopAt(7).test().isDisposed

        // Effect:
        verify(selectedShopObserver, Mockito.only()).onComplete()
    }

    /* Check answers on selection */
    /**
     * Should answer with out of range error.
     */
    @Test
    fun shouldAnswerWithOutOfBoundsError() {
        // Given:
        Mockito.`when`(api.loadHome()).thenReturn(
            Single.just(
                LocationsResponse(
                    brand = Brand(
                        listOf(
                            Shop(id = "0"),
                            Shop(id = "1"),
                            Shop(id = "2"),
                            Shop(id = "3"),
                            Shop(id = "4")
                        )
                    )
                )
            )
        )

        // Action:
        useCase.brandShops.test().isDisposed
        val test = useCase.selectShopAt(-1).test()

        // Effect:
        test.assertError(IndexOutOfBoundsException::class.java)
    }

    /**
     * Should answer with success.
     */
    @Test
    fun shouldAnswerWithSuccess() {
        // Given:
        Mockito.`when`(api.loadHome()).thenReturn(
            Single.just(
                LocationsResponse(
                    brand = Brand(
                        listOf(
                            Shop(id = "0"),
                            Shop(id = "1"),
                            Shop(id = "2"),
                            Shop(id = "3"),
                            Shop(id = "4")
                        )
                    )
                )
            )
        )

        // Action:
        useCase.brandShops.test().isDisposed
        val test = useCase.selectShopAt(1).test()
        val test1 = useCase.selectShopAt(null).test()

        // Effect:
        test.assertComplete()
        test1.assertComplete()
    }

    companion object {
        @ClassRule
        @JvmField
        val classRule: UseCaseThreadTestRule = UseCaseThreadTestRule()
    }
}