package com.cc.resellconnect.ui.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.cc.resellconnect.R
import com.cc.resellconnect.databinding.ActivityMainBinding
import com.cc.resellconnect.models.CometChatUser
import com.cc.resellconnect.ui.fragments.CometChatGroupList
import com.cc.resellconnect.utils.Constants
import com.cometchat.pro.constants.CometChatConstants
import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.CometChat.markAsDelivered
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.*
import com.cometchat.pro.uikit.databinding.ActivityCometchatUnifiedBinding
import com.cc.resellconnect.ui.fragments.CometChatConversationList
import com.cc.resellconnect.utils.MyApplication
import com.cometchat.pro.core.ConversationsRequest
import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity
import com.cometchat.pro.uikit.ui_components.users.user_list.CometChatUserList
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants
import com.cometchat.pro.uikit.ui_resources.utils.ErrorMessagesUtils
import com.cometchat.pro.uikit.ui_resources.utils.Utils
import com.cometchat.pro.uikit.ui_resources.utils.custom_alertDialog.CustomAlertDialogHelper
import com.cometchat.pro.uikit.ui_resources.utils.custom_alertDialog.OnAlertDialogButtonClickListener
import com.cometchat.pro.uikit.ui_resources.utils.item_clickListener.OnItemClickListener
import com.cometchat.pro.uikit.ui_settings.FeatureRestriction
import com.cometchat.pro.uikit.ui_settings.UIKitSettings
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.badge.BadgeDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity(), OnAlertDialogButtonClickListener {

    var cometChatGroupList: CometChatGroupList? = null

    private var userSettingsEnabled: Boolean = false
    private var recentChatListEnabled: Boolean = false
    private var callListEnabled: Boolean = false
    private var groupListEnabled: Boolean = false
    private var userListEnabled: Boolean = false

    //Used to bind the layout with class
    private var activityCometChatUnifiedBinding: ActivityCometchatUnifiedBinding? = null

    //Stores the count of user whose messages are unread.
    val unreadCount: MutableList<String> = ArrayList()

    //  var unreadCountGroupMap:HashMap<String, Int?>? = null
    private var badgeDrawable: BadgeDrawable? = null
    private var fragment: Fragment? = null
    private var progressDialog: ProgressDialog? = null
    private var groupPassword: String? = null
    private var group: Group? = null


    lateinit var binding: ActivityMainBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    var cometChatUser: CometChatUser? = null

    private lateinit var navController: NavController


    var tokenn: String? = null
    var tier: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setWhiteStatusBarColor()

        tier = intent.getStringExtra("tier")

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        createAnonymousUserInCometChat()




        firestore
            .collection("Chat Usernames")
            .document(auth.uid.toString())
            .addSnapshotListener { value, error ->
                if (error == null) {
                    if (value != null && value!!.exists()) {
                        Log.e("TAG9", "user Exist Success: ")
                        cometChatUser = value.toObject(CometChatUser::class.java)
                        if (cometChatUser!!.cometChatUID == null) {
                            cometChatUser = null
                        } else {
                            login(cometChatUser!!.cometChatUID.toString())
                        }

                        //    navController.navigate(R.id.cometChatGroupList)
                    } else {
                        cometChatUser = null
                    }
                } else {
                    cometChatUser = null
                }
            }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        //    navController.navigate(R.id.resellConnect)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (
                destination.id == R.id.splashFragment ||
                destination.id == R.id.userLogin

            ) {
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
        binding.bottomNavigation.setItemIconTintList(null);


        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.resellConnect -> {
                    navController.navigate(R.id.resellConnect)
                }
                R.id.guides -> {
                    navController.navigate(R.id.guides)
                }
                R.id.channelsFragment -> {
                    if (cometChatUser != null) {
                        if(MyApplication.isGuest){
                            navController.navigate(R.id.channelsFragment)
                        }else {
                            if (MyApplication.isChannel) {
                                navController.navigate(R.id.cometChatGroupList)
                            } else {
                                navController.navigate(R.id.cometChatConversationList)
                            }
                        }
                    } else {
                        navController.navigate(R.id.channelsFragment)
                    }
                }
                R.id.settings -> {
                    navController.navigate(R.id.settings)
                }
            }

            true
        }









        initViewComponent()
        // It performs action on click of user item in CometChatUserListScreen.
        setUserClickListener()

        val config: EmojiCompat.Config = BundledEmojiCompatConfig(this)
        EmojiCompat.init(config)

        //It performs action on click of group item in CometChatGroupListScreen.
        //It checks whether the logged-In user is already a joined a group or not and based on it perform actions.
        setGroupClickListener()

        //It performs action on click of conversation item in CometChatConversationListScreen
        //Based on conversation item type it will perform the actions like open message screen for user and groups..
        setConversationClickListener()

        if (intent != null && intent.hasExtra("from") && intent.getStringExtra("from") == "Notification") {
            var group = Group()
//            Log.v(
//                "TAG789",
//                "Group intent data:guid:${group!!.guid}:avatar:${group!!.icon}:owner:${group!!.owner}:name:${group!!.name}:group type:${group!!.groupType}:member count:${group!!.membersCount}:group desc:${group!!.description}:group password:${group!!.password}"
//            )

            group!!.guid = intent.getStringExtra(UIKitConstants.IntentStrings.GUID)
            group.icon = intent.getStringExtra(UIKitConstants.IntentStrings.AVATAR)
            group.owner = intent.getStringExtra(UIKitConstants.IntentStrings.GROUP_OWNER)
            group.name = intent.getStringExtra(UIKitConstants.IntentStrings.NAME)
            group.groupType = intent.getStringExtra(UIKitConstants.IntentStrings.GROUP_TYPE)
            group.membersCount = intent.getIntExtra(UIKitConstants.IntentStrings.MEMBER_COUNT, 0)
            group.description = null
            group.password = null
            startGroupIntent(group)
        }

    }

    private fun createAnonymousUserInCometChat() {
//        if (MyApplication.isGuest) {
//            Log.v(TAG, "not empty")
//            val user = User()
//            user.uid = "user${System.currentTimeMillis()}"
//            user.name = "Anonymous"
//
//            CometChat.createUser(
//                user,
//                Constants.AUTH_KEY,
//                object : CometChat.CallbackListener<User>() {
//                    override fun onSuccess(user: User) {
//                        Log.v(TAG, "success user")
//                        createUserInFirestore(user)
//
//                        login(user.uid)
//                    }
//
//                    override fun onError(e: CometChatException) {
//                        // progressHUD!!.dismiss()
//                        Log.v(TAG, "error :${e.code}")
//                    }
//                })
//
//        }
        if(MyApplication.isGuest){
            if (CometChat.getLoggedInUser() == null) {
                CometChat.login("user161740501462", Constants.AUTH_KEY, object : CometChat.CallbackListener<User?>() {
                    override fun onSuccess(user: User?) {
                     //   gettoken()

                        Log.e("TAG7", "Comet Chat login Success: ")
//                startActivity(Intent(this@LoginCometChatUser, CometChatUI::class.java))
//                finish()
                    }

                    override fun onError(e: CometChatException) {
                        // inputLayout!!.isEndIconVisible = true
                        Log.e("TAG9", "onError login: " + e.code)
                        createAnonymousUserInCometChat()
//                findViewById<View>(R.id.loginProgress).visibility = View.GONE
//                ErrorMessagesUtils.cometChatErrorMessage(this@LoginCometChatUser, e.code)
                    }
                })
            }
        }
    }

    fun updateUserCometChatProfile(toString: String) {
        if(CometChat.getLoggedInUser() != null) {
            val user: User = User()
            user.name = CometChat.getLoggedInUser().name
            user.uid = CometChat.getLoggedInUser().uid
            user.avatar = toString

            val authkey = UIKitConstants.AppInfo.AUTH_KEY
            CometChat.updateUser(user, authkey, object : CometChat.CallbackListener<User?>() {
                override fun onSuccess(user: User?) {
                    //  if (context != null)
//                binding.tvUname.text = user!!.name.toString()
                }

                override fun onError(e: CometChatException) {
                    updateUserCometChatProfile(toString)
                    //     if (context != null)
                    //   ErrorMessagesUtils.cometChatErrorMessage(context, e.code)
//                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun createUserInFirestore(user: User) {
        val user = CometChatUser(
            user.name,
            user.uid,
            auth.uid.toString()
        )

        firestore
            .collection("Chat Usernames")
            .document(auth.uid.toString())
            .set(user)
            .addOnSuccessListener {

                // Utils.showToast(requireContext(),"user created successfully!")
            }

    }

    private fun joinSnekerGroup(){
        CometChat.joinGroup(
            "sneakers-rc",
            CometChatConstants.GROUP_TYPE_PUBLIC,
            "",
            object : CometChat.CallbackListener<Group?>() {
                override fun onSuccess(group: Group?) {

                    group?.let { startGroupIntent(it) }
                }

                override fun onError(e: CometChatException) {
                     joinSnekerGroup()
                }
            })
    }

    fun goToCometChatUserList() {
        navController.navigate(R.id.cometChatUserList)
    }

    fun goToCometChatRules() {
        startActivity(
            Intent(this@MainActivity, CometChatRules::class.java)
                .putExtra("title", "Chat Rules")
                .putExtra("url", "chat_rule.pdf")
        )
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }


    fun goToCometChatRules2() {
        startActivity(
            Intent(this@MainActivity, CometChatRules::class.java)
                .putExtra("title", "Beginners Guide")
                .putExtra("url", "brginner_guide.pdf")
        )
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }


    private fun login(uid: String) {
        if (CometChat.getLoggedInUser() == null) {
            CometChat.login(uid, Constants.AUTH_KEY, object : CometChat.CallbackListener<User?>() {
                override fun onSuccess(user: User?) {
                    gettoken()

                    Log.e("TAG7", "Comet Chat login Success: ")
//                startActivity(Intent(this@LoginCometChatUser, CometChatUI::class.java))
//                finish()
                }

                override fun onError(e: CometChatException) {
                    // inputLayout!!.isEndIconVisible = true
                    Log.e("TAG9", "onError login: " + e.code)
//                findViewById<View>(R.id.loginProgress).visibility = View.GONE
//                ErrorMessagesUtils.cometChatErrorMessage(this@LoginCometChatUser, e.code)
                }
            })
        } else {
            gettoken()
        }
    }

    private fun gettoken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.v(
                        "TAG7",
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                tokenn = task.result

                firestore
                    .collection("User Tokens")
                    .document(auth.uid.toString())
                    .set(
                        mapOf("fcmToken" to tokenn.toString(), "uid" to auth.uid.toString())
                    ).addOnSuccessListener {
                        Log.v("TAG7:onSuccess ", "save in db")
                    }.addOnFailureListener {
                        Log.v("TAG7:onFailure ", "not save in db")
                    }


                CometChat.registerTokenForPushNotification(
                    tokenn.toString(),
                    object : CometChat.CallbackListener<String?>() {
                        override fun onSuccess(s: String?) {
                            Log.v("TAG7:onSuccess ", s!!)
                        }

                        override fun onError(e: CometChatException) {
                            Log.v("TAG7:onError ", e.message!!)
                        }
                    })


                Log.v("TAG7", "onCreate: token:$tokenn")
            })
    }


    private fun setWhiteStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        }
    }

    private fun changeStatusBarColor(color: String) {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor(color)
        }
    }

    /**
     * @param colorId id of color
     * @param isStatusBarFontDark Light or Dark color
     */
    fun updateStatusBarColor(@ColorRes colorId: Int, isStatusBarFontDark: Boolean = true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = ContextCompat.getColor(this, colorId)
            setSystemBarTheme(isStatusBarFontDark)
        }
    }


    /** Changes the System Bar Theme.  */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun setSystemBarTheme(isStatusBarFontDark: Boolean) {
        // Fetch the current flags.
        val lFlags = window.decorView.systemUiVisibility
        // Update the SystemUiVisibility depending on whether we want a Light or Dark theme.
        window.decorView.systemUiVisibility =
            if (isStatusBarFontDark) lFlags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() else lFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }


    private fun setConversationClickListener() {
        CometChatConversationList.Companion.setItemClickListener(object :
            OnItemClickListener<Any>() {
            override fun OnItemClick(t: Any, position: Int) {
                var conversation = t as Conversation;
                if (conversation.conversationType == CometChatConstants.CONVERSATION_TYPE_GROUP)
                    startGroupIntent(conversation.conversationWith as Group) else startUserIntent(
                    conversation.conversationWith as User
                )
            }
        })
    }


    private fun setGroupClickListener() {
        CometChatGroupList.Companion.setItemClickListener(object : OnItemClickListener<Any>() {
            override fun OnItemClick(t: Any, position: Int) {
                group = t as Group
                if (group!!.isJoined) {
                    startGroupIntent(group)
                } else {
//                //    if (group!!.groupType == CometChatConstants.GROUP_TYPE_PASSWORD) {
//                        val dialogview =
//                            layoutInflater.inflate(com.cometchat.pro.uikit.R.layout.cc_dialog, null)
//                        val tvTitle =
//                            dialogview.findViewById<TextView>(com.cometchat.pro.uikit.R.id.textViewDialogueTitle)
//                        tvTitle.text = String.format(
//                            resources.getString(com.cometchat.pro.uikit.R.string.enter_password_to_join),
//                            group!!.name
//                        )
//                        CustomAlertDialogHelper(
//                            this@MainActivity,
//                            resources.getString(com.cometchat.pro.uikit.R.string.password),
//                            dialogview,
//                            resources.getString(
//                                com.cometchat.pro.uikit.R.string.join
//                            ),
//                            "",
//                            resources.getString(com.cometchat.pro.uikit.R.string.cancel),
//                            this@MainActivity,
//                            1,
//                            false
//                        )
//                    }
//                 //   else if (group!!.groupType == CometChatConstants.GROUP_TYPE_PUBLIC) {
                    joinGroup(group)
                    // }
                }
            }
        })
    }


    /**
     * This methods joins the logged-In user in a group.
     *
     * @param group  The Group user will join.
     * @see Group
     *
     * @see CometChat.joinGroup
     */
    private fun joinGroup(group: Group?) {
        if (FeatureRestriction.isJoinLeaveGroupsEnabled()) {
            progressDialog = ProgressDialog.show(
                this,
                "",
                resources.getString(com.cometchat.pro.uikit.R.string.joining)
            )
            progressDialog!!.setCancelable(false)
            CometChat.joinGroup(
                group!!.guid,
                group.groupType,
                group.password,
                //    groupPassword,
                object : CometChat.CallbackListener<Group?>() {
                    override fun onSuccess(group: Group?) {
                        if (progressDialog != null) progressDialog!!.dismiss()
                        group?.let { startGroupIntent(it) }
                    }

                    override fun onError(e: CometChatException) {
                        if (progressDialog != null) progressDialog!!.dismiss()
//                    ErrorMessagesUtils.cometChatErrorMessage(this@CometChatUI, e.code)
                        ErrorMessagesUtils.showCometChatErrorDialog(
                            this@MainActivity, resources.getString(
                                com.cometchat.pro.uikit.R.string.enter_the_correct_password
                            )
                        )
//                Snackbar.make(activityCometChatUnifiedBinding!!.bottomNavigation, resources.getString(R.string.unable_to_join_message) + e.message,
//                        Snackbar.LENGTH_SHORT).show()
                    }
                })
        }
    }


    /**
     * This method initialize the BadgeDrawable which is used on conversation menu of BottomNavigationBar to display unread conversations.
     * It Loads **CometChatConversationScreen** at initial phase.
     * @see CometChatConversationList
     */
    private fun initViewComponent() {

//        badgeDrawable = activityCometChatUnifiedBinding!!.bottomNavigation.getOrCreateBadge(R.id.menu_conversation)
        //  activityCometChatUnifiedBinding!!.bottomNavigation.setOnNavigationItemSelectedListener(this)

        if (UIKitSettings.color != null && UIKitSettings.color.isNotEmpty()) {
            window.statusBarColor = Color.parseColor("#FFFFFFFF")
            val widgetColor = Color.parseColor("#FF000000")
            val colorStateList = ColorStateList(
                arrayOf(intArrayOf(-android.R.attr.state_selected), intArrayOf()),
                intArrayOf(Color.GRAY, widgetColor)
            )
            activityCometChatUnifiedBinding?.bottomNavigation?.itemIconTintList = colorStateList
        }

        //        activityCometChatUnifiedBinding.bottomNavigation.getMenu().add(Menu.NONE,12,Menu.NONE,"Test").setIcon(R.drawable.ic_security_24dp);

        FeatureRestriction.isRecentChatListEnabled(object : FeatureRestriction.OnSuccessListener {
            override fun onSuccess(p0: Boolean) {
                recentChatListEnabled = p0
                activityCometChatUnifiedBinding?.bottomNavigation?.menu?.findItem(com.cometchat.pro.uikit.R.id.menu_conversation)?.isVisible =
                    p0
            }
        })
        FeatureRestriction.isUserListEnabled(object : FeatureRestriction.OnSuccessListener {
            override fun onSuccess(p0: Boolean) {
                userListEnabled = p0
                activityCometChatUnifiedBinding?.bottomNavigation?.menu?.findItem(com.cometchat.pro.uikit.R.id.menu_users)?.isVisible =
                    p0
            }
        })
        FeatureRestriction.isCallListEnabled(object : FeatureRestriction.OnSuccessListener {
            override fun onSuccess(p0: Boolean) {
                callListEnabled = p0
                activityCometChatUnifiedBinding?.bottomNavigation?.menu?.findItem(com.cometchat.pro.uikit.R.id.menu_call)?.isVisible =
                    p0
            }
        })
        FeatureRestriction.isGroupListEnabled(object : FeatureRestriction.OnSuccessListener {
            override fun onSuccess(p0: Boolean) {
                groupListEnabled = p0
                activityCometChatUnifiedBinding?.bottomNavigation?.menu?.findItem(com.cometchat.pro.uikit.R.id.menu_group)?.isVisible =
                    p0
            }

        })
        FeatureRestriction.isUserSettingsEnabled(object : FeatureRestriction.OnSuccessListener {
            override fun onSuccess(p0: Boolean) {
                userSettingsEnabled = p0
                activityCometChatUnifiedBinding?.bottomNavigation?.menu?.findItem(com.cometchat.pro.uikit.R.id.menu_more)?.isVisible =
                    p0
            }

        })


//        badgeDrawable!!.isVisible = false
        //       activityCometChatUnifiedBinding!!.bottomNavigation.id = com.cometchat.pro.uikit.R.id.menu_conversation
//        when {
//            recentChatListEnabled -> loadFragment(CometChatConversationList())
//            callListEnabled -> loadFragment(CometChatCallList())
//            groupListEnabled -> loadFragment(CometChatGroupList())
//            userSettingsEnabled -> loadFragment(CometChatUserProfile())
//            userListEnabled -> loadFragment(CometChatUserList())
//        }
    }


    /**
     * Loads the fragment get from parameter.
     * @param fragment
     * @return true if fragment is not null
     */
    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(com.cometchat.pro.uikit.R.id.frame, fragment).commit()
            return true
        }
        return false
    }//Logs the error if the error occurs.//add total count of users and groups whose messages are unread in BadgeDrawable//Add users whose messages are unread.
    //Add groups whose messages are unread.


    private fun setUserClickListener() {
        CometChatUserList.Companion.setItemClickListener(object : OnItemClickListener<Any>() {
            override fun OnItemClick(t: Any, position: Int) {
                startUserIntent(t as User)
            }
        })
    }

    /**
     * Open Message Screen for group using **CometChatMessageListActivity.class**
     *
     * @param group
     * @see CometChatMessageListActivity
     */
    fun startGroupIntent(group: Group?) {

        Log.v(
            "TAG789",
            "Group data:guid:${group!!.guid}:avatar:${group!!.icon}:owner:${group!!.owner}:name:${group!!.name}:group type:${group!!.groupType}:member count:${group!!.membersCount}:group desc:${group!!.description}:group password:${group!!.password}"
        )
        UnreadCountGroupMap["group_${group.guid}"] = 0
        val intent = Intent(this@MainActivity, CometChatMessageListActivity::class.java)
        intent.putExtra(UIKitConstants.IntentStrings.GUID, group!!.guid)
        intent.putExtra(UIKitConstants.IntentStrings.AVATAR, group.icon)
        intent.putExtra(UIKitConstants.IntentStrings.GROUP_OWNER, group.owner)
        intent.putExtra(UIKitConstants.IntentStrings.NAME, group.name)
        intent.putExtra(UIKitConstants.IntentStrings.GROUP_TYPE, group.groupType)
        intent.putExtra(UIKitConstants.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_GROUP)
        intent.putExtra(UIKitConstants.IntentStrings.MEMBER_COUNT, group.membersCount)
        intent.putExtra(UIKitConstants.IntentStrings.GROUP_DESC, group.description)
        intent.putExtra(UIKitConstants.IntentStrings.GROUP_PASSWORD, group.password)
        startActivity(intent)
    }

    /**
     * Open Message Screen for user using **CometChatMessageListActivity.class**
     *
     * @param user
     * @see CometChatMessageListActivity
     */
    private fun startUserIntent(user: User) {
        Log.e(TAG, "startUserIntent: " + user.link)
        val intent = Intent(this@MainActivity, CometChatMessageListActivity::class.java)
        intent.putExtra(UIKitConstants.IntentStrings.UID, user.uid)
        intent.putExtra(UIKitConstants.IntentStrings.AVATAR, user.avatar)
        intent.putExtra(UIKitConstants.IntentStrings.STATUS, user.status)
        intent.putExtra(UIKitConstants.IntentStrings.NAME, user.name)
        intent.putExtra(UIKitConstants.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER)
        intent.putExtra(UIKitConstants.IntentStrings.LINK, user.link)
        startActivity(intent)
    }


    override fun onButtonClick(alertDialog: AlertDialog?, v: View?, which: Int, popupId: Int) {
        val groupPasswordInput =
            v!!.findViewById<View>(com.cometchat.pro.uikit.R.id.edittextDialogueInput) as EditText
        if (which == DialogInterface.BUTTON_NEGATIVE) { // Cancel
            alertDialog!!.dismiss()
        } else if (which == DialogInterface.BUTTON_POSITIVE) { // Join
            try {
                groupPassword = groupPasswordInput.text.toString()
                if (groupPassword!!.length == 0) {
                    groupPasswordInput.setText("")
                    groupPasswordInput.error =
                        resources.getString(com.cometchat.pro.uikit.R.string.incorrect)
                } else {
                    try {
                        alertDialog!!.dismiss()
                        joinGroup(group)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * MessageListener to update unread count of conversations
     * @see CometChat.addMessageListener
     */
    fun addConversationListener() {
        CometChat.addMessageListener(TAG, object : CometChat.MessageListener() {
            override fun onTextMessageReceived(message: TextMessage) {
                markAsDelivered(message)
                setUnreadCount(message)
            }

            override fun onMediaMessageReceived(message: MediaMessage) {
                markAsDelivered(message)
                setUnreadCount(message)
            }

            override fun onCustomMessageReceived(message: CustomMessage) {
                markAsDelivered(message)
                setUnreadCount(message)
            }


        })
    }

    /**
     * Set unread message count
     * @param message An object of **BaseMessage** class that is been used to set unread count in BadgeDrawable.
     * @see BaseMessage
     */
    private fun setUnreadCount(message: BaseMessage) {
//        if (message.editedAt == 0L && message.deletedAt == 0L) {
        if (message.receiverType == CometChatConstants.RECEIVER_TYPE_GROUP) {
            if (!unreadCount.contains(message.receiverUid)) {
                //   unreadCount.add(message.receiverUid)
//                    setBadge()
            }
        } else {
            if (!unreadCount.contains(message.sender.uid)) {
                unreadCount.add(message.sender.uid)
                //   setBadge()
            }
        }
//        }
    }

    private var conversationsRequest: ConversationsRequest? = null
    val UnreadCountGroupMap = mutableMapOf<String, Int>()


    fun makeConversationList() {
        if (conversationsRequest == null) {
            conversationsRequest =
                ConversationsRequest.ConversationsRequestBuilder().setLimit(50).build()
        }
        conversationsRequest?.fetchNext(object : CometChat.CallbackListener<List<Conversation>>() {
            override fun onSuccess(conversationsList: List<Conversation>) {

                if (conversationsList.isNotEmpty()) {
                    // UnreadCountGroupMap.clear()
                    for (i in conversationsList) {
                        UnreadCountGroupMap[i.conversationId] = i.unreadMessageCount
                    }
                }
            }

            override fun onError(e: CometChatException) {
                ErrorMessagesUtils.cometChatErrorMessage(baseContext, e.code)
            }
        })
    }

    /**
     * Get Unread Count of conversation using `CometChat.getUnreadMessageCount()`.
     * @see CometChat.getUnreadMessageCount
     */
    val unreadConversationCount2: Int
        get() {
            CometChat.getUnreadMessageCount(object :
                CometChat.CallbackListener<HashMap<String?, HashMap<String, Int?>>>() {
                override fun onSuccess(stringHashMapHashMap: HashMap<String?, HashMap<String, Int?>>) {
                    Log.e(TAG, "onSuccess: unread $stringHashMapHashMap")
                    //   unreadCount.addAll(stringHashMapHashMap["user"]!!.keys) //Add users whose messages are unread.
                    unreadCount.addAll(stringHashMapHashMap["group"]!!.keys) //Add groups whose messages are unread.
                    //   unreadCountGroupMap = stringHashMapHashMap["group"]!!
                    for (i in stringHashMapHashMap["group"]!!.keys) {
                        Log.v("TAG77", "key :$i :count :${stringHashMapHashMap[i]}")
                    }

                    //  badgeDrawable!!.isVisible = unreadCount.size != 0
//                    if (unreadCount.size != 0) {
//                        badgeDrawable!!.number = unreadCount.size //add total count of users and groups whose messages are unread in BadgeDrawable
//                    }else{
//                        return 0
//                    }
                }

                override fun onError(e: CometChatException) {
                    e.message?.let { Log.e("onError: ", it) } //Logs the error if the error occurs.
                }
            })

            if (unreadCount.size != 0) {
                return unreadCount.size
                //      badgeDrawable!!.number = unreadCount.size //add total count of users and groups whose messages are unread in BadgeDrawable
            } else {
                return 0
            }
        }

    val unreadConversationCount: Unit
        get() {
            CometChat.getUnreadMessageCount(object :
                CometChat.CallbackListener<HashMap<String?, HashMap<String, Int?>>>() {
                override fun onSuccess(stringHashMapHashMap: HashMap<String?, HashMap<String, Int?>>) {
                    Log.e(TAG, "onSuccess: unread $stringHashMapHashMap")
                    //   unreadCount.addAll(stringHashMapHashMap["user"]!!.keys) //Add users whose messages are unread.
                    //    unreadCount.addAll(stringHashMapHashMap["group"]!!.keys) //Add groups whose messages are unread.
                    //  badgeDrawable!!.isVisible = unreadCount.size != 0
//                    if (unreadCount.size != 0) {
//                        badgeDrawable!!.number = unreadCount.size //add total count of users and groups whose messages are unread in BadgeDrawable
//                    }else{
//                        return 0
//                    }
                }

                override fun onError(e: CometChatException) {
                    e.message?.let { Log.e("onError: ", it) } //Logs the error if the error occurs.
                }
            })


        }

    override fun onStart() {
        super.onStart()
        addConversationListener() //Enable Listener when app starts
    }

    override fun onResume() {
        super.onResume()
        makeConversationList()
        MyApplication.activity = this
        unreadConversationCount // To get unread conversations count
    }

    override fun onPause() {
        super.onPause()
        MyApplication.activity = null
        unreadCount.clear() //Clear conversation count when app pauses or goes background.
    }

    fun gotoSneaker() {

    }


    companion object {
        //Used to identify class in Log's
        private val TAG = MainActivity::class.java.simpleName
    }
}


