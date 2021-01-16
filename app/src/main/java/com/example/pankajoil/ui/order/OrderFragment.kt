package com.example.pankajoil.ui.order

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pankajoil.MainActivity
import com.example.pankajoil.R
import com.example.pankajoil.adapter.OrdersAdapter
import com.example.pankajoil.utils.Util

class OrderFragment : Fragment() {


    lateinit var imageView: ImageView
    var adapter: OrdersAdapter? = null
    lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_order, container, false)
        imageView = root.findViewById(R.id.noOrderImage)
        recyclerView = root.findViewById(R.id.orderRv)
        if (Util.user!!.orders.isEmpty()) {
            imageView.visibility = View.VISIBLE
        } else {
            recyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext)
            (recyclerView.layoutManager as LinearLayoutManager).reverseLayout =true
            (recyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true
            recyclerView.adapter = OrdersAdapter(Util.user!!.orders)
            adapter?.notifyDataSetChanged()
        }
        imageView.setOnClickListener {
            startActivity(Intent(root.context, MainActivity::class.java))
        }



        return root
    }
}