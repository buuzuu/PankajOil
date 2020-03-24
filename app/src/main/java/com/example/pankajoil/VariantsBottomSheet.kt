package com.example.pankajoil

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pankajoil.`interface`.OnGridItemClickListner
import com.example.pankajoil.`interface`.OnSelectionMade
import com.example.pankajoil.`interface`.OnVariantItemClickListner
import com.example.pankajoil.adapter.VariantAdapter
import com.example.pankajoil.data.Product
import com.example.pankajoil.data.Variant
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.variant_sheet.*
import kotlinx.android.synthetic.main.variant_sheet.view.*

class VariantsBottomSheet(product: Product,var selectionDone: OnSelectionMade) : BottomSheetDialogFragment(),
    OnVariantItemClickListner {
    var prod: Product? = null

    init {
        prod = product
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
        val adapter = VariantAdapter(this!!.prod, this)
        variant_view.rv.adapter = adapter
        val keyProvider = StableIdKeyProvider(variant_view.rv)
        val mSelectionTracker = SelectionTracker.Builder(
            "simple-demo",
            variant_view.rv,
            keyProvider,
            MyVariantsLookUp(variant_view.rv),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectSingleAnything())
            .build()

        adapter.tracker = mSelectionTracker
        mSelectionTracker?.addObserver(
            object :SelectionTracker.SelectionObserver<Long>(){
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    val items = mSelectionTracker?.selection!!.size()
                    if (items == 1) {
                        val selection:Selection<Long> = mSelectionTracker?.selection
                        val a = selection.map {
                            it.toInt()
                        }
                        Log.d("TAG", a[0].toString())
                        selectionDone.onVariantSelected(a[0].toString())
                    }
                }
            }
        )
        return variant_view
    }

    override fun onVariantItemClick(variant: Variant, position: Int) {
        Snackbar.make(variant_view,"Long Press To Select",Snackbar.LENGTH_LONG).show()
    }
}