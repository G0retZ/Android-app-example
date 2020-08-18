package com.example.app.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.app.Navigator
import com.example.app.R
import com.example.app.inject
import com.example.app.presentation.ViewState
import com.example.app.presentation.selectedshop.SelectedShopViewActions
import com.example.app.presentation.selectedshop.SelectedShopViewModel
import kotlinx.android.synthetic.main.fragment_selected_shop.*

class SelectedShopFragment : Fragment(), SelectedShopViewActions {

    lateinit var selectionViewModel: SelectedShopViewModel

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
        enterTransition = TransitionInflater
            .from(requireContext())
            .inflateTransition(android.R.transition.slide_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_selected_shop, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) =
        super.onActivityCreated(savedInstanceState)
            .also {
                accept.setOnClickListener {
                    selectionViewModel.accept()
                }
                close.setOnClickListener {
                    selectionViewModel.close()
                }
                hideAnimator = HideAnimator(
                    resources.displayMetrics.density * 128,
                    listOf(accept, gradient)
                )
                selectionViewModel
                    .viewStateLiveData
                    .observe(viewLifecycleOwner,
                        Observer<ViewState<SelectedShopViewActions>> { it?.apply(this) })

                selectionViewModel
                    .navigationLiveData
                    .observe(viewLifecycleOwner, Observer<String>(navigator::navigate))
            }

    override fun onDestroyView() = super.onDestroyView()
        .also {
            hideAnimator = null
        }

    override fun setName(name: String) {
        shopName.text = name
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
                .into(picture)
        }
    }

    override fun setStreet(address: String) {
        shopAddress.text = address
    }

    override fun setCity(city: String) {
        shopCity.text = city
    }
}
