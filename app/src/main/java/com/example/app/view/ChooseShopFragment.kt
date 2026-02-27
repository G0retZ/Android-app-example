package com.example.app.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionListenerAdapter
import com.example.app.Navigator
import com.example.app.R
import com.example.app.databinding.FragmentChooseShopBinding
import com.example.app.inject
import com.example.app.presentation.chooseshop.ChooseShopListItem
import com.example.app.presentation.chooseshop.ChooseShopViewActions
import com.example.app.presentation.chooseshop.ChooseShopViewModel
import com.example.app.presentation.shoplistselection.ShopListSelectionViewActions
import com.example.app.presentation.shoplistselection.ShopListSelectionViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ChooseShopFragment : Fragment(), ChooseShopViewActions, ShopListSelectionViewActions {

    lateinit var chooseShopViewModel: ChooseShopViewModel

    lateinit var shopSelectionViewModel: ShopListSelectionViewModel

    lateinit var navigator: Navigator

    private var hideAnimator: HideAnimator? = null

    private var _binding: FragmentChooseShopBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    hideAnimator?.switchVisibility(false) {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
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
        reenterTransition = TransitionInflater
            .from(requireContext())
            .inflateTransition(android.R.transition.fade)
            ?.addListener(object : TransitionListenerAdapter() {
                override fun onTransitionStart(transition: Transition) {
                    hideAnimator?.setVisible(false)
                }

                override fun onTransitionCancel(transition: Transition) {
                    (binding.recyclerView.adapter as? ChooseShopAdapter)?.selection?.let {
                        hideAnimator?.setVisible(true)
                    }
                }

                override fun onTransitionEnd(transition: Transition) {
                    (binding.recyclerView.adapter as? ChooseShopAdapter)?.selection?.let {
                        hideAnimator?.switchVisibility(true)
                    }
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChooseShopBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.fragment_choose_shop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) =
        super.onViewCreated(view, savedInstanceState)
            .also {
                binding.retryButton.setOnClickListener {
                    chooseShopViewModel.reloadShops()
                }
                binding.recyclerView.layoutManager = LinearLayoutManager(context)
                binding.recyclerView.adapter = ChooseShopAdapter(listOf()) {}
                binding.nearby.setOnClickListener {
                    Snackbar.make(
                        it,
                        "The feature is in progress. Stay tuned",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                binding.accept.setOnClickListener {
                    hideAnimator?.switchVisibility(false) {
                        shopSelectionViewModel.accept()
                    }
                }
                binding.close.setOnClickListener {
                    chooseShopViewModel.close()
                }
                hideAnimator = HideAnimator(
                    resources.displayMetrics.density * 128,
                    listOf(binding.accept, binding.gradient)
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        launch {
                            chooseShopViewModel
                                .viewStates
                                .collect { it.apply(this@ChooseShopFragment) }
                        }
                        launch {
                            shopSelectionViewModel
                                .viewStates
                                .collect { it.apply(this@ChooseShopFragment) }
                        }
                        launch {
                            chooseShopViewModel
                                .navigation
                                .collect(navigator::navigate)
                        }
                        launch {
                            shopSelectionViewModel
                                .navigation
                                .collect(navigator::navigate)
                        }
                    }
                }
            }

    override fun onDestroyView() = super.onDestroyView()
        .also {
            hideAnimator = null
        }

    override fun showShopListPending(show: Boolean) {
        binding.pending.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showShopList(show: Boolean) {
        binding.recyclerView.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun setShopListItems(chooseShopListItems: List<ChooseShopListItem>) {
        binding.recyclerView.adapter =
            ChooseShopAdapter(chooseShopListItems, chooseShopViewModel::selectItem)
    }

    override fun showShopListErrorMessage(show: Boolean) {
        binding.errorText.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun setShopListErrorMessage(message: String) {
        binding.errorText.text = message
    }

    override fun showShopListRetryButton(show: Boolean) {
        binding.retryButton.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showSelectedIndex(index: Int?) {
        (binding.recyclerView.adapter as? ChooseShopAdapter)?.selection = index
    }

    override fun showAcceptButton(show: Boolean) {
        hideAnimator?.switchVisibility(show)
    }
}
