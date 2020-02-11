package com.example.pankajoil


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.jetpack_kotlin.ui.gallery.GalleryFragment
import com.example.jetpack_kotlin.ui.home.HomeFragment
import com.example.jetpack_kotlin.ui.send.SendFragment
import com.example.jetpack_kotlin.ui.share.ShareFragment
import com.example.jetpack_kotlin.ui.slideshow.SlideshowFragment
import com.example.jetpack_kotlin.ui.tools.ToolsFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.squareup.picasso.Picasso
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var banner: ImageView
    lateinit var fragment: Fragment
    lateinit var edtText: EditText
    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        banner = findViewById(R.id.image_id)
        searchView = findViewById(R.id.searchView)
        edtText = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        edtText.setTextColor(Color.BLACK)
        edtText.setHintTextColor(Color.parseColor("#909090"))

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
//        toolbar.setNavigationIcon(R.drawable.ic_menu_camera)
        supportFragmentManager.beginTransaction()
            .add(R.id.host_fragment, HomeFragment())
            .commit()

        //Remote Config
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        val hashMap: HashMap<String, Any> = HashMap<String, Any>(3)
        hashMap.put(
            "image_link",
            "https://www.daimler.com/bilder/produkte/pkw/mercedes-benz/cla/neuer-cla/18c0973-028-w768xh1536-cutout.jpg"
        )
        hashMap.put("cancellable", true)
        hashMap.put("show_enable", false)

        remoteConfig.setDefaultsAsync(hashMap)


    }

    override fun onStart() {
        super.onStart()
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {

                Log.d("TAG", remoteConfig.getBoolean("show_enable").toString())
                Log.d("TAG", remoteConfig.getBoolean("cancellable").toString())
                Log.d("TAG", remoteConfig.getString("image_link"))
                setUpDialogBanner(
                    remoteConfig.getString("image_link"),
                    remoteConfig.getBoolean("cancellable"),
                    remoteConfig.getBoolean("show_enable")
                )
            } else {
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setUpDialogBanner(string: String, cancellable: Boolean, show_enable: Boolean) {

        if (show_enable) {
            val dialogBuilder = AlertDialog.Builder(this)
            val view: View = layoutInflater.inflate(R.layout.banner_layout, null)
            dialogBuilder.setView(view)
            var banner: ImageView = view.findViewById(R.id.banner_image)
            Picasso.get().load(string).into(banner)
            dialogBuilder.setCancelable(cancellable)
            val alert = dialogBuilder.create()
            alert.show()
        }


    }

    override fun onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                Toast.makeText(
                    this, "Clicked",
                    Toast.LENGTH_LONG
                ).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {

                Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                fragment = HomeFragment()
            }
            R.id.nav_gallery -> {
                Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
                fragment = GalleryFragment()

            }
            R.id.nav_slideshow -> {
                Toast.makeText(this, "Friends clicked", Toast.LENGTH_SHORT).show()
                fragment = SlideshowFragment()

            }
            R.id.nav_tools -> {
                Toast.makeText(this, "Update clicked", Toast.LENGTH_SHORT).show()
                fragment = ToolsFragment()
            }
            R.id.nav_share -> {
                fragment = ShareFragment()
                searchView.visibility = View.VISIBLE
                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_send -> {
                fragment = SendFragment()
                searchView.visibility = View.GONE
                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.host_fragment, fragment)
            .commit()

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


}
