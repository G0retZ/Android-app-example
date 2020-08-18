package com.example.app.presentation.chooseshop

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.app.Shop
import com.example.app.ViewModelThreadTestRule
import com.example.app.interactor.BrandShopsUseCase
import com.example.app.presentation.ViewState
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.MockitoAnnotations
import java.io.IOException

class ChooseShopViewModelTest {

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ChooseShopViewModel

    @Mock
    private lateinit var brandShopsUseCase: BrandShopsUseCase

    @Mock
    private lateinit var viewStateObserver: Observer<ViewState<ChooseShopViewActions>>

    @Mock
    private lateinit var navigateObserver: Observer<String>

    private lateinit var shopSingleSubject: SingleSubject<List<Shop>>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        shopSingleSubject = SingleSubject.create<List<Shop>>()
        Mockito.`when`(brandShopsUseCase.brandShops)
            .thenReturn(shopSingleSubject)
        Mockito.`when`(brandShopsUseCase.selectShopAt(ArgumentMatchers.any()))
            .thenReturn(Completable.never())
        viewModel = ChooseShopViewModelImpl(brandShopsUseCase)
    }

    /* Check interactions работу with use case. */
    @Test
    fun shouldAskUseCaseForDataInitially() {
        // Effect:
        verify(brandShopsUseCase, Mockito.only()).brandShops
    }

    @Test
    fun shouldNotTouchUseCaseOnSubscriptions() {
        // Action:
        shopSingleSubject.onSuccess(
            listOf(Shop(id = "1"), Shop(id = "2"), Shop(id = "3"), Shop(id = "4"))
        )
        viewModel.navigationLiveData
        viewModel.viewStateLiveData
        viewModel.navigationLiveData
        viewModel.viewStateLiveData

        // Effect:
        verify(brandShopsUseCase, Mockito.only()).brandShops
    }

    @Test
    fun shouldAskUseCaseToSelectByIndex() {
        // Given:
        Mockito.`when`(brandShopsUseCase.selectShopAt(ArgumentMatchers.any()))
            .thenReturn(Completable.complete())

        // Action:
        viewModel.selectItem(-1)
        viewModel.selectItem(2)
        viewModel.selectItem(3)

        // Effect:
        verify(brandShopsUseCase).brandShops
        verify(brandShopsUseCase).selectShopAt(-1)
        verify(brandShopsUseCase).selectShopAt(2)
        verify(brandShopsUseCase).selectShopAt(3)
        verifyNoMoreInteractions(brandShopsUseCase)
    }

    @Test
    fun shouldNotTouchUseCaseDuringSelection() {
        // Action:
        viewModel.selectItem(-1)
        viewModel.selectItem(2)
        viewModel.selectItem(3)

        // Effect:
        verify(brandShopsUseCase).brandShops
        verify(brandShopsUseCase).selectShopAt(-1)
        verifyNoMoreInteractions(brandShopsUseCase)
    }

    /* Check view state switching. */
    @Test
    fun shouldSetPendingViewStateToLiveDataInitially() {
        // Given:
        val inOrder = Mockito.inOrder(viewStateObserver)

        // Action:
        viewModel.viewStateLiveData.observeForever(viewStateObserver)

        // Effect:
        inOrder.verify(viewStateObserver)
            .onChanged(
                ArgumentMatchers.any(ChooseShopViewStatePending::class.java)
            )
        verifyNoMoreInteractions(viewStateObserver)
    }

    @Test
    fun shouldSetErrorViewStateToLiveData() {
        // Given:
        val inOrder = Mockito.inOrder(viewStateObserver)
        viewModel.viewStateLiveData.observeForever(viewStateObserver)

        // Action:
        shopSingleSubject.onError(IOException("Error"))

        // Effect:
        inOrder.verify(viewStateObserver)
            .onChanged(
                ArgumentMatchers.any(ChooseShopViewStatePending::class.java)
            )
        inOrder.verify(viewStateObserver)
            .onChanged(
                ChooseShopViewStateError("Error")
            )
        verifyNoMoreInteractions(viewStateObserver)
    }

    @Test
    fun shouldSetReadyViewStateWitchCorrectListToLiveData() {
        // Given:
        val inOrder = Mockito.inOrder(viewStateObserver)
        viewModel.viewStateLiveData.observeForever(viewStateObserver)

        // Action:
        shopSingleSubject.onSuccess(
            listOf(Shop(id = "1"), Shop(id = "2"), Shop(id = "3"), Shop(id = "4"))
        )

        // Effect:
        inOrder.verify(viewStateObserver)
            .onChanged(
                ArgumentMatchers.any(ChooseShopViewStatePending::class.java)
            )
        inOrder.verify(viewStateObserver)
            .onChanged(
                ChooseShopViewStateReady(
                    listOf(
                        ChooseShopListItem(Shop(id = "1")),
                        ChooseShopListItem(Shop(id = "2")),
                        ChooseShopListItem(Shop(id = "3")),
                        ChooseShopListItem(Shop(id = "4"))
                    )
                )
            )
        verifyNoMoreInteractions(viewStateObserver)
    }

    @Test
    fun shouldSetPendingViewStateToLiveDataOnRetry() {
        // Given:
        val inOrder = Mockito.inOrder(viewStateObserver)
        viewModel.viewStateLiveData.observeForever(viewStateObserver)

        // Action:
        shopSingleSubject.onError(IOException("Error"))
        Mockito.`when`(brandShopsUseCase.brandShops).thenReturn(Single.never())
        viewModel.reloadShops()

        // Effect:
        inOrder.verify(viewStateObserver)
            .onChanged(
                ArgumentMatchers.any(ChooseShopViewStatePending::class.java)
            )
        inOrder.verify(viewStateObserver)
            .onChanged(
                ChooseShopViewStateError("Error")
            )
        inOrder.verify(viewStateObserver)
            .onChanged(
                ArgumentMatchers.any(ChooseShopViewStatePending::class.java)
            )
        verifyNoMoreInteractions(viewStateObserver)
    }

    @Test
    fun shouldSetErrorViewStateToLiveDataOnSelectionFailure() {
        // Given:
        val inOrder = Mockito.inOrder(viewStateObserver)
        Mockito.`when`(brandShopsUseCase.selectShopAt(ArgumentMatchers.any()))
            .thenReturn(Completable.error(IndexOutOfBoundsException("Error 2")))
        viewModel.viewStateLiveData.observeForever(viewStateObserver)

        // Action:
        shopSingleSubject.onSuccess(listOf())
        viewModel.selectItem(-1)

        // Effect:
        inOrder.verify(viewStateObserver)
            .onChanged(
                ArgumentMatchers.any(ChooseShopViewStatePending::class.java)
            )
        inOrder.verify(viewStateObserver)
            .onChanged(
                ChooseShopViewStateReady(listOf())
            )
        inOrder.verify(viewStateObserver)
            .onChanged(
                ChooseShopViewStateError("Error 2")
            )
        verifyNoMoreInteractions(viewStateObserver)
    }

    @Test
    fun shouldNoSetOtherViewStateToLiveDataOnSelectionSuccess() {
        // Given:
        val inOrder = Mockito.inOrder(viewStateObserver)
        Mockito.`when`(brandShopsUseCase.selectShopAt(ArgumentMatchers.any()))
            .thenReturn(Completable.complete())
        viewModel.viewStateLiveData.observeForever(viewStateObserver)

        // Action:
        shopSingleSubject.onSuccess(listOf())
        viewModel.selectItem(-1)

        // Effect:
        inOrder.verify(viewStateObserver)
            .onChanged(
                ArgumentMatchers.any(ChooseShopViewStatePending::class.java)
            )
        inOrder.verify(viewStateObserver)
            .onChanged(
                ChooseShopViewStateReady(listOf())
            )
        verifyNoMoreInteractions(viewStateObserver)
    }

    /* Check navigation */
    @Test
    fun setNavigateToCloseSelectionDetailsOnClose() {
        // Given:
        viewModel.navigationLiveData.observeForever(navigateObserver)

        // Action:
        viewModel.close()

        // Effect:
        Mockito.verify<Observer<String>>(
            navigateObserver,
            Mockito.only()
        ).onChanged(CLOSE_CHOOSE_SHOP)
    }

    companion object {
        @ClassRule
        @JvmField
        val classRule: ViewModelThreadTestRule = ViewModelThreadTestRule()
    }
}