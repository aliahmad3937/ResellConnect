package com.cc.resellconnect.ui.activities

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.cc.resellconnect.R
import com.cc.resellconnect.utils.MyApplication
import com.cc.resellconnect.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kaopiz.kprogresshud.KProgressHUD

class SubscriptionActivity : AppCompatActivity() {

    private var progressHUD: KProgressHUD? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWhiteStatusBarColor()
        setContentView(R.layout.activity_subscription)

        progressHUD = Utils.getProgressDialog(this, getString(R.string.loading))

        progressHUD?.show()
        FirebaseFirestore.getInstance().collection("customers")
            .document(FirebaseAuth.getInstance().uid.toString())
            .collection("subscriptions")
            .get()
            .addOnSuccessListener {
                if(it != null && !it.isEmpty){
                    val obj = it.documents[0]
                    if(obj.get("status").toString() == "active"  || obj.get("status").toString() == "trialing"){

                        progressHUD?.dismiss()
                        val obj2 = obj.get("items") as ArrayList<Map<String,Map<String,Map<String,Any>>>>
                        val tier = obj2.get(0).get("plan")?.get("metadata")?.get("tier").toString()
                         Log.v("TAG78","${obj2.get(0).get("plan")?.get("metadata")?.get("tier").toString()}")
                        if(tier == "free"){
                            MyApplication.isFree = true
                             findViewById<ImageView>(R.id.lock).visibility = View.VISIBLE
                            startActivity(Intent(this,MainActivity::class.java).putExtra("tier",tier))
                            finishAffinity()
                        }else{
                            MyApplication.isFree = false
                            findViewById<ImageView>(R.id.lock).visibility = View.GONE
                             startActivity(Intent(this,MainActivity::class.java).putExtra("tier",tier))
                             finishAffinity()

                        }
                        MyApplication.isGuest = false
                    }else{
                        progressHUD?.dismiss()
                     Toast.makeText(this,"Subscription Cancel",Toast.LENGTH_LONG).show()
                    }
                }else{
                    progressHUD?.dismiss()
                }
            }
            .addOnFailureListener{
                progressHUD?.dismiss()
                Toast.makeText(this,"Error: ${it.localizedMessage}",Toast.LENGTH_LONG).show()
            }








    }

    private fun setWhiteStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }
    }
}