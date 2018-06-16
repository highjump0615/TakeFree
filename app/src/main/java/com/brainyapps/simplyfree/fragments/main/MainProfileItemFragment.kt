package com.brainyapps.simplyfree.fragments.main

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.adapters.main.ProfileItemAdapter
import com.brainyapps.simplyfree.models.BaseModel
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_profile_item_list.*
import kotlinx.android.synthetic.main.fragment_profile_item_list.view.*
import java.util.ArrayList

/**
 * Created by Administrator on 2/19/18.
 */
class MainProfileItemFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, ProfileItemAdapter.DeletePostListener {

    var adapter: ProfileItemAdapter? = null
    var aryItem = ArrayList<Item>()

    var isInitialized = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile_item_list, container, false)

        // init list
        rootView.swiperefresh.setOnRefreshListener(this)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.list)

        val layoutManager = LinearLayoutManager(this.activity)
        recyclerView.setLayoutManager(layoutManager)

        this.adapter = ProfileItemAdapter(this.activity!!, this.aryItem)
        adapter?.deleteListener = this
        recyclerView.setAdapter(this.adapter)
        recyclerView.setItemAnimator(DefaultItemAnimator())

        return rootView
    }

    override fun onResume() {
        super.onResume()

        // refresh item list
        updateList()
    }

    /**
     * get User data
     */
    fun getItems(bRefresh: Boolean, bAnimation: Boolean) {

        if (bAnimation) {
            if (!this.swiperefresh.isRefreshing) {
                this.swiperefresh.isRefreshing = true
            }
        }

        val user = User.currentUser!!

        user.fetchItems(object: User.FetchDatabaseListener {
            override fun onFetchedReviews() {
            }

            override fun onFetchedUser(u: User?, success: Boolean) {
            }

            override fun onFetchedItems() {
                stopRefresh()

                updateList(bAnimation)
            }
        })

        isInitialized = true
    }

    private fun updateList(bAnimation: Boolean = false) {
        val user = User.currentUser!!

        if (bAnimation) {
            this@MainProfileItemFragment.adapter!!.notifyItemRangeRemoved(0, aryItem.count())
        }
        aryItem.clear()

        // add items
        if (arguments!!.getInt(ARG_ITEM_LIST_TYPE) == MainProfileFragment.ITEM_AVAILABLE) {
            for (item in user.items) {
                if (!item.taken) {
                    aryItem.add(item)
                }
            }
        }
        else {
            for (item in user.items) {
                if (item.taken) {
                    aryItem.add(item)
                }
            }
        }

        // show empty notice
        if (aryItem.isEmpty()) {
            text_empty_notice.visibility = View.VISIBLE
        }
        else {
            text_empty_notice.visibility = View.GONE
        }

        if (bAnimation) {
            this@MainProfileItemFragment.adapter!!.notifyItemRangeInserted(0, aryItem.count())
        }
        else {
            this@MainProfileItemFragment.adapter!!.notifyDataSetChanged()
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
        private const val ARG_ITEM_LIST_TYPE = "item_list_type"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(listType: Int): MainProfileItemFragment {
            val fragment = MainProfileItemFragment()
            val args = Bundle()
            args.putInt(ARG_ITEM_LIST_TYPE, listType)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onRefresh() {
        getItems(true, false)
    }

    //
    // ProfileItemAdapter.DeletePostListener
    //
    override fun onDeletedPost() {
        updateList()
    }
}