package com.example.jetpack_kotlin.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pankajoil.R
import com.example.pankajoil.adapter.WishlistGridAdapter
import com.example.pankajoil.data.WishlistProducts
import com.example.pankajoil.utils.Util
import java.util.ArrayList

class WishlistFragment : Fragment() {



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_wishlist, container, false)
        var rv:RecyclerView = root.findViewById(R.id.wishlistRv)
        var adapter = WishlistGridAdapter(Util.user!!.wishlistProducts as ArrayList<WishlistProducts>,root.context)
        if (Util.user!!.wishlistProducts.isNotEmpty()) {
            rv.layoutManager = GridLayoutManager(root.context, 2)
            rv.adapter = adapter
            adapter.notifyDataSetChanged()
        }
        return root
    }
}