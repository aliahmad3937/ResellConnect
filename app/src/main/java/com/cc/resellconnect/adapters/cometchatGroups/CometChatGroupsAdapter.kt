package com.cc.resellconnect.adapters.cometchatGroups

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cc.resellconnect.R
import com.cc.resellconnect.databinding.GroupListItemBinding
import com.cc.resellconnect.utils.MyApplication
import com.cc.resellconnect.utils.SavedPreference
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.Group
import com.cometchat.pro.uikit.ui_resources.utils.FontUtils
import com.cometchat.pro.uikit.ui_resources.utils.Utils
import com.cometchat.pro.uikit.ui_resources.utils.item_clickListener.OnItemClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


/**
 * Purpose - GroupListAdapter is a subclass of RecyclerView Adapter which is used to display
 * the list of groups. It helps to organize the list data in recyclerView.
 *
 * Created on - 20th December 2019
 *
 * Modified on  - 23rd March 2020
 *
 */
class CometChatGroupsAdapter(context: Context) :
    RecyclerView.Adapter<CometChatGroupsAdapter.GroupViewHolder>() {
    private var context: Context
    var groupList: MutableList<Group> = ArrayList()
    private var fontUtils: FontUtils
    private val muteList: MutableList<String>


    /**
     * It is constructor which takes groupsList as parameter and bind it with groupList in adapter.
     *
     * @param context is a object of Context.
     * @param groupList is a list of groups used in this adapter.
     */
    init {
        updateGroupList(groupList)
        MyApplication.activity?.makeConversationList()
        this.context = context
        fontUtils = FontUtils.getInstance(context)
        muteList = SavedPreference.getMuteList(context) as MutableList<String>

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val groupListRowBinding: GroupListItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.group_list_item, parent, false)
        return GroupViewHolder(groupListRowBinding)
    }

    /**
     * This method is used to bind the GroupViewHolder contents with group at given
     * position. It set group icon, group name in a respective GroupViewHolder content.
     *
     * @param groupViewHolder is a object of GroupViewHolder.
     * @param position is a position of item in recyclerView.
     * @see Group
     */
    override fun onBindViewHolder(groupViewHolder: GroupViewHolder, position: Int) {
        val group = groupList[position]
        groupViewHolder.groupListRowBinding.group = group
        groupViewHolder.groupListRowBinding.executePendingBindings()
        val id = "group_${group.guid}";

        try {
            if (MyApplication.activity?.UnreadCountGroupMap!!.containsKey(id)) {
                val value = MyApplication.activity?.UnreadCountGroupMap!![id]
                if (value != 0) {
                    groupViewHolder.groupListRowBinding.tvMessageCount.visibility = View.VISIBLE
                    groupViewHolder.groupListRowBinding.tvMessageCount.text = value.toString()
                } else {
                    groupViewHolder.groupListRowBinding.tvMessageCount.visibility = View.GONE
                }

            } else {
                groupViewHolder.groupListRowBinding.tvMessageCount.visibility = View.GONE
            }
        } catch (e: Exception) {
        }
//        for(i in map!!.keys){
//            Log.v("TAG77","key :$i value:${map!![i]}")
//        }
        Log.v("TAG77", "--------------------------------");
//        if(unreadCount == 0){
//            groupViewHolder.groupListRowBinding.tvMessageCount.visibility = View.GONE
//        }else{
//            groupViewHolder.groupListRowBinding.tvMessageCount.visibility = View.VISIBLE
//            groupViewHolder.groupListRowBinding.tvMessageCount.text = unreadCount.toString()
//
//        }


        if (muteList.isNotEmpty() && muteList.contains(group.guid)) {
            groupViewHolder.groupListRowBinding.editTask.text = "UnMute"
            groupViewHolder.groupListRowBinding.muteNotif.setImageDrawable(context.getDrawable(R.drawable.mute))
        } else {
            groupViewHolder.groupListRowBinding.muteNotif.setImageDrawable(context.getDrawable(R.drawable.unmute))
            groupViewHolder.groupListRowBinding.editTask.text = "Mute"
        }

        groupViewHolder.groupListRowBinding.muteNotif.setOnClickListener {
            openBottomsheet(group)
            //  Toast.makeText(context,"click",Toast.LENGTH_SHORT).show()
        }


        groupViewHolder.groupListRowBinding.layout.setOnClickListener {
            // openBottomsheet(group)
            //  Toast.makeText(context,"click message",Toast.LENGTH_SHORT).show()
            MyApplication.cometChatGroupList!!.adapterclickListener(group, position)
        }


        if (group.name == "Investing Talk") {
            groupViewHolder.groupListRowBinding.avGroup.setImageDrawable(context.getDrawable(R.drawable.ic_talk))
        } else if (group.name == "Sneakers") {

            groupViewHolder.groupListRowBinding.avGroup.setImageDrawable(context.getDrawable(R.drawable.ic_sneaker))
        } else {
            Glide.with(context)
                .load(group.icon)
                .error(R.drawable.ic_sneaker)
                .into(groupViewHolder.groupListRowBinding.avGroup)
        }


        groupViewHolder.groupListRowBinding.txtUserMessage.text =
            context.resources.getString(R.string.members) + ": " + group.membersCount

        when (group.groupType) {
            CometChatConstants.GROUP_TYPE_PRIVATE -> groupViewHolder.groupListRowBinding.txtUserName.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_private_group,
                0
            )
            CometChatConstants.GROUP_TYPE_PASSWORD -> groupViewHolder.groupListRowBinding.txtUserName.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_password_protected_group,
                0
            )
            else -> groupViewHolder.groupListRowBinding.txtUserName.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                0,
                0
            )
        }

        //       groupViewHolder.groupListRowBinding.avGroup.setBackgroundColor(Color.parseColor("#000000"))
