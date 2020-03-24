package com.example.pankajoil.`interface`

import com.example.pankajoil.data.Product
import com.example.pankajoil.data.Variant

interface OnVariantItemClickListner {

    fun onVariantItemClick(variant: Variant, position: Int)

}