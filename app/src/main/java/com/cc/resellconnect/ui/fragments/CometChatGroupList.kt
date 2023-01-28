package com.cc.resellconnect.ui.fragments


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.cc.resellconnect.R
import com.cc.resellconnect.adapters.RecyclerTouchListener
import com.cc.resellconnect.adapters.SwipeToDeleteCallback
import com.cc.resellconnect.adapters.cometchatGroups.CometChatGroups
import com.cc.resellconnect.adapters.cometchatGroups.CometChatGroupsAdapter
import com.cc.resellconnect.ui.activities.MainActivity
import com.cc.resellconnect.utils.MyApplication
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat.CallbackListener
import com.cometchat.pro.core.GroupsRequest
import com.cometchat.pro.core.GroupsRequest.GroupsRequestBuilder
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Group
import com.cometchat.pro.uikit.ui_resources.utils.FontUtils
import com.cometchat.pro.uikit.ui_resources.utils.Utils
import com.cometchat.pro.uikit.ui_resources.utils.item_clickListener.OnItemClickListener
import com.cometchat.pro.uikit.ui_settings.FeatureRestriction
import com.cometchat.pro.uikit.ui_settings.UIKitSettings
import com.cometchat.pro.uikit.ui_settings.enum.GroupMode
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import java.util.*


class CometChatGroupList : Fragment() {
    private var isCreateGroupVisible: Boolean = true
    private var isTitleVisible: Boolean = true
    private var rvGroups //Uses to display list of groups.
           : CometChatGroups? = null
  private var cometChatGroupsAdapter: CometChatGroupsAdapter? = null
    private var groupsRequest //Uses to fetch Groups.
            : GroupsRequest? = null
    private var etSearch //Uses to perform search operations on groups.
            : EditText? = null
    private var clearSearch: ImageView? = null
    private var ivCreateGroup: ImageView? = null
    private var noGroupLayout: LinearLayout? = null
    private val groupList: MutableList<Group> = ArrayList()
    private var conversationShimmer: ShimmerFrameLayout? = null
    private var tvTitle: TextView? = null
    private lateinit var mContext: MainActivity

