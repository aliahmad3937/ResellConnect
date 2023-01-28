package com.cc.resellconnect.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cc.resellconnect.R
import com.cc.resellconnect.callBacks.SneakerClickListener
import com.cc.resellconnect.databinding.ItemSneakerBinding
import com.cc.resellconnect.models.SneakerModel
import com.cc.resellconnect.models.SneakerNotifModel
import com.cc.resellconnect.utils.MyApplication
import com.cc.resellconnect.utils.SavedPreference
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.CustomMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.smarteist.autoimageslider.SliderView
import org.json.JSONException
import org.json.JSONObject
import java.util.*


/**
 * Created by Ali Ahmad on 9,September,2022
 * CodeCoy,
 * Lahore, Pakistan.
 */
class SneakerAdapter(
    private var mList: List<SneakerModel>,
    private var tempList: List<SneakerModel>,
    val context: Context,
    val sneakerClickListener: SneakerClickListener,

    ) : RecyclerView.Adapter<SneakerViewHolder>() {

   private var notifReference:DocumentReference? = null
    private var uid:String?=null

    init {
        uid = FirebaseAuth.getInstance().uid.toString()
        notifReference = FirebaseFirestore.getInstance()
            .collection("Flip Notifications")
            .document(uid!!)
    }



    val likesList: MutableList<String> =
        SavedPreference.getLikesList(context) as MutableList<String>

    val notifList: MutableList<String> =
        SavedPreference.getNotificationList(context) as MutableList<String>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SneakerViewHolder {
        return SneakerViewHolder(
            ItemSneakerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SneakerViewHolder, position: Int) {
        val model = mList[position]

        holder.mBinding.tvName.text = mList[position].name!!.capitalize()
        holder.mBinding.tvDate.text = mList[position].date
        holder.mBinding.tvRetailPrice.text = mList[position].info
        holder.mBinding.tvResellPrice.text = mList[position].infoSecondary
        holder.mBinding.tvLike.text = mList[position].likes.toString()



        if (model.image != null && model.image!!.isNotEmpty()) {
            if (model.image2 != null && model.image2.isNotEmpty()) {

                holder.mBinding.productImg.visibility = View.GONE
                holder.mBinding.slider.visibility = View.VISIBLE

                if (model.image3 != null && model.image3.isNotEmpty()) {
                    setSlider(
                        holder.mBinding.slider,
                        arrayListOf(
                            mList[position].image.toString(),
                            mList[position].image2.toString(),
                            mList[position].image3.toString()
                        ),
                        mList[position],
                        sneakerClickListener
                    )

                } else {
                    setSlider(
                        holder.mBinding.slider,
                        arrayListOf(
                            mList[position].image.toString(),
                            mList[position].image2.toString()
                        ),
                        mList[position],
                        sneakerClickListener
                    )
                }
            } else {
                holder.mBinding.productImg.visibility = View.VISIBLE
                holder.mBinding.slider.visibility = View.GONE
                Glide.with(context).load(mList[position].image).into(holder.mBinding.productImg)
            }
        }


        if (likesList.contains(mList[position].flipId)) {
            holder.mBinding.like.setImageDrawable(context.getDrawable(R.drawable.ic_likes))
        } else {
            holder.mBinding.like.setImageDrawable(context.getDrawable(R.drawable.ic_like))
        }


        if (notifList.contains(mList[position].flipId)) {
            holder.mBinding.notify.setImageDrawable(context.getDrawable(R.drawable.ic_notify_fill))
        } else {
            holder.mBinding.notify.setImageDrawable(context.getDrawable(R.drawable.ic_notify))
        }


        holder.mBinding.notify.setOnClickListener {
            if (notifList.contains(mList[position].flipId)) {
                notifList.remove(mList[position].flipId.toString())
                holder.mBinding.notify.setImageDrawable(context.getDrawable(R.drawable.ic_notify))
                notifReference!!
                    .collection("flip")
                    .document(mList[position].name.toString())
                    .delete()

                SavedPreference.saveNotificationList(context, notifList)
            } else {
                notifList.add(mList[position].flipId.toString())
                holder.mBinding.notify.setImageDrawable(context.getDrawable(R.drawable.ic_notify_fill))

                notifReference!!.set(mutableMapOf("uid" to uid!!))

                notifReference!!
                    .collection("flip")
                    .document(mList[position].name.toString())
                    .set(SneakerNotifModel(
                        mList[position].id!!,
                        mList[position].name,
                        uid!!
                    ))
                SavedPreference.saveNotificationList(context, notifList)
            }
        }

        holder.mBinding.like.setOnClickListener {
            if (likesList.contains(mList[position].flipId)) {
                likesList.remove(mList[position].flipId.toString())
                holder.mBinding.like.setImageDrawable(context.getDrawable(R.drawable.ic_like))
                mList[position].likes = mList[position].likes!!.minus(1)
                holder.mBinding.tvLike.text = mList[position].likes.toString()
                FirebaseFirestore.getInstance()
                    .collection("Flips")
                    .document(mList[position].flipId.toString())
                    .update("likes", mList[position].likes)

                SavedPreference.saveLikesList(context, likesList)
            } else {
                likesList.add(mList[position].flipId.toString())
                holder.mBinding.like.setImageDrawable(context.getDrawable(R.drawable.ic_likes))
                mList[position].likes = mList[position].likes!!.plus(1)
                holder.mBinding.tvLike.text = mList[position].likes.toString()
                FirebaseFirestore.getInstance()
                    .collection("Flips")
                    .document(mList[position].flipId.toString())
                    .update("likes", mList[position].likes)
                SavedPreference.saveLikesList(context, likesList)
            }


        }


        holder.itemView.setOnClickListener {
            sneakerClickListener.onSneakerClick(mList[position])
        }
    }

    private fun sendNotification() {
        var receiverId: String = "user166307205359"
        var metaData: JSONObject = JSONObject()
        var customData: JSONObject = JSONObject()

        try {
            metaData.put("pushNotification", "Custom Notification Body")
            customData.put("data", "Your Value")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        var customMessage =
            CustomMessage(receiverId, CometChatConstants.RECEIVER_TYPE_USER, "ali", customData)
        customMessage.metadata = metaData;

        CometChat.sendCustomMessage(
            customMessage,
            object : CometChat.CallbackListener<CustomMessage>() {
                override fun onSuccess(p0: CustomMessage?) {
                    Log.d("TAG7", "onSuccess ${p0?.toString()}")
                }

                override fun onError(p0: CometChatException?) {
                    Log.d("TAG7", "onError ${p0?.message}")
                }
            })
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun onFilter(query: String?) {
        val charSequenceString: String = query.toString()
        mList = java.util.ArrayList()
        if (query.equals("")) {
            mList = tempList
        } else {
            for (model in tempList) {
                if (model.name!!.lowercase()
                        .contains(charSequenceString.lowercase(Locale.getDefault()))
                ) {
                    (mList as java.util.ArrayList<SneakerModel>).add(model)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun setSlider(
        sliderView: SliderView, list: ArrayList<String>,
        model: SneakerModel, sneakerClickListener: SneakerClickListener
    ) {


        // passing this array list inside our adapter class.

        // passing this array list inside our adapter class.
        val adapter = SliderAdapter(context, list, model, sneakerClickListener)

        // below method is used to set auto cycle direction in left to
        // right direction you can change according to requirement.

        // below method is used to set auto cycle direction in left to
        // right direction you can change according to requirement.
        sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR

        // below method is used to
        // setadapter to sliderview.

        // below method is used to
        // setadapter to sliderview.
        sliderView.setSliderAdapter(adapter)

        // below method is use to set
        // scroll time in seconds.

        // below method is use to set
        // scroll time in seconds.
        sliderView.scrollTimeInSec = 2

        // to set it scrollable automatically
        // we use below method.

        // to set it scrollable automatically
        // we use below method.
        sliderView.isAutoCycle = false

        // to start autocycle below method is used.

        // to start autocycle below method is used.
        // sliderView.startAutoCycle()
    }


}

class SneakerViewHolder(val mBinding: ItemSneakerBinding) : RecyclerView.ViewHolder(mBinding.root)