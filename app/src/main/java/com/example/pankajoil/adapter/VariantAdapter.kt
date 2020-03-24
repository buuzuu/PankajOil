package com.example.pankajoil.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.example.pankajoil.R
import com.example.pankajoil.`interface`.OnVariantItemClickListner
import com.example.pankajoil.data.Product
import com.example.pankajoil.data.Variant
import com.squareup.picasso.Picasso

class VariantAdapter(product: Product?, var clickListner: OnVariantItemClickListner) :
    RecyclerView.Adapter<VariantAdapter.VariantViewHolder>() {
    var tracker: SelectionTracker<Long>? = null

    var prod: Product? = null
    init {
        prod = product
        setHasStableIds(true)

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VariantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.variant_item_layout, null)
        return VariantViewHolder(view)
    }

    override fun getItemCount(): Int {
        return prod!!.variants.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: VariantViewHolder, position: Int) {

        val key = getItemId(position)
        val selected = tracker!!.isSelected(key)

        holder.initialize(prod!!.variants[position], clickListner,selected)
    }

    inner class VariantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ImageView? = null
        var imageText: TextView? = null
        var layout: RelativeLayout? = null

        init {
            image = itemView.findViewById(R.id.item_image)
            imageText = itemView.findViewById(R.id.item_text)
            layout = itemView.findViewById(R.id.relativeLayout)
        }

        fun initialize(variant: Variant, action: OnVariantItemClickListner , isSelected:Boolean) {
            Picasso.get().load(variant.url).into(image)
            imageText!!.text = variant.size.toString() + " ℓ"
            itemView.setOnClickListener {
                action.onVariantItemClick(variant, adapterPosition)
            }
            layout!!.isActivated = isSelected

        }
        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long? = itemId
            }


    }



}