    private lateinit var layoutChannel: ConstraintLayout
    private lateinit var layoutDm: ConstraintLayout
    private lateinit var tvChannel: TextView
    private lateinit var tvDm: TextView
    private lateinit var v_channel: View
    private lateinit var v_dm: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_cometchat_group_list, container, false)
        MyApplication.cometChatGroupList = this

        rvGroups = view.findViewById(R.id.rv_group_list)


       val touchListener = RecyclerTouchListener(requireActivity(), rvGroups)
        touchListener
            .setClickable(object : RecyclerTouchListener.OnRowClickListener {
                override fun onRowClicked(position: Int) {
//                    Toast.makeText(
//                       requireContext(),
//                       "click",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }

                override fun onIndependentViewClicked(independentViewID: Int, position: Int) {}
            })
            .setSwipeOptionViews(R.id.edit_task)
            .setSwipeable(
                R.id.rowFG,
                R.id.rowBG
            ) { viewID, position ->
                when (viewID) {
                    R.id.edit_task -> {
                        rvGroups!!.cometChatGroupsViewModel!!.cometChatGroupsAdapter!!.openBottomsheet(groupList!![position])

//                        Toast.makeText(
//                            requireContext(),
//                            "Edit Not Available",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }
                }
            }
        rvGroups?.addOnItemTouchListener(touchListener)




        noGroupLayout = view.findViewById(R.id.no_group_layout)
        etSearch = view.findViewById(R.id.search_bar)
        clearSearch = view.findViewById(R.id.clear_search)
        conversationShimmer = view.findViewById(com.cometchat.pro.uikit.R.id.shimmer_layout)
        tvTitle = view.findViewById(R.id.tv_title)
        tvChannel = view.findViewById(R.id.tv_channel)
        tvDm = view.findViewById(R.id.tv_dm)
        layoutChannel = view.findViewById(R.id.layout_channel)
        layoutDm = view.findViewById(R.id.layout_dm)
        v_channel = view.findViewById(R.id.v_1)
        v_dm = view.findViewById(R.id.v_2)
        tvTitle?.typeface = FontUtils.getInstance(activity).getTypeFace(FontUtils.robotoMedium)

        val fragment = fragmentManager?.findFragmentByTag("startChat")
        if (fragment != null && fragment.isVisible) {
            Log.e("TAG", "onCreateView: group " + fragment.toString())
            tvTitle?.visibility = View.GONE
        }

        FeatureRestriction.isGroupSearchEnabled(object : FeatureRestriction.OnSuccessListener {
            override fun onSuccess(p0: Boolean) {
                if (!p0) {
                    etSearch?.visibility = View.GONE
                    clearSearch?.visibility = View.GONE
                }
            }
        })
        ivCreateGroup = view.findViewById(R.id.create_group)
        //  ivCreateGroup?.imageTintList = ColorStateList.valueOf(Color.parseColor(UIKitSettings.color))
        if (FeatureRestriction.isGroupCreationEnabled()) ivCreateGroup?.visibility =
            View.VISIBLE else ivCreateGroup?.visibility = View.GONE
        if (Utils.isDarkMode(requireContext())) {
            tvTitle?.setTextColor(resources.getColor(R.color.textColorWhite))
        } else {
            tvTitle?.setTextColor(resources.getColor(R.color.primaryTextColor))
        }
        ivCreateGroup?.setOnClickListener(View.OnClickListener { view1: View? ->
//            val intent: Intent = Intent(context,  CometChatsUsers::class.java)
//           startActivity(intent)

            // view.findNavController().navigate(R.id.userLogin)

            mContext.goToCometChatUserList()
        })

        view.findViewById<ImageView>(com.cc.resellconnect.R.id.chat_rule)
            .setOnClickListener(View.OnClickListener { view1: View? ->

                mContext.goToCometChatRules()
            })



        changeLayout()


        layoutChannel.setOnClickListener {
            MyApplication.isChannel = true
            changeLayout()

            //   findNavController().navigate(com.cc.resellconnect.R.id.cometChatGroupList)
        }

        layoutDm.setOnClickListener {
            MyApplication.isChannel = false
            changeLayout()

            Log.v("TAG33", "destina :${findNavController().currentDestination?.displayName}")
            try{
                findNavController().navigate(R.id.action_cometChatGroupList_to_cometChatConversationList)
            }catch (e:Exception){
                findNavController().navigate(R.id.action_cometChatUserList_to_cometChatConversationList)
            }

        }




        etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.isEmpty()) {
                    // if etSearch is empty then fetch all groups.
                    groupsRequest = null
                    rvGroups?.clear()
                    fetchGroup()
                } else {
                    // Search group based on text in etSearch field.
                    searchGroup(editable.toString())
                }
            }
        })
        etSearch?.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(
                textView: TextView,
                i: Int,
                keyEvent: KeyEvent?
            ): Boolean {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    searchGroup(textView.text.toString())
                    clearSearch?.visibility = View.VISIBLE
                    return true
                }
                return false
            }
        })
        clearSearch?.setOnClickListener {
            etSearch?.setText("")
            clearSearch?.visibility = View.GONE
            searchGroup(etSearch?.text.toString())
            val inputMethodManager: InputMethodManager =
                context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            // Hide the soft keyboard
            inputMethodManager.hideSoftInputFromWindow(etSearch?.windowToken, 0)
        }

        //Uses to fetch next list of group if rvGroupList (RecyclerView) is scrolled in upward direction.
        rvGroups?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    fetchGroup()
                }
            }
        })

        // Used to trigger event on click of group item in rvGroupList (RecyclerView)
//        rvGroups?.setItemClickListener(object : OnItemClickListener<Group?>() {
//            public override fun OnItemClick(t: Any, position: Int) {
//                event?.OnItemClick(t as Group, position)
//
//                  // Toast.makeText(mContext,"click 2",Toast.LENGTH_SHORT).show()
//            }
//        })

//        adapterclickListener()

    //    enableSwipeToDeleteAndUndo()

        return view
    }

