package com.example.pankajoil


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.example.jetpack_kotlin.ui.home.*
import com.example.pankajoil.data.LoginCredentials
import com.example.pankajoil.data.Product
import com.example.pankajoil.data.User
import com.example.pankajoil.service.APIServices
import com.example.pankajoil.service.RetrofitService
import com.example.pankajoil.utils.Util
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.squareup.picasso.Picasso
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var banner: ImageView
    lateinit var fragment: Fragment
    private lateinit var edtText: EditText
    private lateinit var toolbar_title: TextView
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var headerView: View
    private lateinit var signin_text: TextView
    private lateinit var orsymbol_text: TextView
    private lateinit var signin: Button
    private lateinit var register: TextView
    private lateinit var forgot_text: TextView
    private lateinit var header_name: TextView
    private lateinit var header_mobile: TextView
    private lateinit var lottie: LottieAnimationView
    private lateinit var signin_lottie: LottieAnimationView
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
        toolbar_title.text = "Pankaj Oil Indistries"
        toolbar_title.textAlignment = View.TEXT_ALIGNMENT_CENTER
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        headerView = navView.getHeaderView(0)
        banner = findViewById(R.id.image_id)
        searchView = findViewById(R.id.searchView)
        lottie = findViewById(R.id.animation_view)
        Util.startLoading(lottie)
        signin_text = headerView.findViewById(R.id.signin) as TextView
        register = headerView.findViewById(R.id.register) as TextView
        header_name = headerView.findViewById(R.id.header_name) as TextView
        header_mobile = headerView.findViewById(R.id.header_mobile) as TextView
        orsymbol_text = headerView.findViewById(R.id.orsymbol_tv) as TextView

        Util.setupLoggedInOrOutView(
            header_name,
            header_mobile,
            signin_text,
            register,
            orsymbol_text,
            this
        )
        if (TokenSharedPreference(this).isTokenPresent()) {
            loadUserDetails(
                TokenSharedPreference(this).getMobileNumber(),
                TokenSharedPreference(this).getAuthKey()
            )
        }

        edtText = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        edtText.setTextColor(Color.BLACK)
        edtText.setHintTextColor(Color.parseColor("#909090"))

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        supportFragmentManager.beginTransaction().add(R.id.host_fragment, HomeFragment()).commit()

        setRemoteConfig()
        //Retrofit..........

        val service: APIServices =
            RetrofitService.retrofitService(this).create(APIServices::class.java)
        val call = service.getProducts()
        call.enqueue(object : Callback<List<Product>> {
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.d("TAG", t.message.toString())
            }

            override fun onResponse(data: Call<List<Product>>, response: Response<List<Product>>) {

                if (response.code() == 200) {
                    val products: List<Product> = response.body()!!
                    Util.stopLoading(lottie)
//                    Toast.makeText(
//                        this@MainActivity,
//                        "Success ${products[16].productName}",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }

            }
        })

        signin_text.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(
                GravityCompat.START
            )
            val dialogBuilder = AlertDialog.Builder(this, R.style.MyDialogTheme)
            val view: View = layoutInflater.inflate(R.layout.signin_layout, null)
            signin = view.findViewById(R.id.signin_btn)
            forgot_text = view.findViewById(R.id.forgot_text)
            signin_lottie = view.findViewById(R.id.signin_view)
            login_number = view.findViewById(R.id.mobileNumber_Layout)
            login_password = view.findViewById(R.id.password_Layout)
            dialogBuilder.setView(view)
            dialogBuilder.setCancelable(true)
            alert = dialogBuilder.create()
            alert.show()
            forgot_text.setOnClickListener{
                alert.dismiss()
                Util.passwordSheet.show(supportFragmentManager , "PasswordResetBottomSheet")
            }
            signin.setOnClickListener {
                Util.startLoading(signin_lottie)

                getJWTToken(
                    signin_lottie, alert,
                    login_number.editText!!.text.toString(),
                    login_password.editText!!.text.toString(), this
                )
            }
        }
        register.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            Util.registerSheet.show(supportFragmentManager, "RegisterBottomSheet")
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
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                when (response.code()) {
                    200 -> {
                        val user: User? = response.body()
                        Toast.makeText(this@MainActivity, user!!.companyName, Toast.LENGTH_LONG)
                            .show()
                    }
                    400 -> {
                        Toast.makeText(this@MainActivity, "Number Unregistered", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }

        })


    }

    private fun getJWTToken(
        view: LottieAnimationView,
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
                Util.stopLoading(view)
                Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<JSONObject>, response: Response<JSONObject>) {
                when (response.code()) {
                    200 -> {
                        TokenSharedPreference(this@MainActivity).setAuthKey(
                            response.headers().get("X-Auth-Token").toString(),
                            id
                        )
                        Util.setupLoggedInOrOutView(
                            header_name,
                            header_mobile,
                            signin_text,
                            register,
                            orsymbol_text,
                            ctx
                        )
                        loadUserDetails(
                            TokenSharedPreference(ctx).getMobileNumber(),
                            TokenSharedPreference(ctx).getAuthKey()
                        )
                        Util.stopLoading(view)
                        alert.dismiss()
                    }

                    400 -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Wrong Password or Mobile Number",
                            Toast.LENGTH_SHORT
                        ).show()
                        Util.stopLoading(view)
                    }
                    else -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Something went wrong...",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        Util.stopLoading(view)

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
            R.id.action_search -> {
                Toast.makeText(
                    this, "Clicked",
                    Toast.LENGTH_LONG
                ).show()
                true
            }
            R.id.action_notification -> {
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
            R.id.nav_profile -> {
                Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
                fragment = ProfileFragment()

            }
            R.id.nav_cart -> {
                Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
                fragment = CartFragment()

            }
            R.id.nav_orders -> {
                Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
                fragment = OrderFragment()

            }
            R.id.nav_wishList -> {
                Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
                fragment = WishlistFragment()

            }
            R.id.nav_notification -> {
                Toast.makeText(this, "Messages clicked", Toast.LENGTH_SHORT).show()
                fragment = NotificationFragment()

            }
            R.id.nav_feedback -> {
                fragment = NotificationFragment()
                searchView.visibility = View.VISIBLE
                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_rate -> {
                fragment = NotificationFragment()
                searchView.visibility = View.GONE
                logout()
                Toast.makeText(this, "Sign out clicked", Toast.LENGTH_SHORT).show()
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
        val hashMap: HashMap<String, Any> = HashMap<String, Any>(3)
        hashMap.put(
            "image_link",
            "https://www.daimler.com/bilder/produkte/pkw/mercedes-benz/cla/neuer-cla/18c0973-028-w768xh1536-cutout.jpg"
        )
        hashMap.put("cancellable", true)
        hashMap.put("show_enable", false)

        remoteConfig.setDefaultsAsync(hashMap)


    }

     fun logout (){
        Util.signOut(header_name, header_mobile, signin_text, register, orsymbol_text, this)

    }



}
