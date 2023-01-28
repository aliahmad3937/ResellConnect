package com.cc.resellconnect.ui.fragments


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cc.resellconnect.adapters.SneakerAdapter
import com.cc.resellconnect.callBacks.SneakerClickListener
import com.cc.resellconnect.databinding.FragmentSneakersBinding
import com.cc.resellconnect.models.SneakerModel
import com.cc.resellconnect.ui.activities.MainActivity
import com.cc.resellconnect.utils.MyApplication
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.models.Group
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class Sneakers : Fragment() ,SneakerClickListener{

    private lateinit var binding: FragmentSneakersBinding
    private lateinit var firestore: FirebaseFirestore
    private var mList: ArrayList<SneakerModel>? = null
    private var mAdapter: SneakerAdapter? = null
    private lateinit var mContext:MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSneakersBinding.inflate(inflater, container, false)
        setUpRecyclerView()
        firestore = FirebaseFirestore.getInstance()



        binding.icChat.setOnClickListener {
            if(CometChat.getLoggedInUser() != null){
                   mContext.startGroupIntent(Group("sneakers-rc","Sneakers",CometChatConstants.GROUP_TYPE_PUBLIC,""))
            }else{
                 if(MyApplication.isGuest){
                     mContext.startGroupIntent(Group("sneakers-rc","Sneakers",CometChatConstants.GROUP_TYPE_PUBLIC,""))
                 }
            }
        }

        binding.clearSearch.setOnClickListener {
            binding.searchBar.setText("")
            mAdapter!!.onFilter(binding.searchBar.text.toString())
            binding.clearSearch.visibility = View.GONE
        }


        binding.searchBar.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                   if(s!!.isNotEmpty()){
                       binding.clearSearch.visibility = View.VISIBLE
                   }else{
                       binding.clearSearch.visibility = View.GONE
                   }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.searchBar.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                  //  mAdapter!!.onFilter(binding.searchBar.text.toString())
                    val str = binding.searchBar.text.toString().replace(" ","")
                    visitPage( "https://stockx.com/search?s=$str")
                    return true
                }
                return false
            }
        })

        firestore
            .collection("Flips")
            .whereEqualTo("isHidden","false")
            .orderBy("id",Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error == null) {
                    Log.v("TAG9","error   null")
                    if (value != null) {
                        Log.v("TAG9","guide")
//                        val list = value.toObjects(SneakerModel::class.java)
                        mList!!.clear()
                        value.forEach {
                            val sneaker = it.toObject(SneakerModel::class.java)
                            sneaker.flipId = it.id
                            mList!!.add(sneaker)
                        }


                       // mList?.addAll(list)
                        mAdapter?.notifyDataSetChanged()
                    }
                }else{
                    Log.v("TAG9","error else:${error.localizedMessage}")
                }
            }





        return binding.root
    }

    private fun setUpRecyclerView() {
        mList = ArrayList()
        mAdapter = SneakerAdapter(mList!!,mList!!, requireContext(), this)
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvSneakers.layoutManager = linearLayoutManager
        binding.rvSneakers.adapter = mAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }

    override fun onSneakerClick(sneakerModel: SneakerModel) {
        // If you can not find the RegistrationDirection try to "Build project"
        val action = ResellConnectDirections.actionResellConnectToSneakerDetailFragment(sneaker = sneakerModel, type = "Flips")

        // this will navigate the current fragment i.e
        // Registration to the Detail fragment
        findNavController().navigate(
            action
        )

       //   Toast.makeText(mContext,"id :${sneakerModel.id}",Toast.LENGTH_SHORT).show()
    }


}

inline fun Fragment.visitPage(s:String){
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(s))
    startActivity(browserIntent)
}