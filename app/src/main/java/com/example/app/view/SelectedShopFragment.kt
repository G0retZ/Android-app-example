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
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionListenerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.app.Navigator
import com.example.app.R
import com.example.app.databinding.FragmentSelectedShopBinding
import com.example.app.inject
import com.example.app.presentation.selectedshop.SelectedShopViewActions
import com.example.app.presentation.selectedshop.SelectedShopViewModel
import kotlinx.coroutines.launch

class SelectedShopFragment : Fragment(), SelectedShopViewActions {

    lateinit var selectionViewModel: SelectedShopViewModel

    lateinit var navigator: Navigator

    private var hideAnimator: HideAnimator? = null

    private var _binding: FragmentSelectedShopBinding? = null

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
        enterTransition = TransitionInflater
            .from(requireContext())
            .inflateTransition(android.R.transition.slide_right)
            ?.addListener(object : TransitionListenerAdapter() {
                override fun onTransitionEnd(transition: Transition) {
                    hideAnimator?.switchVisibility(true)
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectedShopBinding.inflate(inflater, container, false)
        return inflater.inflate(R.layout.fragment_selected_shop, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) =
        super.onViewCreated(view, savedInstanceState)
            .also {
                binding.accept.setOnClickListener {
                    selectionViewModel.accept()
                }
                binding.close.setOnClickListener {
                    selectionViewModel.close()
                }
                hideAnimator = HideAnimator(
                    resources.displayMetrics.density * 128,
                    listOf(binding.accept, binding.gradient)
                )
                viewLifecycleOwner.lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        launch {
                            selectionViewModel
                                .viewStates
                                .collect { it.apply(this@SelectedShopFragment) }
                        }
                        selectionViewModel
                            .navigation
                            .collect(navigator::navigate)
                    }
                }
            }

    override fun onDestroyView() = super.onDestroyView()
        .also {
            hideAnimator = null
        }

    override fun setName(name: String) {
        binding.shopName.text = name
    }

    override fun setPicture(url: String?) {
        context?.let {
            Glide.with(it)
                .load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .transform(
                    CenterCrop(),
                    RoundedCorners((resources.displayMetrics.density * 16).toInt())
                )
                .into(binding.picture)
        }
    }

    override fun setStreet(address: String) {
        binding.shopAddress.text = address
    }

    override fun setCity(city: String) {
        binding.shopCity.text = city
    }
}
