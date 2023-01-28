package com.cc.resellconnect.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.cc.resellconnect.R
import com.cc.resellconnect.adapters.GuidesAdapter
import com.cc.resellconnect.callBacks.GuideClickListener
import com.cc.resellconnect.databinding.FragmentGuidesBinding
import com.cc.resellconnect.models.GuideModel
import com.cc.resellconnect.ui.activities.CometChatRules
import com.cc.resellconnect.ui.activities.MainActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class Guides : Fragment(), GuideClickListener {


    private lateinit var binding: FragmentGuidesBinding
    private lateinit var firestore: FirebaseFirestore
    private var mList: ArrayList<GuideModel>? = null
    private var mAdapter: GuidesAdapter? = null

    private lateinit var mContext:MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGuidesBinding.inflate(inflater, container, false)
        setUpRecyclerView()
        firestore = FirebaseFirestore.getInstance()


        firestore
            .collection("Guides")
            .orderBy("id", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener{ value ->
                mList?.clear()

                         for (i in value){
                             Log.v("TAG9","value :${i.get("isFree")}")
                             if(i.get("name").toString() != "Hot Tub Reselling Guide") {
                                 mList?.add(
                                     GuideModel(
                                         i.get("date").toString(),
                                         i.get("description").toString(),
                                         i.get("id") as Long,
                                         i.get("isFree").toString(),
                                         i.get("isHidden").toString(),
                                         i.get("name").toString(),
                                         i.get("pdf").toString()
                                     )
                                 )
                             }
                         }

//                        val list = value.toObjects(GuideModel::class.java)
//                        mList?.addAll(list)
                        mAdapter?.notifyDataSetChanged()


            }





        return binding.root
    }

    private fun setUpRecyclerView() {
        mList = ArrayList()
        mAdapter = GuidesAdapter(mList!!, requireContext(), this)
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvGuides.layoutManager = layoutManager
        binding.rvGuides.adapter = mAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }

    override fun onPause() {
        binding.rvGuides.visibility = View.INVISIBLE
        super.onPause()
    }

    override fun onResume() {
        binding.rvGuides.visibility = View.VISIBLE
        super.onResume()
    }


    override fun onGuideClick(guideModel: GuideModel) {
        startActivity(
            Intent(mContext, CometChatRules::class.java)
            .putExtra("title" ,guideModel.name)
            .putExtra("url",guideModel.pdf)
        )
      //  mContext.overridePendingTransition( R.anim.animate_slides_up, R.anim.animate_slides_down)
        mContext.overridePendingTransition( R.anim.slide_in_up ,  R.anim.slide_out_up)

      //  Toast.makeText(requireContext(), "click", Toast.LENGTH_SHORT).show()
    }
}