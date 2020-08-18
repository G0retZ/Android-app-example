package com.example.app.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.example.app.Navigator
import com.example.app.R
import com.example.app.inject
import com.example.app.presentation.ViewState
import com.example.app.presentation.chooseshop.ChooseShopListItem
import com.example.app.presentation.chooseshop.ChooseShopViewActions
import com.example.app.presentation.chooseshop.ChooseShopViewModel
import com.example.app.presentation.shoplistselection.ShopListSelectionViewActions
import com.example.app.presentation.shoplistselection.ShopListSelectionViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_choose_shop.*
import timber.log.Timber

class ChooseShopFragment : Fragment(), ChooseShopViewActions, ShopListSelectionViewActions {

    lateinit var chooseShopViewModel: ChooseShopViewModel

    lateinit var shopSelectionViewModel: ShopListSelectionViewModel

    lateinit var navigator: Navigator

    private var hideAnimator: HideAnimator? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    hideAnimator?.switchVisibility(false) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject(this)
        exitTransition = TransitionInflater
            .from(requireContext())
            .inflateTransition(android.R.transition.fade)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_choose_shop, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) =
        super.onActivityCreated(savedInstanceState)
            .also {
                retryButton.setOnClickListener {
                    chooseShopViewModel.reloadShops()
                }
                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = ChooseShopAdapter(listOf()) {}
                nearby.setOnClickListener {
                    Timber.d("Nearby clicked!")
                    Snackbar.make(
                        it,
                        "The feature is in progress. Stay tuned",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                accept.setOnClickListener {
                    hideAnimator?.switchVisibility(false) {
                        shopSelectionViewModel.accept()
                    }
                }
                close.setOnClickListener {
                    chooseShopViewModel.close()
                }
                hideAnimator = HideAnimator(
                    resources.displayMetrics.density * 128,
                    listOf(accept, gradient)
                )
                chooseShopViewModel
                    .viewStateLiveData
                    .observe(viewLifecycleOwner,
                        Observer<ViewState<ChooseShopViewActions>> { it?.apply(this) })

                shopSelectionViewModel
                    .viewStateLiveData
                    .observe(viewLifecycleOwner,
                        Observer<ViewState<ShopListSelectionViewActions>> { it?.apply(this) })

                chooseShopViewModel
                    .navigationLiveData
                    .observe(viewLifecycleOwner, Observer<String>(navigator::navigate))

                shopSelectionViewModel
                    .navigationLiveData
                    .observe(viewLifecycleOwner, Observer<String>(navigator::navigate))
            }

    override fun onDestroyView() = super.onDestroyView()
        .also {
            hideAnimator = null
        }

    override fun showShopListPending(show: Boolean) {
        pending.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showShopList(show: Boolean) {
        recyclerView.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun setShopListItems(chooseShopListItems: List<ChooseShopListItem>) {
        recyclerView.adapter =
            ChooseShopAdapter(chooseShopListItems, chooseShopViewModel::selectItem)
    }

    override fun showShopListErrorMessage(show: Boolean) {
        errorText.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun setShopListErrorMessage(message: String) {
        errorText.text = message
    }

    override fun showShopListRetryButton(show: Boolean) {
        retryButton.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showSelectedIndex(index: Int?) {
        (recyclerView.adapter as? ChooseShopAdapter)?.selection = index
    }

    override fun showAcceptButton(show: Boolean) {
        hideAnimator?.switchVisibility(show)
    }
}
