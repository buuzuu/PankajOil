package com.example.pankajoil

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.pankajoil.utils.Util
import com.shuhart.stepview.StepView
import kotlinx.android.synthetic.main.activity_order_summary.*

class OrderSummary : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    lateinit var stepView: StepView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        stepView = findViewById(R.id.step_view)
        stepView.go(1, true)
        setupUserDetails()


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupUserDetails(){
        if (Util.user !=null){
            gstin.text = Util.user!!.gstin
            userName.text = Util.user!!.firstName +" " +Util.user!!.firstName
            shopName.text = Util.user!!.companyName
            address.text = Util.user!!.address
            mobileNumber.text = Util.user!!.mobileNumber.toString()
        }
    }
}