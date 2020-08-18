package com.example.app.presentation.selectedshop

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.app.Shop
import com.example.app.ViewModelThreadTestRule
import com.example.app.interactor.DataReceiver
import com.example.app.presentation.ViewState
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class SelectedShopViewModelTest {

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SelectedShopViewModel

    @Mock
    private lateinit var useCase: DataReceiver<Shop>

    @Mock
    private lateinit var viewStateObserver: Observer<ViewState<SelectedShopViewActions>>

    @Mock
    private lateinit var navigateObserver: Observer<String>

    private lateinit var selectionSubject: PublishSubject<Shop>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        selectionSubject = PublishSubject.create()
        Mockito.`when`(useCase.get()).thenReturn(selectionSubject)
        viewModel = SelectedShopViewModelImpl(useCase)
    }

    /* Check interactions with use case. */
    @Test
    fun shouldAskUseCaseForDataInitially() {
        // Effect:
        verify(useCase, Mockito.only()).get()
    }

    @Test
    fun shouldAskUseCaseForDataOnReSubscriptions() {
        // Given:
        Mockito.`when`(useCase.get())
            .thenReturn(Observable.empty(), Observable.empty(), Observable.never())

        // Action:
        selectionSubject.onComplete()

        // Effect:
        verify(useCase, Mockito.times(4)).get()
    }

    @Test
    fun shouldNotTouchUseCaseOnSubscriptions() {
        // Action:
        selectionSubject.onNext(Shop(id = "2"))
        selectionSubject.onNext(Shop(id = "3"))
        viewModel.navigationLiveData
        viewModel.viewStateLiveData
        viewModel.navigationLiveData
        viewModel.viewStateLiveData

        // Effect:
        verify(useCase, Mockito.only()).get()
    }

    /* Check view state switching. */
    @Test
    fun shouldSetNotSelectedViewStateToLiveDataInitially() {
        // Given:
        val inOrder = Mockito.inOrder(viewStateObserver)

        // Action:
        viewModel.viewStateLiveData.observeForever(viewStateObserver)

        // Effect:
        inOrder.verify(viewStateObserver)
            .onChanged(
                ArgumentMatchers.any(SelectedShopStateNotSelected::class.java)
            )
        Mockito.verifyNoMoreInteractions(viewStateObserver)
    }

    @Test
    fun shouldSetSelectedViewStateToLiveData() {
        // Given:
        val inOrder = Mockito.inOrder(viewStateObserver)
        viewModel.viewStateLiveData.observeForever(viewStateObserver)

        // Action:
        selectionSubject.onNext(Shop(id = "2"))
        selectionSubject.onNext(Shop(id = "5"))

        // Effect:
        inOrder.verify(viewStateObserver)
            .onChanged(
                ArgumentMatchers.any(SelectedShopStateNotSelected::class.java)
            )
        inOrder.verify(viewStateObserver)
            .onChanged(
                SelectedShopStateSelected(Shop(id = "2"))
            )
        inOrder.verify(viewStateObserver)
            .onChanged(
                SelectedShopStateSelected(Shop(id = "5"))
            )
        Mockito.verifyNoMoreInteractions(viewStateObserver)
    }

    @Test
    fun shouldSetNotSelectedViewStateToLiveData() {
        // Given:
        Mockito.`when`(useCase.get()).thenReturn(Observable.never())
        val inOrder = Mockito.inOrder(viewStateObserver)
        viewModel.viewStateLiveData.observeForever(viewStateObserver)


        // Action:
        selectionSubject.onNext(Shop(id = "2"))
        selectionSubject.onComplete()

        // Effect:
        inOrder.verify(viewStateObserver)
            .onChanged(
                ArgumentMatchers.any(SelectedShopStateNotSelected::class.java)
            )
        inOrder.verify(viewStateObserver)
            .onChanged(
                SelectedShopStateSelected(Shop(id = "2"))
            )
        inOrder.verify(viewStateObserver)
            .onChanged(
                ArgumentMatchers.any(SelectedShopStateNotSelected::class.java)
            )
        Mockito.verifyNoMoreInteractions(viewStateObserver)
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
        ).onChanged(CLOSE_SELECTION_DETAILS)
    }

    @Test
    fun setNavigateToCloseSelectionDetailsOnComplete() {
        // Given:
        Mockito.`when`(useCase.get()).thenReturn(Observable.never())
        viewModel.navigationLiveData.observeForever(navigateObserver)

        // Action:
        selectionSubject.onComplete()

        // Effect:
        Mockito.verify<Observer<String>>(
            navigateObserver,
            Mockito.only()
        ).onChanged(CLOSE_SELECTION_DETAILS)
    }

    companion object {
        @ClassRule
        @JvmField
        val classRule: ViewModelThreadTestRule = ViewModelThreadTestRule()
    }
}