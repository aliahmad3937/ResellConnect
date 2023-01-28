package com.cc.resellconnect.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cc.resellconnect.R
import com.cc.resellconnect.adapters.LinksAdapter
import com.cc.resellconnect.adapters.SliderAdapter
import com.cc.resellconnect.callBacks.SneakerClickListener
import com.cc.resellconnect.databinding.FragmentSneakerDetailBinding
import com.cc.resellconnect.models.LinkModel
import com.cc.resellconnect.models.SneakerModel
import com.cc.resellconnect.models.SneakerNotifModel
import com.cc.resellconnect.ui.activities.SubscriptionActivity
import com.cc.resellconnect.utils.BottomSheetSignIn
import com.cc.resellconnect.utils.MyApplication
import com.cc.resellconnect.utils.SavedPreference
import com.cc.resellconnect.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.kaopiz.kprogresshud.KProgressHUD
import com.smarteist.autoimageslider.SliderView


class SneakerDetailFragment : Fragment(), SneakerClickListener {


    // get the arguments from the Registration fragment
    private val args: SneakerDetailFragmentArgs by navArgs()
    private lateinit var mBinding: FragmentSneakerDetailBinding
    private val TAG = "SneakerDetailFragment"

    private lateinit var adapter: LinksAdapter
    private var mList: ArrayList<LinkModel>? = null
    private lateinit var recyclerView: RecyclerView

    private lateinit var dialog: BottomSheetDialog

    private lateinit var animation: Animation

    private var notifReference: DocumentReference? = null
    private var uid:String?=null
    private var progressHUD: KProgressHUD? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentSneakerDetailBinding.inflate(inflater, container, false)
        progressHUD = Utils.getProgressDialog(requireContext(), getString(R.string.loading))

        mBinding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        if (MyApplication.isGuest) {
         mBinding.notify.visibility= View.GONE
            mBinding.like.visibility = View.GONE
        } else {
            mBinding.notify.visibility = View.VISIBLE
            mBinding.like.visibility = View.VISIBLE
        }

            // loading Animation from
            animation = AnimationUtils.loadAnimation(requireContext(), R.anim.bounce)
            mBinding.btnBuy.startAnimation(animation)
            mBinding.btnBuy.startAnimation(animation)

            updateView()
            showBottomSheetDialog(R.layout.bottom_sheet_sneakers)

            FirebaseFirestore.getInstance()
                .collection(args.type)
                .document(args.sneaker.flipId.toString())
                .collection("links")
                .addSnapshotListener { value, error ->
                    Log.v("TAG4", "list :")
                    if (error == null) {
                        //    Log.v("TAG4","list : error ")
                        if (!value!!.isEmpty) {
                            mList?.clear()
                            val list = value!!.toObjects(LinkModel::class.java)
                            mList!!.addAll(list)
                            Log.v("TAG4", "list size :${mList!!.size}")
                            adapter.notifyDataSetChanged()
                        } else {
                            mList?.clear()
                            adapter.notifyDataSetChanged()
                            Log.v("TAG4", "list :empty")
                        }
                    } else {
                        Log.v("TAG4", "list error :${error.localizedMessage}")
                        mList?.clear()
                        adapter.notifyDataSetChanged()
                    }
                }




