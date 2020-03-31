package com.example.pankajoil.bottomSheets

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.pankajoil.R
import com.example.pankajoil.data.SignupUser
import com.example.pankajoil.service.APIServices
import com.example.pankajoil.utils.Util
import com.google.android.gms.tasks.TaskExecutors
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import dmax.dialog.SpotsDialog
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
    private lateinit var register_dialog: android.app.AlertDialog

    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private var storedVerificationId: String = ""
    private var resendToken: String = ""
    private lateinit var mAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        view3 = inflater.inflate(R.layout.register_sheet, container, false)
        register_dialog = SpotsDialog.Builder().setContext(view3.context).setTheme(R.style.Custom).setMessage("Getting OTP ...").setCancelable(false).build()

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
                Toast.makeText(view3.context, "Please fill all the details", Toast.LENGTH_SHORT).show()
            } else {
                Util.startLoading(register_dialog)
                verify( "+91"+mobileNumber.editText!!.text.toString())
                Util.registerSheet.isCancelable = false

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
    }

    fun setupCallback() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val smsCode: String? = credential.smsCode
                Toast.makeText(activity!!.applicationContext,smsCode, Toast.LENGTH_SHORT).show()

                if (!smsCode.isNullOrEmpty()) {
                    otp.editText!!.text =
                        Editable.Factory.getInstance().newEditable(smsCode)
                    verifyCode(smsCode)
                    Util.stopLoading(register_dialog)
                    Util.registerSheet.dismiss()
                }else{
                    sendToDatabase(credential)
                    Util.stopLoading(register_dialog)
                    Util.registerSheet.dismiss()
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {

                    Toast.makeText(
                        view3.context,
                        "Invalid PhoneNumber",
                        Toast.LENGTH_SHORT
                    ).show()
                    Util.stopLoading(register_dialog)
                    // [END_EXCLUDE]
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(
                        view3.context,
                        "Quota Exceeded",
                        Toast.LENGTH_SHORT
                    ).show()
                    Util.stopLoading(register_dialog)
                }else if (e is FirebaseAuthInvalidUserException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(
                        view3.context,
                        "Invalid User",
                        Toast.LENGTH_SHORT
                    ).show()
                    Util.stopLoading(register_dialog)
                }

            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                storedVerificationId = verificationId
                resendToken = token.toString()

            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                Util.stopLoading(register_dialog)
                Toast.makeText(view3.context,"Something went wrong.Please retry", Toast.LENGTH_SHORT).show()
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
        sendToDatabase(credentials)
    }

    fun sendToDatabase(credential: PhoneAuthCredential){
        activity?.let {
            mAuth.signInWithCredential(credential).addOnCompleteListener(it) { task ->
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
                            Toast.makeText(view3.context, "Something went wrong", Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            when (response.code()) {
                                200 -> {

                                    Util.registerSheet.dismiss()
                                    Toast.makeText(view3.context, "Account Created", Toast.LENGTH_LONG).show()

                                }
                                400 -> {
                                    Toast.makeText(
                                        view3.context,
                                        "Number Already Registered",
                                        Toast.LENGTH_LONG
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