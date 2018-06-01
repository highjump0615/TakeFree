package com.brainyapps.simplyfree.fragments.admin

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.admin.AdminUserActivity
import com.brainyapps.simplyfree.adapters.admin.AdminUserItemAdapter
import com.brainyapps.simplyfree.models.BaseModel
import com.brainyapps.simplyfree.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_admin_users.*
import kotlinx.android.synthetic.main.fragment_admin_user.*
import kotlinx.android.synthetic.main.fragment_admin_user.view.*
import java.util.ArrayList

/**
 * Created by Administrator on 2/19/18.
 */
class AdminUserFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    var adapter: AdminUserItemAdapter? = null
    var aryUser = ArrayList<User>()
    var aryUserAll = ArrayList<User>()

    var isInitialized = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_admin_user, container, false)

        // init list
        rootView.swiperefresh.setOnRefreshListener(this)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.list)

        recyclerView.layoutManager = LinearLayoutManager(this.activity)

        this.adapter = AdminUserItemAdapter(this.activity!!, aryUser)
        recyclerView.adapter = this.adapter
        recyclerView.itemAnimator = DefaultItemAnimator()

        return rootView
    }

    /**
     * get User data
     */
    fun getUsers(bRefresh: Boolean, bAnimation: Boolean) {

        if (bAnimation) {
            if (!this.swiperefresh.isRefreshing) {
                this.swiperefresh.isRefreshing = true
            }
        }

        val database = FirebaseDatabase.getInstance().reference
        var query = database.child(User.TABLE_NAME).orderByChild(BaseModel.FIELD_DATE)

        if (this.arguments!!.getInt(ARG_USER_LIST_TYPE) == AdminUserActivity.USER_BANNED) {
            query = database.child(User.TABLE_NAME).orderByChild(User.FIELD_BANNED).equalTo(true)
        }

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                stopRefresh()

                // store all users
                aryUserAll.clear()

                for (userItem in dataSnapshot.children) {
                    val user = userItem.getValue(User::class.java)
                    user!!.id = userItem.key
                    if (user.type == User.USER_TYPE_ADMIN) {
                        continue
                    }

                    aryUserAll.add(user)
                }

                updateList(bAnimation)
            }

            override fun onCancelled(error: DatabaseError) {
                stopRefresh()
            }
        })

        isInitialized = true
    }

    /**
     * update the list with animation
     */
    fun updateList(bAnimation: Boolean = false) {
        val activityUser = activity as AdminUserActivity
        val strSearch = activityUser.edit_search.text.toString()

        stopRefresh()

        // clear
        if (bAnimation) {
            adapter!!.notifyItemRangeRemoved(0, aryUser.count())
        }
        aryUser.clear()

        // categorize the items
        for (user in aryUserAll) {
            // add match-keyword only
            if (TextUtils.isEmpty(strSearch) || user.userFullName().contains(strSearch, ignoreCase = true)) {
                aryUser.add(user)
            }
        }

        // empty notice
        if (aryUser.isEmpty()) {
            text_empty_notice.visibility = View.VISIBLE
        }
        else {
            text_empty_notice.visibility = View.GONE
        }

        if (bAnimation) {
            adapter!!.notifyItemRangeInserted(0, aryUser.count())
        }
        else {
            adapter!!.notifyDataSetChanged()
        }
    }

    fun stopRefresh() {
        this.swiperefresh.isRefreshing = false
    }

    companion object {
        /**
         * The fragment argument representing thee section number for this
         * fragment.
         */
        private val ARG_USER_LIST_TYPE = "user_list_type"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(listType: Int): AdminUserFragment {
            val fragment = AdminUserFragment()
            val args = Bundle()
            args.putInt(ARG_USER_LIST_TYPE, listType)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onRefresh() {
        getUsers(true, false)
    }
}