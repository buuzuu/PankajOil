package com.example.pankajoil.bottomSheets

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.pankajoil.R
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
import dmax.dialog.SpotsDialog
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
        reset_dialog = SpotsDialog.Builder().setContext(view2.context).setTheme(R.style.Custom).setMessage("Please wait ...").setCancelable(false).build()

        getOTP_btn.setOnClickListener {

            if (newPassword.editText!!.text.toString() == repeatPassword.editText!!.text.toString()) {
                Util.passwordSheet.isCancelable = false
                verify("+91" + mobileNumber.editText!!.text.toString())
                Util.startLoading(reset_dialog)
            } else {
                Util.showToast(
                    view2.context,
                    "Password do not match",1
                )
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
                val smsCode: String? = credential.smsCode
                if (!smsCode.isNullOrEmpty()) {
                    otp_edtText.editText!!.text =
                        Editable.Factory.getInstance().newEditable(smsCode)
                    verifyCode(smsCode)
                    Util.stopLoading(reset_dialog)
                }else{
                    sendToDataBase(credential)
                    Util.stopLoading(reset_dialog)
                    Util.passwordSheet.dismiss()

                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {


                    Util.showToast(
                        view2.context,
                        "Invalid PhoneNumber",1
                    )
                    Util.stopLoading(reset_dialog)
                    // [END_EXCLUDE]
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(
                        view2.context,
                        "Quota Exceeded",
                        Toast.LENGTH_SHORT
                    ).show()
                    Util.startLoading(reset_dialog)
                }
                Util.passwordSheet.isCancelable = true


            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                Log.d("TAG", "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token

            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)
                Toast.makeText(view2.context,"Timeout: Retry",Toast.LENGTH_SHORT).show()
                Util.showToast(
                    view2.context,
                    "Timeout: Retry",1
                )
            }
        }

    }

    fun verifyCode(code: String) {
        val credentials: PhoneAuthCredential =
            PhoneAuthProvider.getCredential(storedVerificationId, code)
        sendToDataBase(credentials)

    }

    fun sendToDataBase(credential: PhoneAuthCredential){
        activity?.let {
            mAuth.signInWithCredential(credential).addOnCompleteListener(it) { task ->
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

                            Util.showToast(
                                view2.context,
                                "Something went wrong",1
                            )
                        }
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            when (response.code()) {
                                200 -> {

                                    Util.showToast(
                                        view2.context,
                                        "Changed Success",1
                                    )
                                    Util.passwordSheet.dismiss()

                                }
                                400 -> {
                                    Util.showToast(
                                        view2.context,
                                        "Number not registered",1
                                    )
                                    Util.stopLoading(reset_dialog)

                                }
                            }
                        }

                    })
                }

            }
        }

    }


}