//    private fun enableSwipeToDeleteAndUndo() {
//
//        val swipeToDeleteCallback: SwipeToDeleteCallback = object : SwipeToDeleteCallback(requireContext()) {
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
//                val position = viewHolder.adapterPosition
//             //   val item: String = mAdapter.getData().get(position)
//             //   mAdapter.removeItem(position)
//                val snackbar = Snackbar
//                    .make(
//                       etSearch!! ,
//                        "Item was removed from the list.",
//                        Snackbar.LENGTH_LONG
//                    )
//                snackbar.setAction("UNDO") {
////                    mAdapter.restoreItem(item, position)
////                    recyclerView.scrollToPosition(position)
//                }
//                snackbar.setActionTextColor(Color.YELLOW)
//                snackbar.show()
//            }
//        }
//        val itemTouchhelper = ItemTouchHelper(swipeToDeleteCallback)
//        itemTouchhelper.attachToRecyclerView(rvGroups)
//    }

    fun adapterclickListener(t:Group , position: Int){
        event?.OnItemClick(t, position)

     //   Toast.makeText(mContext,"click 2",Toast.LENGTH_SHORT).show()

    }

    var itemTouchHelper = object :
        ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            var position = viewHolder.adapterPosition
            when (direction) {
                ItemTouchHelper.LEFT -> {
            //        onChildDraw()

                    Toast.makeText(context,"swipe",Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isTitleVisible)
            tvTitle?.visibility = View.VISIBLE
        else tvTitle?.visibility = View.GONE

        if (isCreateGroupVisible)
            ivCreateGroup?.visibility = View.VISIBLE
        else ivCreateGroup?.visibility = View.GONE
    }


    fun changeLayout() {
        if (MyApplication.isChannel) {
            tvChannel.setTextColor(mContext.getColor(R.color.black))
            tvDm.setTextColor(mContext.getColor(R.color.grey))
            v_channel.visibility = View.VISIBLE
            v_dm.visibility = View.INVISIBLE

        } else {
            tvChannel.setTextColor(mContext.getColor(R.color.grey))
            tvDm.setTextColor(mContext.getColor(R.color.black))
            v_channel.visibility = View.INVISIBLE
            v_dm.visibility = View.VISIBLE
        }
    }

    /**
     * This method is used to retrieve list of groups present in your App_ID.
     * For more detail please visit our official documentation []//prodocs.cometchat.com/docs/android-groups-retrieve-groups" ">&quot;https://prodocs.cometchat.com/docs/android-groups-retrieve-groups&quot;
     *
     * @see GroupsRequest
     */
    private fun fetchGroup() {
        MyApplication.activity?.makeConversationList()
        if (groupsRequest == null) {
            groupsRequest = GroupsRequestBuilder().setLimit(30).build()
        }
        groupsRequest?.fetchNext(object : CallbackListener<List<Group>?>() {
            override fun onSuccess(groups: List<Group>?) {
                stopHideShimmer()
                val filteredList: MutableList<Group> = filterGroup(groups as MutableList<Group>)
                rvGroups?.setGroupList(groups) // sets the groups in rvGroupList i.e CometChatGroupList Component.

                groupList.addAll((groups))
                if (groupList.size == 0) {
                    noGroupLayout?.visibility = View.VISIBLE
                    rvGroups?.visibility = View.GONE
                } else {
                    noGroupLayout?.visibility = View.GONE
                    rvGroups?.visibility = View.VISIBLE
                }
            }

            override fun onError(e: CometChatException) {
                stopHideShimmer()
                if (rvGroups != null) {
                fetchGroup()
                //    ErrorMessagesUtils.cometChatErrorMessage(context, e.localizedMessage)
                }
            }
        })
    }

    private fun filterGroup(groups: MutableList<Group>): MutableList<Group> {
        val resultList: MutableList<Group> = ArrayList()
        for (group in groups) {
            if (group.isJoined) {
                resultList.add(group)
            }
            if (UIKitSettings.groupInMode == GroupMode.PUBLIC_GROUP &&
                group.groupType.equals(CometChatConstants.GROUP_TYPE_PUBLIC, ignoreCase = true)
            ) {
                resultList.add(group)
            } else if (UIKitSettings.groupInMode == GroupMode.PASSWORD_GROUP &&
                group.groupType.equals(CometChatConstants.GROUP_TYPE_PASSWORD, ignoreCase = true)
            ) {
                resultList.add(group)
            } else if (UIKitSettings.groupInMode == GroupMode.ALL_GROUP) {
                resultList.add(group)
            }
        }
        return resultList
    }

    /**
     * This method is used to search groups present in your App_ID.
     * For more detail please visit our official documentation []//prodocs.cometchat.com/docs/android-groups-retrieve-groups" ">&quot;https://prodocs.cometchat.com/docs/android-groups-retrieve-groups&quot;
     *
     * @param s is a string used to get groups matches with this string.
     * @see GroupsRequest
     */
    private fun searchGroup(s: String) {
        val groupsRequest: GroupsRequest =
            GroupsRequestBuilder().setSearchKeyWord(s).setLimit(100).build()
        groupsRequest.fetchNext(object : CallbackListener<List<Group?>?>() {
            override fun onSuccess(groups: List<Group?>?) {
                rvGroups?.searchGroupList(groups) // sets the groups in rvGroupList i.e CometChatGroupList Component.
            }

            override fun onError(e: CometChatException) {
                Log.d("TAG", "onError: " + e.message)
            }
        })
    }

    /**
     * This method is used to hide shimmer effect if the list is loaded.
     */
    private fun stopHideShimmer() {
        conversationShimmer?.stopShimmer()
        conversationShimmer?.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        groupsRequest = null
        cometChatGroupsAdapter = null
        fetchGroup()
    }

    fun setTitleVisible(isVisible: Boolean) {
        isTitleVisible = isVisible
    }

    fun setGroupCreateVisible(isVisible: Boolean) {
        isCreateGroupVisible = isVisible
    }

    companion object {
        private var event: OnItemClickListener<Any>? = null
        private val TAG: String = "CometChatGroupListScreen"

        /**
         *
         * @param groupItemClickListener An object of `OnItemClickListener<T>` abstract class helps to initialize with events
         * to perform onItemClick & onItemLongClick.
         * @see OnItemClickListener
         */
        @JvmStatic
        fun setItemClickListener(groupItemClickListener: OnItemClickListener<Any>) {
            event = groupItemClickListener
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity


    }


}