//        groupViewHolder.groupListRowBinding.avGroup.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
        groupViewHolder.groupListRowBinding.root.setTag(R.string.group, group)
        groupViewHolder.groupListRowBinding.txtUserMessage.typeface =
            fontUtils.getTypeFace(FontUtils.robotoRegular)
        groupViewHolder.groupListRowBinding.txtUserName.typeface =
            fontUtils.getTypeFace(FontUtils.robotoMedium)
        if (Utils.isDarkMode(context)) {
            groupViewHolder.groupListRowBinding.txtUserName.compoundDrawableTintList =
                ColorStateList.valueOf(context.resources.getColor(R.color.grey))
            groupViewHolder.groupListRowBinding.txtUserName.setTextColor(
                context.resources.getColor(
                    R.color.textColorWhite
                )
            )
            groupViewHolder.groupListRowBinding.tvSeprator.setBackgroundColor(
                context.resources.getColor(
                    R.color.grey
                )
            )
        } else {
            groupViewHolder.groupListRowBinding.txtUserName.compoundDrawableTintList =
                ColorStateList.valueOf(context.resources.getColor(R.color.message_bubble_grey))
            groupViewHolder.groupListRowBinding.txtUserName.setTextColor(
                context.resources.getColor(
                    R.color.primaryTextColor
                )
            )
            groupViewHolder.groupListRowBinding.tvSeprator.setBackgroundColor(
                context.resources.getColor(
                    R.color.light_grey
                )
            )
        }
    }

    private val TAG = "ChannelGroupAdapter"


    fun getList(): MutableList<Group> {
        return groupList
    }

    fun openBottomsheet(group: Group) {
        // on below line we are creating a new bottom sheet dialog.
        val dialog = BottomSheetDialog(context)

        // on below line we are inflating a layout file which we have created.
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_mute, null)

        // on below line we are creating a variable for our button
        // which we are using to dismiss our dialog.
        val btnCancel = view.findViewById<MaterialButton>(R.id.btn_cancel)
        val tvMute = view.findViewById<TextView>(R.id.tv_mute)




        if (muteList.isNotEmpty() && muteList.contains(group.guid)) {
            tvMute.setText("UnMute")
        } else {
            tvMute.setText("Mute")
        }


        tvMute.setOnClickListener {
            val body = JSONObject()
            val guids = JSONArray()
            val uids = JSONArray()
            guids.put(group.guid)
            uids.put(CometChat.getLoggedInUser().uid)

            body.put("uids", uids)
            body.put("guids", guids)
            if (tvMute.text.toString() == "Mute") {
                body.put("timeInMS", "1628425767881")

                CometChat.callExtension("push-notification", "POST", "/v1/mute-chat", body,
                    object : CometChat.CallbackListener<JSONObject?>() {
                        override fun onSuccess(jsonObject: JSONObject?) {
                            //On Success
                            Log.v("TAG656", "Success Mute")
                            muteList.add(group.guid)
                            SavedPreference.saveMuteList(context, muteList)
                            notifyDataSetChanged()
                            dialog.dismiss()
                        }

                        override fun onError(e: CometChatException) {
                            //On Failure
                            Log.v("TAG656", "Error :${e.localizedMessage}")
                        }
                    })
            } else {

                CometChat.callExtension("push-notification", "POST", "/v1/unmute-chat", body,
                    object : CometChat.CallbackListener<JSONObject?>() {
                        override fun onSuccess(jsonObject: JSONObject?) {
                            //On Success
                            Log.v("TAG656", "Success UnMute")
                            muteList.remove(group.guid)
                            SavedPreference.saveMuteList(context, muteList)
                            notifyDataSetChanged()
                            dialog.dismiss()
                        }

                        override fun onError(e: CometChatException) {
                            //On Failure
                            Log.v("TAG656", "Error Unmute :${e.localizedMessage}")
                        }
                    })
            }
        }


        // on below line we are adding on click listener
        // for our dismissing the dialog button.
        btnCancel.setOnClickListener {
            Log.v(TAG, "btn click")
            // on below line we are calling a dismiss
            // method to close our dialog.

            dialog.dismiss()


        }
        // below line is use to set cancelable to avoid
        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)

        // on below line we are setting
        // content view to our view.
        dialog.setContentView(view)

        // on below line we are calling
        // a show method to display a dialog.
        dialog.show()
    }

    private fun muteNotif() {
        val body = JSONObject()
        val uids = JSONArray()
        val guids = JSONArray()

        guids.put("")

        // uids.add("superhero1")
//        guids.add("supergroup")

        //    body.put("uids", uids)
        body.put("guids", guids)
        body.put("timeInMS", "1628425767881")

        CometChat.callExtension("push-notification", "POST", "/v1/mute-chat", body,
            object : CometChat.CallbackListener<JSONObject?>() {
                override fun onSuccess(jsonObject: JSONObject?) {
                    //On Success
                }

                override fun onError(e: CometChatException) {
                    //On Failure
                }
            })
    }

    /**
     * This method is used to update groupList in adapter.
     * @param groupList is a list of groups which will be updated in adapter.
     */
    fun updateGroupList(groupList: List<Group?>) {
        for (i in groupList.indices) {
            if (!this.groupList.contains(groupList[i])) {
                this.groupList.add(groupList[i]!!)
            }
        }
        notifyDataSetChanged()
    }

    /**
     * This method is used to update a particular group in groupList of adapter.
     *
     * @param group is an object of Group. It will be updated with previous group in a list.
     */
    fun updateGroup(group: Group?) {
        if (group != null) {
            if (groupList.contains(group)) {
                val index = groupList.indexOf(group)
                groupList.removeAt(index)
                groupList.add(group)
                notifyItemChanged(index)
            } else {
                groupList.add(group)
                notifyItemInserted(itemCount - 1)
            }
        }
    }

    /**
     * This method is used to remove particular group from groupList in adapter.
     *
     * @param group is a object of Group which will be removed from groupList.
     *
     * @see Group
     */
    fun removeGroup(group: Group?) {
        if (group != null) {
            if (groupList.contains(group)) {
                val index = groupList.indexOf(group)
                groupList.remove(group)
                notifyItemRemoved(index)
            }
        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    /**
     * This method is used to set searchGroupList with a groupList in adapter.
     *
     * @param groups is a list of group which will be set with a groupList in adapter.
     */
    fun searchGroup(groups: List<Group?>?) {
        if (groups != null) {
            groupList.clear();
            updateGroupList(groups)
            notifyDataSetChanged()
        }
    }

    /**
     * This method is used to add particular group in groupList of adapter.
     *
     * @param group is a object of group which will be added in groupList.
     *
     * @see Group
     */
    fun add(group: Group?) {
        group?.let { updateGroup(it) }
    }

    fun clear() {
        groupList.clear()
        notifyDataSetChanged()
    }


    inner class GroupViewHolder(var groupListRowBinding: GroupListItemBinding) :
        RecyclerView.ViewHolder(groupListRowBinding.root)
}