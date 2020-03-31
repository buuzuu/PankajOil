package com.example.jetpack_kotlin.ui.home


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.pankajoil.R
import com.example.pankajoil.TokenSharedPreference
import com.example.pankajoil.data.ProfileImage
import com.example.pankajoil.service.APIServices
import com.example.pankajoil.utils.FileUtils
import com.example.pankajoil.utils.ProgressRequestBody
import com.example.pankajoil.utils.Util
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.fragment_profile.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment(), ProgressRequestBody.UploadCallbacks {


    lateinit var logout: Button
    lateinit var orderCount: TextView
    lateinit var wishlistCount: TextView
    lateinit var editImage: CircleImageView
    lateinit var profileImage: CircleImageView
    lateinit var preference: TokenSharedPreference
    private var PICK_IMAGE = 100
    private var link:String? = null
    private lateinit var dialog: android.app.AlertDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialog = SpotsDialog.Builder().setContext(activity).setTheme(R.style.Custom)
            .setMessage("Uploading...").setCancelable(false).build()
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        logout = root.findViewById(R.id.logout)
        orderCount = root.findViewById(R.id.orderCount)
        wishlistCount = root.findViewById(R.id.wishlistCount)
        setupViews(root)
        logout.setOnClickListener {
            Util.signOut(
                Util.header_name!!,
                Util.header_mobile!!,
                Util.signin_text!!,
                Util.register!!,
                Util.orsymbol_text!!,
                activity!!.applicationContext
            )
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.host_fragment, HomeFragment())
                .commit()
        }

        orderCount.text = Util.user!!.orders.size.toString()
        wishlistCount.text = Util.user!!.wishlistProducts.size.toString()
        profileImage.setOnClickListener {
        }

        editImage.setOnClickListener {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)

        }
        return root
    }


    private fun setupViews(root: View) {
        editImage = root.findViewById(R.id.uploadImage)
        profileImage = root.findViewById(R.id.profileImage)
        preference = TokenSharedPreference(activity!!.applicationContext)
        if (preference.isTokenPresent()) {
            link = Util.user!!.profileImage

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!link.isNullOrEmpty()){
            Picasso.get().load(link).into(profileImage)
        }else{
            profileImage.setImageResource(R.drawable.test_image)
        }
        gstin.text = Util.user!!.gstin
        shopName.text = Util.user!!.companyName
        mobileNumber.text = Util.user!!.mobileNumber.toString()
        email.text = Util.user!!.email
        address.text = Util.user!!.address




    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE) {
            val imageURI: Uri? = data!!.data
            val file = FileUtils.getFile(activity, imageURI)

//            val requestFile:RequestBody = RequestBody.create(
//                MediaType.parse( "image/*"),
//                file
//            )
            val requestFile = ProgressRequestBody(file, this)
            Util.startLoading(dialog)

            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("profile_image", file.name, requestFile)
            val service: APIServices = Util.generalRetrofit.create(APIServices::class.java)
            val call = service.uploadProfileImage(
                preference.getMobileNumber(),
                body,
                preference.getAuthKey()
            )
            call.enqueue(object : Callback<ProfileImage> {
                override fun onFailure(call: Call<ProfileImage>, t: Throwable) {

                    Log.d("TAG", t.message)
                    Toast.makeText(activity, "Something Went Wrong .", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onResponse(
                    call: Call<ProfileImage>,
                    response: Response<ProfileImage>
                ) {
                    when (response.code()) {
                        200 -> {
                            val success: ProfileImage = response.body()!!
                            Log.d("TAG", success.success)
                            Picasso.get().load(success.success).into(Util.profilePicture)
                            Picasso.get().load(success.success).into(profileImage)
                            Util.user!!.profileImage = success.success
                            Util.stopLoading(dialog)

                        }
                        400 -> {
                            Toast.makeText(
                                activity,
                                "Failed. Please retry.",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                }

            })


        }

    }

    override fun onFinish() {
    }

    override fun onProgressUpdate(percentage: Int) {

    }

    override fun onError() {
    }


}