            mBinding.btnLowBid.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(args.sneaker.viewOfferURL))
                startActivity(browserIntent)
            }

            mBinding.btnHighBid.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(args.sneaker.viewOfferURL))
                startActivity(browserIntent)
            }




            mBinding.btnBuy.setOnClickListener {
                //  findNavController().popBackStack()
                // openBottomSheet()
                if(MyApplication.isGuest){
                    BottomSheetSignIn().show(requireContext(),R.layout.bottom_sheet_signin){ email, pass ->
                        progressHUD?.show()
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener{
                                if(it.isSuccessful){
                                    progressHUD?.dismiss()
                                    startActivity(
                                        Intent(requireContext(),
                                            SubscriptionActivity::class.java)
                                    )
                                    requireActivity().finishAffinity()
                                }else{
                                    Utils.showToast(requireContext(), "Fail")
                                    progressHUD?.dismiss()
                                }
                            }
                    }
                }else{
                    mBinding.btnBuy.startAnimation(animation)
                    dialog.show()
                }


            }

        if(MyApplication.isGuest){
            mBinding.btnBuy.setBackgroundColor(requireContext().getColor(R.color.grey))
        }else{
            mBinding.btnBuy.setBackgroundColor(requireContext().getColor(R.color.black))
        }



        return mBinding.root
    }

    private fun setUpRecyclerView() {
        mList = ArrayList()
        adapter = LinksAdapter(mList!!, requireContext())
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
    }


    private fun showBottomSheetDialog(
        @LayoutRes layout: Int,
        fullScreen: Boolean = true,
        expand: Boolean = true
    ) {
        dialog = BottomSheetDialog(requireContext())
        dialog.setOnShowListener {
            val bottomSheet: FrameLayout =
                dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
                    ?: return@setOnShowListener
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            if (fullScreen && bottomSheet.layoutParams != null) {
                showFullScreenBottomSheet(bottomSheet)
            }

            if (!expand) return@setOnShowListener

            bottomSheet.setBackgroundResource(android.R.color.transparent)
            expandBottomSheet(bottomSheetBehavior)
        }

        @SuppressLint("InflateParams") // dialog does not need a root view here
        val sheetView = layoutInflater.inflate(layout, null)
        recyclerView = sheetView.findViewById<RecyclerView>(R.id.rv_links)
        setUpRecyclerView()

        dialog.setContentView(sheetView)

    }

    private fun showFullScreenBottomSheet(bottomSheet: FrameLayout) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = Resources.getSystem().displayMetrics.heightPixels - 100
        bottomSheet.layoutParams = layoutParams
    }

    private fun expandBottomSheet(bottomSheetBehavior: BottomSheetBehavior<FrameLayout>) {
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun updateView() {
        val likesList: MutableList<String> =
            SavedPreference.getLikesList(requireContext()) as MutableList<String>
        val notifList: MutableList<String> =
            SavedPreference.getNotificationList(requireContext()) as MutableList<String>


        val model = args.sneaker
        mBinding.tvName.text = model.name
        if (args.type == "Flips") {
            mBinding.btnLowBid.text = model.lowBid + "\nLowest Ask"
            mBinding.btnHighBid.text = model.highBid + "\nHighest Bid"
            mBinding.btnLowBid.setBackgroundColor(requireContext().getColor(R.color.flip))
            mBinding.btnHighBid.visibility = View.VISIBLE
            mBinding.tvStockx.visibility = View.VISIBLE
            mBinding.tvEbay.visibility = View.GONE

        } else {
            mBinding.btnLowBid.text = "View Resell"
            mBinding.btnLowBid.setBackgroundColor(requireContext().getColor(R.color.flip2))
            mBinding.btnHighBid.visibility = View.GONE
            mBinding.tvStockx.visibility = View.GONE
            mBinding.tvEbay.visibility = View.VISIBLE

        }


        mBinding.tvName.text = model.name
        mBinding.tvDes.text = model.viewOfferInfo
        mBinding.tvRetailPrice.text = model.info
        mBinding.tvResellPrice.text = model.infoSecondary
        mBinding.tvLike.text = model.likes.toString()

        if (model.image != null && model.image!!.isNotEmpty()) {
            if (model.image2 != null && model.image2.isNotEmpty()) {

                mBinding.productImg.visibility = View.GONE
                mBinding.slider.visibility = View.VISIBLE

                if (model.image3 != null && model.image3.isNotEmpty()) {
                    setSlider(
                        mBinding.slider,
                        arrayListOf(
                            model.image.toString(),
                            model.image2.toString(),
                            model.image3.toString()
                        ),
                        model,
                        this
                    )

                } else {
                    setSlider(
                        mBinding.slider,
                        arrayListOf(
                            model.image.toString(),
                            model.image2.toString()
                        ), model,
                        this
                    )
                }
            } else {
                mBinding.productImg.visibility = View.VISIBLE
                mBinding.slider.visibility = View.GONE
                Glide.with(requireContext()).load(model.image).into(mBinding.productImg)
            }
        }


        if (likesList.contains(model.flipId)) {
            mBinding.like.setImageDrawable(requireContext().getDrawable(R.drawable.ic_likes))
        } else {
            mBinding.like.setImageDrawable(requireContext().getDrawable(R.drawable.ic_like))
        }

        mBinding.like.setOnClickListener {
            if (likesList.contains(model.flipId)) {
                likesList.remove(model.flipId.toString())
                mBinding.like.setImageDrawable(requireContext().getDrawable(R.drawable.ic_like))
                model.likes = model.likes!!.minus(1)
                mBinding.tvLike.text = model.likes.toString()
                FirebaseFirestore.getInstance()
                    .collection(args.type)
                    .document(model.flipId.toString())
                    .update("likes", model.likes)

                SavedPreference.saveLikesList(requireContext(), likesList)
            } else {
                likesList.add(model.flipId.toString())
                mBinding.like.setImageDrawable(requireContext().getDrawable(R.drawable.ic_likes))
                model.likes = model.likes!!.plus(1)
                mBinding.tvLike.text = model.likes.toString()
                FirebaseFirestore.getInstance()
                    .collection(args.type)
                    .document(model.flipId.toString())
                    .update("likes", model.likes)
                SavedPreference.saveLikesList(requireContext(), likesList)
            }


        }

        if (notifList.contains(model.flipId)) {
            mBinding.notify.setImageDrawable(requireContext().getDrawable(R.drawable.ic_notify_fill))
        } else {
            mBinding.notify.setImageDrawable(requireContext().getDrawable(R.drawable.ic_notify))
        }
        uid = FirebaseAuth.getInstance().uid.toString()
        notifReference = FirebaseFirestore.getInstance()
            .collection("Flip Notifications")
            .document(uid!!)
        mBinding.notify.setOnClickListener {
            if (notifList.contains(model.flipId)) {
                notifList.remove(model.flipId.toString())
                mBinding.notify.setImageDrawable(requireContext().getDrawable(R.drawable.ic_notify))
                notifReference!!
                    .collection("flip 2")
                    .document(model.name.toString())
                    .delete()

                SavedPreference.saveNotificationList(requireContext(), notifList)
            } else {
                notifList.add(model.flipId.toString())
                mBinding.notify.setImageDrawable(requireContext().getDrawable(R.drawable.ic_notify_fill))

                notifReference!!.set(mutableMapOf("uid" to uid!!))

                notifReference!!
                    .collection("flip 2")
                    .document(model.name.toString())
                    .set(
                        SneakerNotifModel(
                            model.id!!,
                            model.name,
                            uid!!
                        )
                    )
                SavedPreference.saveNotificationList(requireContext(), notifList)
            }
        }



    }

    fun setSlider(
        sliderView: SliderView, list: ArrayList<String>,
        model: SneakerModel, sneakerClickListener: SneakerClickListener
    ) {


        // passing this array list inside our adapter class.

        // passing this array list inside our adapter class.
        val adapter = SliderAdapter(requireContext(), list, model, sneakerClickListener)

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

    override fun onSneakerClick(sneakerModel: SneakerModel) {

    }


}