package com.example.pankajoil

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.jetpack_kotlin.ui.home.*
import com.example.pankajoil.data.LoginCredentials
import com.example.pankajoil.data.User
import com.example.pankajoil.service.APIServices
import com.example.pankajoil.utils.Util
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import dmax.dialog.SpotsDialog
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var banner: ImageView
    private var fragment: Fragment = HomeFragment()
    private lateinit var toolbar_title: TextView
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var headerView: View

    private lateinit var signin: Button
    private lateinit var forgot_text: TextView
    private lateinit var dialog: android.app.AlertDialog
    private lateinit var alert: AlertDialog
    private lateinit var login_number: TextInputLayout
    private lateinit var login_password: TextInputLayout


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar_title = toolbar.findViewById(R.id.toolbar_title)
        toolbar_title.text = "Pankaj Oil Industries"
        toolbar_title.textAlignment = View.TEXT_ALIGNMENT_CENTER
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        headerView = navView.getHeaderView(0)
        banner = findViewById(R.id.image_id)
        dialog =
            SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom).setMessage("Loading...")
                .setCancelable(false).build()

        Util.signin_text = headerView.findViewById(R.id.signin) as TextView
        Util.register = headerView.findViewById(R.id.register) as TextView
        Util.header_name = headerView.findViewById(R.id.header_name) as TextView
        Util.header_mobile = headerView.findViewById(R.id.header_mobile) as TextView
        Util.orsymbol_text = headerView.findViewById(R.id.orsymbol_tv) as TextView
        Util.profilePicture = headerView.findViewById(R.id.profilePicture) as CircleImageView

        Dexter.withActivity(this).withPermissions(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                }
            }).check()


        Util.setupLoggedInOrOutView(
            Util.header_name!!,
            Util.header_mobile!!,
            Util.signin_text!!,
            Util.register!!,
            Util.orsymbol_text!!,
            this
        )
        if (TokenSharedPreference(this).isTokenPresent()) {
            Util.startLoading(dialog)
            loadUserDetails(
                TokenSharedPreference(this).getMobileNumber(),
                TokenSharedPreference(this).getAuthKey()
            )
        }
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        supportFragmentManager.beginTransaction().add(R.id.host_fragment, HomeFragment()).commit()

        setRemoteConfig()

        Util.signin_text!!.setOnClickListener {
            loginPopup()
        }
        Util.register!!.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            Util.registerSheet.show(supportFragmentManager, "RegisterBottomSheet")
        }


    }

    fun loginPopup() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(
            GravityCompat.START
        )
        val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        val view: View = layoutInflater.inflate(R.layout.signin_layout, null)
        signin = view.findViewById(R.id.signin_btn)
        forgot_text = view.findViewById(R.id.forgot_text)
        login_number = view.findViewById(R.id.mobileNumber_Layout)
        login_password = view.findViewById(R.id.password_Layout)
        dialogBuilder.setView(view)
        dialogBuilder.setCancelable(true)
        alert = dialogBuilder.create()
        alert.show()
        forgot_text.setOnClickListener {
            alert.dismiss()
            Util.passwordSheet.show(supportFragmentManager, "PasswordResetBottomSheet")
        }
        signin.setOnClickListener {
            val view = this.currentFocus
            view?.let { v ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
            }
            Util.startLoading(dialog)

            getJWTToken(
                alert,
                login_number.editText!!.text.toString(),
                login_password.editText!!.text.toString(), this
            )
        }
    }


    private fun loadUserDetails(mobileNumber: String, authKey: String) {
        val service: APIServices = Util.generalRetrofit.create(APIServices::class.java)
        val call = service.getUserDetails(
            mobileNumber,
            authKey
        )
        call.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {

                Util.showToast(this@MainActivity, t.message + "- Restart the app", 1)

                Util.stopLoading(dialog)
                logout()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                when (response.code()) {
                    200 -> {
                        Util.user = response.body()
                        Util.header_name!!.text = "Hello " + Util.user!!.firstName + "!"
                        Util.header_mobile!!.text = "+91 " + Util.user!!.mobileNumber.toString()
                        if (Util.user!!.profileImage.isNotEmpty()) {
                            Picasso.get().load(Util.user!!.profileImage).into(Util.profilePicture!!)
                        } else {
                            Util.profilePicture?.setImageResource(R.drawable.test_image)
                        }

                        Util.stopLoading(dialog)


                    }
                    400 -> {
                        Util.showToast(this@MainActivity, "Number not registered", 0)

                        Util.stopLoading(dialog)

                    }
                }

            }

        })


    }

    private fun getJWTToken(

        alert: AlertDialog,
        id: String,
        pass: String,
        ctx: Context
    ) {
        val service: APIServices = Util.generalRetrofit.create(APIServices::class.java)
        val credentials = LoginCredentials(id, pass)
        val call = service.signIn(credentials)
        call.enqueue(object : Callback<JSONObject> {
            override fun onFailure(call: Call<JSONObject>, t: Throwable) {
                Util.stopLoading(dialog)
                Util.showToast(this@MainActivity, "Failed", 0)

            }

            override fun onResponse(call: Call<JSONObject>, response: Response<JSONObject>) {
                when (response.code()) {
                    200 -> {
                        TokenSharedPreference(this@MainActivity).setAuthKey(
                            response.headers().get("X-Auth-Token").toString(),
                            id
                        )
                        Util.setupLoggedInOrOutView(
                            Util.header_name!!,
                            Util.header_mobile!!,
                            Util.signin_text!!,
                            Util.register!!,
                            Util.orsymbol_text!!,
                            ctx
                        )
                        loadUserDetails(
                            TokenSharedPreference(ctx).getMobileNumber(),
                            TokenSharedPreference(ctx).getAuthKey()
                        )

                        alert.dismiss()
                    }

                    400 -> {

                        Util.showToast(
                            this@MainActivity, "Wrong Password or Mobile Number",
                            0
                        )

                        Util.stopLoading(dialog)
                    }
                    else -> {
                        Util.showToast(this@MainActivity, "Something went wrong...", 0)

                        Util.stopLoading(dialog)

                    }
                }
            }
        })
    }

    override fun onStart() {

        super.onStart()
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {

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
            val banner: ImageView = view.findViewById(R.id.banner_image)
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
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                if (TokenSharedPreference(this).isTokenPresent()) {
                    startActivity(Intent(this, Cart::class.java))
                } else {
                    Util.showToast(this, "Sign in your account ", 0)
                }

                true
            }
            R.id.action_search -> {
                startActivity(Intent(this, SearchActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                fragment = HomeFragment()
            }
            R.id.nav_profile -> {

                if (TokenSharedPreference(this).isTokenPresent()) {
                    fragment = ProfileFragment()
                } else {
                    loginPopup()
                }
            }
            R.id.nav_cart -> {
                //  fragment = CartFragment()
                if (TokenSharedPreference(this).isTokenPresent()) {
                    startActivity(Intent(this, Cart::class.java))
                } else {
                    loginPopup()
                }

            }
            R.id.nav_orders -> {
                if (TokenSharedPreference(this).isTokenPresent()) {
                    fragment = OrderFragment()
                } else {
                    loginPopup()
                }
            }
            R.id.nav_wishList -> {
                if (TokenSharedPreference(this).isTokenPresent()) {
                    fragment = WishlistFragment()
                } else {
                    loginPopup()
                }

            }
            R.id.nav_notification -> {
                fragment = NotificationFragment()
                Util.showToast(this, "Gello", 0)

            }
            R.id.nav_feedback -> {
                fragment = NotificationFragment()

            }
            R.id.nav_rate -> {
                fragment = NotificationFragment()
                logout()
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.host_fragment, fragment)
            .commit()

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setRemoteConfig() {

        //Remote Config
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        val hashMap: HashMap<String, Any> = HashMap(3)
        hashMap.put(
            "image_link",
            "https://www.daimler.com/bilder/produkte/pkw/mercedes-benz/cla/neuer-cla/18c0973-028-w768xh1536-cutout.jpg"
        )
        hashMap.put("cancellable", true)
        hashMap.put("show_enable", false)

        remoteConfig.setDefaultsAsync(hashMap)

    }

    fun logout() {
        Util.signOut(
            Util.header_name!!,
            Util.header_mobile!!,
            Util.signin_text!!,
            Util.register!!,
            Util.orsymbol_text!!,
            this
        )

    }


}
