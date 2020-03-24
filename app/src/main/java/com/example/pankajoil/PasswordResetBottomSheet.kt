package com.example.pankajoil

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.pankajoil.data.LoginCredentials
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

class PasswordResetBottomSheet : BottomSheetDialogFragment() {

    private var storedVerificationId: String = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var getOTP_btn: Button
    private lateinit var otp_edtText: TextInputLayout
    private lateinit var mobileNumber: TextInputLayout
    private lateinit var reset_dialog: android.app.AlertDialog
    private lateinit var newPassword: TextInputLayout
    private lateinit var repeatPassword: TextInputLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var view2: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        setupCallback()
        view2 = inflater.inflate(R.layout.password_sheet, container, false)
        otp_edtText = view2.findViewById(R.id.otp_Layout)
        mobileNumber = view2.findViewById(R.id.mobileNumber)
        newPassword = view2.findViewById(R.id.newPassword_Layout)
        repeatPassword = view2.findViewById(R.id.repeatPassword_Layout)
        getOTP_btn = view2.findViewById(R.id.getOTP_btn)
        mAuth = FirebaseAuth.getInstance()

        getOTP_btn.setOnClickListener {

            if (newPassword.editText!!.text.toString() == repeatPassword.editText!!.text.toString()) {
                Util.passwordSheet.isCancelable = false
                verify("+91" + mobileNumber.editText!!.text.toString())
            } else {
                Toast.makeText(activity, "Password do not match", Toast.LENGTH_SHORT).show()
            }

        }

        return view2
    }

    private fun verify(number: String) {

        PhoneAuthProvider.getInstance()
            .verifyPhoneNumber(number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, callbacks)
    }

    fun setupCallback() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("TAG", "onVerificationCompleted:$credential")
                val smsCode: String? = credential.smsCode
                if (!smsCode.isNullOrEmpty()) {
                    otp_edtText.editText!!.text =
                        Editable.Factory.getInstance().newEditable(smsCode)
                    verifyCode(smsCode)
                    Util.stopLoading(reset_dialog)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {


                Log.w("TAG", "onVerificationFailed", e)

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
                Util.passwordSheet.isCancelable = true


            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                Log.d("TAG", "onCodeSent:$verificationId")
                Util.startLoading(reset_dialog)
                storedVerificationId = verificationId
                resendToken = token

            }
        }

    }

    fun verifyCode(code: String) {
        val credentials: PhoneAuthCredential =
            PhoneAuthProvider.getCredential(storedVerificationId, code)
        activity?.let {
            mAuth.signInWithCredential(credentials).addOnCompleteListener(it) { task ->
                if (task.isSuccessful) {
                    //Code Verified
                    // Make network request
                    val service: APIServices =
                        Util.generalRetrofit.create((APIServices::class.java))
                    val cred = LoginCredentials(
                        mobileNumber.editText!!.text.toString(),
                        newPassword.editText!!.text.toString()

                    )
                    val call = service.changePassword(cred)
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
                                    Toast.makeText(activity, "Changed Success", Toast.LENGTH_SHORT)
                                        .show()
                                    Util.passwordSheet.dismiss()

                                }
                                400 -> {
                                    Toast.makeText(
                                        activity,
                                        "Number Not Registered",
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



