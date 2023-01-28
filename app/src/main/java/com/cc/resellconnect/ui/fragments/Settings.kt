package com.cc.resellconnect.ui.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cc.resellconnect.R
import com.cc.resellconnect.databinding.FragmentSettingsBinding
import com.cc.resellconnect.ui.activities.LogInActivity
import com.cc.resellconnect.ui.activities.MainActivity
import com.cc.resellconnect.ui.activities.SubscriptionActivity
import com.cc.resellconnect.utils.BottomSheetSignIn
import com.cc.resellconnect.utils.MyApplication
import com.cc.resellconnect.utils.Utils
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants
import com.cometchat.pro.uikit.ui_resources.utils.ErrorMessagesUtils
import com.github.ramiz.nameinitialscircleimageview.NameInitialsCircleImageView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kaopiz.kprogresshud.KProgressHUD
import gun0912.tedimagepicker.builder.TedImagePicker


class Settings : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private lateinit var activity: MainActivity
    private val TAG = "SettingsFragment"
    private lateinit var uri: Uri

    val dbReference: DocumentReference by lazy {
        FirebaseFirestore.getInstance().collection("Users")
            .document(FirebaseAuth.getInstance().uid.toString())
    }
    private var progressHUD: KProgressHUD? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        progressHUD = Utils.getProgressDialog(requireContext(), getString(R.string.loading))
        val notifLay = binding.layNotif
        val btnAllow = notifLay.findViewById<TextView>(R.id.tv_allow)
        val btnNotAllow = notifLay.findViewById<TextView>(R.id.tv_not_allow)



        binding.profileImage.setOnClickListener {
            if (CometChat.getLoggedInUser() != null) {
                if (!MyApplication.isGuest) {
                    TedImagePicker.with(activity)
                        .start { uri ->
                            this.uri = uri
                            uploadImage()
                            val imageInfo: NameInitialsCircleImageView.ImageInfo =
                                NameInitialsCircleImageView.ImageInfo
                                    .Builder("RR")
                                    .setTextColor(android.R.color.white)
                                    .setImageUrl(uri.toString())
                                    .setCircleBackgroundColorRes(android.R.color.holo_orange_dark)
                                    .build()
                            binding.profileImage.setImageInfo(imageInfo)
                        }
                }
            } else {
                if (MyApplication.isFree || MyApplication.isGuest) {
                    Snackbar.make(
                        binding.icSetting,
                        "You need to Create Paid Account!",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    Snackbar.make(
                        binding.icSetting,
                        "You need to Create your CometChat Account!",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }

        notifLay.setOnClickListener {
            notifLay.visibility = View.GONE
        }

        btnAllow.setOnClickListener {
            notifLay.visibility = View.GONE
        }

        btnNotAllow.setOnClickListener {
            notifLay.visibility = View.GONE
        }

        binding.tvNotif.setOnClickListener {
            notifLay.visibility = View.VISIBLE
        }
        binding.tvTermoofuse.setOnClickListener {
//            val browserIntent = Intent(
//                Intent.ACTION_VIEW,
//                Uri.parse("https://resellconnect.com/terms-and-conditions")
//            )
//            startActivity(browserIntent)
            openWebView()
            binding.webView2.visibility = View.GONE
            binding.webView1.visibility = View.VISIBLE
        }
        binding.tvPrivacypolicy.setOnClickListener {
//            val browserIntent =
//                Intent(Intent.ACTION_VIEW, Uri.parse("https://resellconnect.com/privacy-policy"))
//            startActivity(browserIntent)
            openWebView()
            binding.webView1.visibility = View.GONE
            binding.webView2.visibility = View.VISIBLE
        }
        binding.tvFaq.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://resellconnect.com/faq"))
            startActivity(browserIntent)
        }
        binding.tvContactus.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://resellconnect.com/contact"))
            startActivity(browserIntent)
        }


        dbReference
            .get()
            .addOnSuccessListener {
                if (it != null && it.exists()) {
                    if (!MyApplication.isGuest) {
                        binding.tvName.text = it.get("name").toString()
                        binding.tvEmail.text = it.get("email").toString()
                        var url = ""
                        if (it.contains("imageUrl")) {
                            url = it.get("imageUrl").toString()
                        }

                        val imageInfo: NameInitialsCircleImageView.ImageInfo =
                            NameInitialsCircleImageView.ImageInfo
                                .Builder(it.get("name").toString().get(0).toString())
                                .setTextColor(android.R.color.white)
                                .setImageUrl(url)
                                .setCircleBackgroundColorRes(android.R.color.black)
                                .build()
                        binding.profileImage.setImageInfo(imageInfo)
                    }
                }
            }


        val user = CometChat.getLoggedInUser()

//        Log.v("TAG9", "CometChatUser name:${user.name}   email:${user.toString()}")

        if (MyApplication.isGuest) {
            binding.profileImage2.visibility = View.VISIBLE
            binding.profileImage.visibility = View.GONE
        } else {
            binding.profileImage.visibility = View.VISIBLE
            binding.profileImage2.visibility = View.GONE
            binding.tvUname.text = user?.name
        }

        binding.tvLogout.setOnClickListener {
            if (MyApplication.isGuest) {
                BottomSheetSignIn().show(
                    requireContext(),
                    R.layout.bottom_sheet_signin
                ) { email, pass ->
                    progressHUD?.show()
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                progressHUD?.dismiss()
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        SubscriptionActivity::class.java
                                    )
                                )
                                requireActivity().finishAffinity()
                            } else {
                                Utils.showToast(requireContext(), "Fail")
                                progressHUD?.dismiss()
                            }
                        }
                }
            } else {
                logout()
            }

        }

        if (MyApplication.isGuest) {
            binding.tvLogout.text = "Login"
        } else {
            binding.tvLogout.text = "Log Out"
        }


        binding.icSetting.setOnClickListener {
            if (!MyApplication.isGuest) {

                if (CometChat.getLoggedInUser() != null) {

                    // on below line we are creating a new bottom sheet dialog.
                    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)

                    // on below line we are inflating a layout file which we have created.
                    val view = layoutInflater.inflate(R.layout.bottom_sheet_channels, null)

                    // on below line we are creating a variable for our button
                    // which we are using to dismiss our dialog.
                    val btnSave = view.findViewById<MaterialButton>(R.id.btn_save)
                    val uname = view.findViewById<EditText>(R.id.et_username)

                    // on below line we are adding on click listener
                    // for our dismissing the dialog button.
                    btnSave.setOnClickListener {
                        Log.v(TAG, "btn click")
                        // on below line we are calling a dismiss
                        // method to close our dialog.
                        if (uname.text.toString().isNotEmpty()) {
                            Log.v(TAG, "not empty")
                            val user: User = User()
                            user.name = uname.text.toString()
                            user.uid = CometChat.getLoggedInUser().uid
                            user.avatar = CometChat.getLoggedInUser().avatar
                            updateUser(user)
                            dialog.dismiss()
                        } else {
                            uname.error =
                                getString(com.cometchat.pro.uikit.R.string.fill_this_field)
                            Log.v(TAG, "empty")
                        }


                    }
                    // below line is use to set cancelable to avoid
                    // closing of dialog box when clicking on the screen.
                    dialog.setCancelable(true)

                    // on below line we are setting
                    // content view to our view.
                    dialog.setContentView(view)
                    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

                    // on below line we are calling
                    // a show method to display a dialog.
                    dialog.show()
                } else {
//                        Snackbar.make(
//                            binding.icSetting,
//                            "You need to log in to CometChat!",
//                            Snackbar.LENGTH_LONG
//                        ).show();
                }

            }
        }

        binding.back.setOnClickListener {
            binding.layoutSetting.visibility = View.VISIBLE
            binding.webView1.visibility = View.GONE
            binding.webView2.visibility = View.GONE
            binding.back.visibility = View.GONE

        }


        binding.webView1.settings.javaScriptEnabled = true
        binding.webView1.loadUrl("https://resellconnect.com/terms-and-conditions")
        binding.webView2.settings.javaScriptEnabled = true
        binding.webView2.loadUrl("https://resellconnect.com/privacy-policy")

        return binding.root
    }

    private fun openWebView() {
        binding.layoutSetting.visibility = View.GONE
        binding.back.visibility = View.VISIBLE
    }

    // UploadImage method
    private fun uploadImage() {
        if (uri != null) {

            // Code for showing progressDialog while uploading
            val progressDialog = ProgressDialog(activity)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            // Defining the child of storageReference
            val ref: StorageReference = FirebaseStorage.getInstance().reference.child("Users")
                .child(FirebaseAuth.getInstance().uid.toString())

            // adding listeners on upload
            // or failure of image
            ref.putFile(uri)
                .addOnSuccessListener { // Image uploaded successfully
                    // Dismiss dialog
                    progressDialog.dismiss()
                    ref.downloadUrl.addOnSuccessListener {
                        val userMap = mapOf(
                            "imageUrl" to it.toString()
                        )
                        activity.updateUserCometChatProfile(it.toString())

                        dbReference
                            .set(userMap, SetOptions.merge())
                            .addOnSuccessListener {
                                Toast
                                    .makeText(
                                        activity,
                                        "Image Uploaded!!",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }
                            .addOnFailureListener {

                            }
                    }

                }
                .addOnFailureListener {

                    // Error, Image not uploaded
                    progressDialog.dismiss()
                    Toast
                        .makeText(
                            activity,
                            "Failed " + it.message,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = ((100.0
                            * taskSnapshot.bytesTransferred
                            / taskSnapshot.totalByteCount))
                    progressDialog.setMessage(
                        ("Uploaded " + progress.toInt() + "%")
                    )
                }

        }
    }


    private fun updateUser(user: User) {
        val authkey = UIKitConstants.AppInfo.AUTH_KEY
        CometChat.updateUser(user, authkey, object : CometChat.CallbackListener<User?>() {
            override fun onSuccess(user: User?) {
                if (context != null) Toast.makeText(
                    context,
                    "Updated User Successfully",
                    Toast.LENGTH_LONG
                ).show()
                binding.tvUname.text = user!!.name.toString()
            }

            override fun onError(e: CometChatException) {
                if (context != null)
                    ErrorMessagesUtils.cometChatErrorMessage(context, e.code)
//                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun logout() {
        AlertDialog.Builder(activity)
            .setTitle("Logout!")
            .setMessage("Do you want to Exit from app?") // Specifying a listener allows you to take an action before dismissing the dialog.
            // The dialog is automatically dismissed when a dialog button is clicked.
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, which ->
                    CometChat.logout(object : CometChat.CallbackListener<String?>() {
                        override fun onSuccess(successMessage: String?) {
                            Log.d("TAG", "Logout completed successfully")
                            FirebaseAuth.getInstance().signOut()
                            MyApplication.isGuest = false
                            MyApplication.isFree = false
                            val intent = Intent(activity, LogInActivity::class.java)
                            startActivity(intent)
                            activity.finishAffinity()
                        }

                        override fun onError(e: CometChatException) {
                            Log.d("TAG", "Logout failed with exception: " + e.message)
                        }
                    })
                }) // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }


}