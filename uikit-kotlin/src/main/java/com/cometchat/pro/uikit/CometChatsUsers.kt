package com.cometchat.pro.uikit

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

import com.cometchat.pro.core.CometChat
import com.cometchat.pro.core.UsersRequest
import com.cometchat.pro.exceptions.CometChatException
import com.cometchat.pro.models.User
import com.cometchat.pro.uikit.ui_components.shared.cometchatUsers.CometChatUsers
import com.cometchat.pro.uikit.ui_components.shared.cometchatUsers.CometChatUsersAdapter
import com.cometchat.pro.uikit.ui_resources.utils.ErrorMessagesUtils
import com.cometchat.pro.uikit.ui_resources.utils.FontUtils
import com.cometchat.pro.uikit.ui_resources.utils.Utils
import com.cometchat.pro.uikit.ui_resources.utils.item_clickListener.OnItemClickListener
import com.cometchat.pro.uikit.ui_settings.FeatureRestriction
import com.cometchat.pro.uikit.ui_settings.UIKitSettings
import com.cometchat.pro.uikit.ui_settings.enum.UserMode
import com.facebook.shimmer.ShimmerFrameLayout
import java.util.ArrayList

class CometChatsUsers : AppCompatActivity() {
    private var isTitleVisible: Boolean = true
    private val LIMIT: Int = 30
  //  private var c: Context? = null
    private val isSearching: Boolean = false
    private val userListAdapter: CometChatUsersAdapter? = null
    private var usersRequest // Use to fetch users
            : UsersRequest? = null
    private var rvUserList // Use to display list of users
            : CometChatUsers? = null
    private var etSearch // Use to perform search operation on list of users.
            : EditText? = null
    private var clearSearch //Use to clear the search operation performed on list.
            : ImageView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var title: TextView? = null
    private var rlSearchBox: RelativeLayout? = null
    private var noUserLayout: LinearLayout? = null
    private val userList: MutableList<User> = ArrayList()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comet_chat_users)
        title = findViewById(com.cometchat.pro.uikit.R.id.tv_title)
        title?.typeface = FontUtils.getInstance(this).getTypeFace(FontUtils.robotoMedium)
        rvUserList = findViewById(com.cometchat.pro.uikit.R.id.rv_user_list)
        noUserLayout = findViewById(com.cometchat.pro.uikit.R.id.no_user_layout)
        etSearch = findViewById(com.cometchat.pro.uikit.R.id.search_bar)
        clearSearch = findViewById(com.cometchat.pro.uikit.R.id.clear_search)
        rlSearchBox = findViewById(com.cometchat.pro.uikit.R.id.rl_search_box)
        shimmerFrameLayout = findViewById(com.cometchat.pro.uikit.R.id.shimmer_layout)

        fetchUsers()
        if (isTitleVisible)
            title?.visibility = View.VISIBLE
        else title?.visibility = View.GONE


        val fragment = fragmentManager?.findFragmentByTag("startChat")
        if (fragment != null && fragment.isVisible) {
            Log.e(TAG, "onCreateView: user " + fragment.toString())
            title?.visibility = View.GONE
        }

        FeatureRestriction.isUserSearchEnabled(object : FeatureRestriction.OnSuccessListener {
            override fun onSuccess(p0: Boolean) {
                if (!p0) {
                    etSearch?.visibility = View.GONE
                    clearSearch?.visibility = View.GONE
                }
            }

        })
        if (Utils.isDarkMode(this)) {
            title!!.setTextColor(resources.getColor(com.cometchat.pro.uikit.R.color.textColorWhite))
        } else {
            title!!.setTextColor(resources.getColor(com.cometchat.pro.uikit.R.color.primaryTextColor))
        }
        etSearch!!.addTextChangedListener(object : TextWatcher {
            public override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            public override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            public override fun afterTextChanged(editable: Editable) {
                if (editable.isEmpty()) {
                    // if etSearch is empty then fetch all users.
                    usersRequest = null
                    rvUserList!!.clear()
                    fetchUsers()
                } else {
                    // Search users based on text in etSearch field.
                    searchUser(editable.toString())
                }
            }
        })
        etSearch!!.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            public override fun onEditorAction(textView: TextView, i: Int, keyEvent: KeyEvent?): Boolean {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    searchUser(textView.text.toString())
                    clearSearch!!.visibility = View.VISIBLE
                    return true
                }
                return false
            }
        })
        clearSearch!!.setOnClickListener {
            etSearch!!.setText("")
            clearSearch!!.visibility = View.GONE
            searchUser(etSearch?.text.toString())
            val inputMethodManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            // Hide the soft keyboard
            inputMethodManager.hideSoftInputFromWindow(etSearch!!.windowToken, 0)
        }


        // Uses to fetch next list of user if rvUserList (RecyclerView) is scrolled in upward direction.
        rvUserList!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            public override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    fetchUsers()
                }
            }
        })

        // Used to trigger event on click of user item in rvUserList (RecyclerView)
        rvUserList!!.setItemClickListener(object : OnItemClickListener<User?>() {
            public override fun OnItemClick(t: Any, position: Int) {
                if (events != null) events!!.OnItemClick(t as User, position)
            }
        })

    }


    private fun stopHideShimmer() {
        shimmerFrameLayout?.stopShimmer()
        shimmerFrameLayout?.visibility = View.GONE
    }


    /**
     * This method is used to retrieve list of users present in your App_ID.
     * For more detail please visit our official documentation []//prodocs.cometchat.com/docs/android-users-retrieve-users.section-retrieve-list-of-users" ">&quot;https://prodocs.cometchat.com/docs/android-users-retrieve-users#section-retrieve-list-of-users&quot;
     *
     * @see UsersRequest
     */
    private fun fetchUsers() {
        if (usersRequest == null) {
            if (UIKitSettings.userInMode == UserMode.FRIENDS) usersRequest = UsersRequest.UsersRequestBuilder()
                .setLimit(30)
                .friendsOnly(true).build()
            else if (UIKitSettings.userInMode == UserMode.ALL_USER) usersRequest = UsersRequest.UsersRequestBuilder()
                .setLimit(30).build()
        }
        usersRequest!!.fetchNext(object : CometChat.CallbackListener<List<User>>() {
            public override fun onSuccess(users: List<User>) {
                Log.e(TAG, "onfetchSuccess: " + users.size)
                userList.addAll(users)
                stopHideShimmer()
                rvUserList!!.setUserList(users) // set the users to rvUserList i.e CometChatUserList Component.
                if (userList.size == 0) {
                    noUserLayout!!.visibility = View.VISIBLE
                    rvUserList!!.visibility = View.GONE
                } else {
                    rvUserList!!.visibility = View.VISIBLE
                    noUserLayout!!.visibility = View.GONE
                }
            }

            public override fun onError(e: CometChatException) {
                Log.e(TAG, "onError: " + e.message)
                stopHideShimmer()
                if (this != null) ErrorMessagesUtils.cometChatErrorMessage(this@CometChatsUsers, e.code)
            }
        })
    }

    /**
     * This method is used to search users present in your App_ID.
     * For more detail please visit our official documentation []//prodocs.cometchat.com/docs/android-users-retrieve-users.section-retrieve-list-of-users" ">&quot;https://prodocs.cometchat.com/docs/android-users-retrieve-users#section-retrieve-list-of-users&quot;
     *
     * @param s is a string used to get users matches with this string.
     * @see UsersRequest
     */
    private fun searchUser(s: String) {
        val usersRequest: UsersRequest = UsersRequest.UsersRequestBuilder().setSearchKeyword(s).setLimit(100).build()
        usersRequest.fetchNext(object : CometChat.CallbackListener<List<User?>?>() {
            public override fun onSuccess(users: List<User?>?) {
                rvUserList!!.searchUserList(users) // set the users to rvUserList i.e CometChatUserList Component.
            }

            public override fun onError(e: CometChatException) {
                ErrorMessagesUtils.cometChatErrorMessage(this@CometChatsUsers, e.code)
            }
        })
    }

    public override fun onResume() {
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
    }

//    public override fun onAttach(context: Context) {
//        super.onAttach(context)
//        this.c = context
//    }

    fun setTitleVisible(isVisible: Boolean) {
        isTitleVisible = isVisible
    }

    companion object {
        private val TAG: String = "CometChatUserListScreen"
        private var events: OnItemClickListener<Any>? = null

        /**
         *
         * @param onItemClickListener An object of `OnItemClickListener<T>` abstract class helps to initialize with events
         * to perform onItemClick & onItemLongClick,
         * @see OnItemClickListener
         */
        @JvmStatic
        fun setItemClickListener(onItemClickListener: OnItemClickListener<Any>?) {
            events = onItemClickListener
        }
    }
}