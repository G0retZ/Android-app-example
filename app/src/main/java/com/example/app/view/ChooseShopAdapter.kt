package com.example.app.view

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.app.R
import com.example.app.presentation.chooseshop.ChooseShopListItem
import com.example.app.view.ChooseShopAdapter.ChooseShopViewHolder
import com.google.android.material.snackbar.Snackbar

internal class ChooseShopAdapter(
    private val list: List<ChooseShopListItem>,
    private val selectionListener: (Int) -> Unit
) : RecyclerView.Adapter<ChooseShopViewHolder>() {

    var selection: Int? = null
        set(value) {
            if (field != value) {
                field?.let(this::notifyItemChanged)
                value?.let(this::notifyItemChanged)
                field = value
            }
        }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseShopViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_choose_shop_list_item, parent, false)
        return ChooseShopViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChooseShopViewHolder, position: Int) {
        val chooseVehicleListItem = list[position]
        holder.shopName.text = chooseVehicleListItem.name
        holder.shopAddress.text = chooseVehicleListItem.address
        holder.itemView.setOnClickListener {
            selectionListener(position)
        }
        if (position == selection) {
            R.drawable.ic_item_background_selected
        } else {
            R.drawable.ic_item_background
        }.let(holder.itemView::setBackgroundResource)

        holder.getDirections.setOnClickListener(null)
        ContextCompat
            .getColorStateList(
                holder.itemView.context,
                if (position == selection) R.color.text_color else R.color.button_color
            )
            .let(holder.getDirections::setTextColor)


        chooseVehicleListItem.navigationQuery
            .also {
                holder.getDirections.isEnabled = it != null
            }
            ?.let {
                holder.getDirections.setOnClickListener { v ->
                    val navigationIntent = Intent(Intent.ACTION_VIEW)
                    navigationIntent.data = Uri.parse(it)
                    if (navigationIntent.resolveActivity(v.context.packageManager) != null) {
                        v.context.startActivity(navigationIntent)
                    } else {
                        Snackbar
                            .make(v, "Oops! Install navigation app first.", Snackbar.LENGTH_SHORT)
                            .show()
                    }

                }
            }

        val radius = holder.itemView.context.resources.displayMetrics.density * 8

        Glide.with(holder.itemView.context)
            .load(chooseVehicleListItem.thumbnail)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .transform(CenterCrop(), RoundedCorners(radius.toInt()))
            .into(holder.thumbnail)
    }

    internal inner class ChooseShopViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val shopName: TextView = itemView.findViewById(R.id.shopName)
        internal val shopAddress: TextView = itemView.findViewById(R.id.shopAddress)
        internal val getDirections: TextView = itemView.findViewById(R.id.getDirections)
        internal val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
    }
}