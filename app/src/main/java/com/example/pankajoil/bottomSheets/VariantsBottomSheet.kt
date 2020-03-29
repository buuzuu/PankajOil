package com.example.pankajoil.bottomSheets

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pankajoil.R
import com.example.pankajoil.adapter.OnVariantClickListner
import com.example.pankajoil.adapter.VariantRecyclerAdapter
import com.example.pankajoil.data.Product
import com.example.pankajoil.data.Variant
import com.example.pankajoil.utils.Util
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.variant_sheet.view.*

class VariantsBottomSheet(product: Product) :
    BottomSheetDialogFragment(), OnVariantClickListner {
    var prod: Product? = null
    var list:List<Variant>? =null
    init {
        prod = product
        list = prod!!.variants
    }

    private lateinit var variant_view: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        variant_view = inflater.inflate(R.layout.variant_sheet, container, false)
        variant_view.rv.layoutManager = LinearLayoutManager(
            activity!!.applicationContext,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        val adapter = VariantRecyclerAdapter(list, this)
        variant_view.rv.adapter = adapter
        return variant_view
    }

    override fun onVariantClick(variant: Variant?) {
        if (variant != null) {
            updateUI(variant)

        }
    }

    private fun updateUI(variant: Variant?) {
        Picasso.get().load(variant!!.url).into(Util.prod_Image)
        Util.prod_Size!!.text = "${variant.size} ℓ"
        Util.prod_Quantity!!.text = "${variant.perCarton} piece"
        Util.prod_Item_Price!!.text = "₹ ${variant.price}"

        Util.current_Variant = variant

        Handler().postDelayed({
            Util.prod_BottomSheet!!.dismiss()
        }, 500)

    }

}