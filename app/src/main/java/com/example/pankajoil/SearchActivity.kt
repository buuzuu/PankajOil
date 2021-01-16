package com.example.pankajoil

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pankajoil.adapter.GridAdapter
import com.example.pankajoil.adapter.StaggeredAdapter
import com.example.pankajoil.utils.Util

import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var toolbar_title: TextView
    var adapter: StaggeredAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        toolbar = findViewById(R.id.toolbar)
        toolbar_title = toolbar.findViewById(R.id.toolbar_title)
        toolbar_title.text = "Search"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
       // product_rv!!.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        product_rv!!.layoutManager = LinearLayoutManager(this)
        adapter = StaggeredAdapter(this,Util.products!!.shuffled())
        product_rv.adapter = adapter


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val menuItem = menu!!.findItem(R.id.action_search)
        val searchView: SearchView = menuItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false;
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter!!.filter.filter(newText)

                return false;
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

}
