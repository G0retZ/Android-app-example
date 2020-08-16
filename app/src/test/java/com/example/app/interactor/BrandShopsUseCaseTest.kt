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
    private lateinit var choiceReceiver: Observer<Int>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        useCase = BrandShopsUseCaseImpl(api, choiceReceiver)
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

    /* Check interaction with data sharer */
    /**
     * Should clear touch choice data receiver on result and error.
     */
    @Test
    fun shouldClearChoiceReceiverOnError() {
        // Given:
        Mockito.`when`(api.loadHome()).thenReturn(Single.error(IOException()))

        // Action:
        useCase.brandShops.test().isDisposed

        // Effect:
        verify(choiceReceiver, Mockito.only()).onNext(-1)
    }

    /**
     * Should clear touch choice data receiver on result.
     */
    @Test
    fun shouldClearChoiceReceiverOnResult() {
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
        verify(choiceReceiver, Mockito.only()).onNext(-1)
    }

    /**
     * Should provide the choice.
     */
    @Test
    fun shouldProvideSelectionToChoiceReceiver() {
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
        val inOrder = Mockito.inOrder(choiceReceiver)
        inOrder.verify(choiceReceiver).onNext(-1)
        inOrder.verify(choiceReceiver).onNext(3)
        Mockito.verifyNoMoreInteractions(choiceReceiver)
    }

    /**
     * Should remove the choice.
     */
    @Test
    fun shouldRemoveSelectionFromChoiceReceiver() {
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
        verify(choiceReceiver, Mockito.times(2)).onNext(-1)
    }

    /**
     * Should not touch choice receiver if selection is wrong.
     */
    @Test
    fun doNotTouchChoiceReceiverIfSelectionInvalid() {
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
        verify(choiceReceiver, Mockito.only()).onNext(-1)
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