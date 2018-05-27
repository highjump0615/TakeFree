package com.brainyapps.simplyfree.fragments.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.main.HomeActivity
import com.brainyapps.simplyfree.activities.main.ItemPostActivity
import com.brainyapps.simplyfree.adapters.main.HomeCategoryAdapter
import com.brainyapps.simplyfree.models.Category
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.utils.FontManager
import com.brainyapps.simplyfree.utils.Globals
import com.brainyapps.simplyfree.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_main_home.*
import kotlinx.android.synthetic.main.fragment_main_home.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MainHomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainHomeFragment : MainBaseFragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private val TAG = MainHomeFragment::class.java.getSimpleName()

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    var aryItemAll = ArrayList<Item>()
    var aryCategory = ArrayList<Category>()
    var adapter: HomeCategoryAdapter? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mParam1 = arguments?.getString(ARG_PARAM1)
        mParam2 = arguments?.getString(ARG_PARAM2)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewMain = inflater.inflate(R.layout.fragment_main_home, container, false)

        // set search font
        val iconFont = FontManager.getTypeface(activity!!, FontManager.FONTAWESOME)
        FontManager.markAsIconContainer(viewMain.text_search_mark, iconFont)

        viewMain.imgview_map.setOnClickListener(this)

        // init list
        val layoutManager = LinearLayoutManager(activity)
        viewMain.list.layoutManager = layoutManager

        this.adapter = HomeCategoryAdapter(activity!!, aryCategory)
        viewMain.list.adapter = this.adapter
        viewMain.list.itemAnimator = DefaultItemAnimator()

        viewMain.swiperefresh.setOnRefreshListener(this)

        viewMain.but_new.setOnClickListener(this)

        // search keyword
        viewMain.edit_search.addTextChangedListener(mTextWatcher)

        // load data
        Handler().postDelayed({ getItems(true, true) }, 500)

        // Inflate the layout for this fragment
        return viewMain
    }

    private val mTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override fun afterTextChanged(editable: Editable) {
            updateList()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onRefresh() {
        getItems(true, false)
    }

    /**
     * get User data
     */
    private fun getItems(bRefresh: Boolean, bAnimation: Boolean) {
        if (bAnimation) {
            if (!this.swiperefresh.isRefreshing) {
                this.swiperefresh.isRefreshing = true
            }
        }

        val database = FirebaseDatabase.getInstance().reference
        val query = database.child(Item.TABLE_NAME).orderByChild(Item.FIELD_TAKEN).equalTo(false)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // store all items
                aryItemAll.clear()
                for (iItem in dataSnapshot.children) {
                    val item = iItem.getValue(Item::class.java)
                    item!!.id = iItem.key

                    aryItemAll.add(item)
                }

                updateList(bAnimation)
            }

            override fun onCancelled(error: DatabaseError) {
                stopRefresh()
            }
        })
    }

    /**
     * get category from list, add new if not exist
     */
    private fun getCategoryFromIndex(index: Int): Category {
        var category: Category? = null

        for (cItem in aryCategory) {
            if (cItem.id == index) {
                category = cItem
                break
            }
        }

        if (category == null) {
            category = Globals.Categories[index].copy()
            aryCategory.add(category)
        }

        return category
    }

    /**
     * update the list with animation
     */
    fun updateList(bAnimation: Boolean = false) {

        val strSearch = edit_search.text.toString()

        stopRefresh()

        // clear
        if (bAnimation) {
            this@MainHomeFragment.adapter!!.notifyItemRangeRemoved(0, aryCategory.count())
        }
        aryCategory.clear()

        // categorize the items
        for (item in aryItemAll) {
            // add match-keyword only
            if (TextUtils.isEmpty(strSearch) || item.name.contains(strSearch, ignoreCase = true)) {
                val category = getCategoryFromIndex(item.category)
                category.items.add(item)
            }
        }

        // empty notice
        if (aryCategory.isEmpty()) {
            this@MainHomeFragment.text_empty_notice.visibility = View.VISIBLE
        }
        else {
            this@MainHomeFragment.text_empty_notice.visibility = View.GONE
        }

        stopRefresh()

        if (bAnimation) {
            this@MainHomeFragment.adapter!!.notifyItemRangeInserted(0, aryCategory.count())
        }
        else {
            this@MainHomeFragment.adapter!!.notifyDataSetChanged()
        }
    }

    fun stopRefresh() {
        this.swiperefresh.isRefreshing = false
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener : MainBaseFragment.OnFragmentInteractionListener {
        fun onHomeClickMap()
    }

    companion object {
        const val RC_ADD_ITEM = 2000
        const val KEY_ADD_ITEM = "added_item"

        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MainHomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): MainHomeFragment {
            val fragment = MainHomeFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imgview_map -> {
                if (mListener != null) {
                    mListener!!.onHomeClickMap()
                }
            }

            R.id.but_new -> {
                val intent = Intent(activity, ItemPostActivity::class.java)
                startActivityForResult(intent, RC_ADD_ITEM)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val bundle = data?.extras
        bundle?.let {
            val item = it.getParcelable<Item>(KEY_ADD_ITEM)

            onAddedItem(item)
        }
    }

    // add item listener
    private fun onAddedItem(item: Item) {
        aryItemAll.add(item)
        updateList()
    }

}// Required empty public constructor
