package com.example.pankajoil.bottomSheets

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pankajoil.R
import com.example.pankajoil.`interface`.OnVariantDataSent
import com.example.pankajoil.adapter.OnVariantClickListner
import com.example.pankajoil.adapter.VariantRecyclerAdapter
import com.example.pankajoil.data.Product
import com.example.pankajoil.data.Variant
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.variant_sheet.view.*

class VariantsBottomSheet(product: Product, clickListner: OnVariantDataSent) :
    BottomSheetDialogFragment(), OnVariantClickListner {
    var prod: Product? = null
    var list:List<Variant>? =null
    var listner:OnVariantDataSent?=null
    init {
        prod = product
        listner=clickListner
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
        Log.d("TAG",variant!!.price.toString() + "From Bottomsheet")
        if (variant != null) {
            listner!!.onRecieveVariant(variant)
        }
    }

}