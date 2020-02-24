package com.example.pankajoil

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.example.pankajoil.data.LoginCredentials
import com.example.pankajoil.data.SignupUser
import com.example.pankajoil.service.APIServices
import com.example.pankajoil.utils.Util
import com.google.android.gms.tasks.TaskExecutors
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class RegisterBottomSheet : BottomSheetDialogFragment() {

    private lateinit var view3: View
    private lateinit var firstName: TextInputLayout
    private lateinit var lastName: TextInputLayout
    private lateinit var companyName: TextInputLayout
    private lateinit var mobileNumber: TextInputLayout
    private lateinit var email: TextInputLayout
    private lateinit var password: TextInputLayout
    private lateinit var gstin: TextInputLayout
    private lateinit var address: TextInputLayout
    private lateinit var otp: TextInputLayout
    private lateinit var getOTP_btn: Button
    private lateinit var register_view: LottieAnimationView
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var storedVerificationId: String = ""
    private lateinit var mAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        view3 = inflater.inflate(R.layout.register_sheet, container, false)
        setupID(view3)
        setupCallback()
        mAuth = FirebaseAuth.getInstance()

        getOTP_btn.setOnClickListener {
            if (firstName.editText!!.text.isNullOrBlank() ||
                companyName.editText!!.text.isNullOrBlank() ||
                mobileNumber.editText!!.text.isNullOrBlank() ||
                password.editText!!.text.isNullOrBlank() ||
                gstin.editText!!.text.isNullOrBlank() ||
                address.editText!!.text.isNullOrBlank()
            ) {
                Toast.makeText(activity, "Please fill all the details", Toast.LENGTH_SHORT).show()
            } else {
                Util.startLoading(register_view)
                verify("+91" + mobileNumber.editText!!.text.toString())
                Util.registerSheet.isCancelable = false
                Util.startLoading(register_view)

            }
        }

        return view3
    }

    private fun setupID(view: View) {
        firstName = view.findViewById(R.id.firstName)
        lastName = view.findViewById(R.id.lastName)
        companyName = view.findViewById(R.id.companyName)
        mobileNumber = view.findViewById(R.id.mobileNumber)
        email = view.findViewById(R.id.email)
        password = view.findViewById(R.id.password)
        gstin = view.findViewById(R.id.gstin)
        address = view.findViewById(R.id.address)
        otp = view.findViewById(R.id.otp)
        getOTP_btn = view.findViewById(R.id.getOTP_btn)
        register_view = view.findViewById(R.id.register_view)
    }

    fun setupCallback() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val smsCode: String? = credential.smsCode
                if (!smsCode.isNullOrEmpty()) {
                    otp.editText!!.text =
                        Editable.Factory.getInstance().newEditable(smsCode)
                    verifyCode(smsCode)
                    Util.stopLoading(register_view)
                    Util.registerSheet.dismiss()
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {

                    Toast.makeText(
                        activity!!.applicationContext,
                        "Invalid PhoneNumber",
                        Toast.LENGTH_SHORT
                    ).show()
                    // [END_EXCLUDE]
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Quota Exceeded",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId

            }
        }

    }

    private fun verify(number: String) {

        PhoneAuthProvider.getInstance()
            .verifyPhoneNumber(number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, callbacks)
    }

    fun verifyCode(code: String) {
        val credentials: PhoneAuthCredential =
            PhoneAuthProvider.getCredential(storedVerificationId, code)
        activity?.let {
            mAuth.signInWithCredential(credentials).addOnCompleteListener(it) { task ->
                if (task.isSuccessful) {
                    //Code Verified
                    // Make network request
                    val service: APIServices = Util.generalRetrofit.create(APIServices::class.java)
                    val signup_user = SignupUser(
                        firstName.editText!!.text.toString(),
                        lastName.editText!!.text.toString(),
                        companyName.editText!!.text.toString(),
                        mobileNumber.editText!!.text.toString(),
                        email.editText!!.text.toString(),
                        password.editText!!.text.toString(),
                        address.editText!!.text.toString(),
                        gstin.editText!!.text.toString()
                    )
                    val call = service.createUser(signup_user)
                    call.enqueue(object : Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            when (response.code()) {
                                200 -> {

                                    Util.registerSheet.dismiss()

                                }
                                400 -> {
                                    Toast.makeText(
                                        activity,
                                        "Number Already Registered",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            }
                        }

                    })
                }

            }
        }

    }


}