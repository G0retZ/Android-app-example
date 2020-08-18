package com.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.app.presentation.chooseshop.CLOSE_CHOOSE_SHOP
import com.example.app.presentation.selectedshop.CLOSE_SELECTION_DETAILS
import com.example.app.presentation.shoplistselection.TO_SELECTION_DETAILS
import com.example.app.view.ChooseShopFragment
import com.example.app.view.SelectedShopFragment

class MainActivity : AppCompatActivity(), Navigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.content, ChooseShopFragment())
                .commit()
        }
    }

    override fun navigate(path: String) {
        when (path) {
            TO_SELECTION_DETAILS -> supportFragmentManager.beginTransaction()
                .replace(R.id.content, SelectedShopFragment())
                .addToBackStack(null)
                .commit()
            CLOSE_CHOOSE_SHOP,
            CLOSE_SELECTION_DETAILS -> onBackPressed()
        }
    }
}