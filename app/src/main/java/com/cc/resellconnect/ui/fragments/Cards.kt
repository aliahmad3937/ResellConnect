package com.cc.resellconnect.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cc.resellconnect.R
import com.cc.resellconnect.adapters.CardsAdapter
import com.cc.resellconnect.callBacks.SneakerClickListener
import com.cc.resellconnect.databinding.FragmentCardsBinding
import com.cc.resellconnect.models.SneakerModel
import com.cc.resellconnect.ui.activities.MainActivity
import com.cc.resellconnect.ui.activities.SubscriptionActivity
import com.cc.resellconnect.utils.MyApplication
import com.cc.resellconnect.utils.BottomSheetSignIn
import com.cc.resellconnect.utils.BottomSheetUpgradeMembership
import com.cc.resellconnect.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kaopiz.kprogresshud.KProgressHUD

class Cards : Fragment(), SneakerClickListener {

    private lateinit var binding: FragmentCardsBinding
    private lateinit var firestore: FirebaseFirestore
    private var mList: ArrayList<SneakerModel>? = null
    private var mAdapter: CardsAdapter? = null
    private lateinit var mContext: MainActivity

    private var progressHUD: KProgressHUD? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCardsBinding.inflate(inflater, container, false)
        progressHUD = Utils.getProgressDialog(requireContext(), getString(R.string.loading))


        setUpRecyclerView()
        firestore = FirebaseFirestore.getInstance()



        binding.clearSearch.setOnClickListener {
            binding.searchBar.setText("")
            mAdapter!!.onFilter(binding.searchBar.text.toString())
            binding.clearSearch.visibility = View.GONE
        }


        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isNotEmpty()) {
                    binding.clearSearch.visibility = View.VISIBLE
                } else {
                    binding.clearSearch.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.searchBar.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(
                v: TextView?,
                actionId: Int,
                event: KeyEvent?
            ): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //    mAdapter!!.onFilter(binding.searchBar.text.toString())
                    val str = binding.searchBar.text.toString().replace(" ", "+")
                    visitPage("https://www.ebay.co.uk/sch/i.html?_from=R40&_trksid=p2334524.m570.l1313&_nkw=\\$str&_sacat=0&LH_TitleDesc=0&rt=nc&_odkw=this&_osacat=0&LH_Complete=1&LH_Sold=1")
                    return true
                }
                return false
            }
        })



        firestore.collection("Flips 3")
            .whereEqualTo("isHidden", "false")
            .orderBy("id", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                if (error == null) {
                    Log.v("TAG9", "error   null")
                    if (value != null) {
                        Log.v("TAG9", "guide")
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
                } else {
                    Log.v("TAG9", "error else :${error.localizedMessage}")
                }
            }



        return binding.root
    }

    private fun setUpRecyclerView() {
        mList = ArrayList()
        mAdapter = CardsAdapter(mList!!, mList!!, requireContext(), this)
        val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.rvCard.layoutManager = linearLayoutManager
        binding.rvCard.adapter = mAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }


    override fun onSneakerClick(sneakerModel: SneakerModel) {
        if (!MyApplication.isGuest) {
            if (!MyApplication.isFree) {
                // If you can not find the RegistrationDirection try to "Build project"
                val action = ResellConnectDirections.actionResellConnectToSneakerDetailFragment(
                    sneaker = sneakerModel,
                    type = "Flips 3"
                )

                // this will navigate the current fragment i.e
                // Registration to the Detail fragment
                findNavController().navigate(
                    action
                )
            } else {
                BottomSheetUpgradeMembership().show(
                    requireContext(),
                    R.layout.bottom_sheet_upgrade_membership
                )
            }
        } else {
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
        }
        //   Toast.makeText(mContext,"id :${sneakerModel.flipId}",Toast.LENGTH_SHORT).show()
    }


}