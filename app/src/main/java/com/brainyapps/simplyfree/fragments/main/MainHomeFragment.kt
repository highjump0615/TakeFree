package com.brainyapps.simplyfree.fragments.main

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.adapters.main.HomeCategoryAdapter
import com.brainyapps.simplyfree.models.Category
import com.brainyapps.simplyfree.utils.FontManager
import kotlinx.android.synthetic.main.fragment_main_home.*
import kotlinx.android.synthetic.main.fragment_main_home.view.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MainHomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainHomeFragment : Fragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    var aryCategory = ArrayList<Category>()
    var adapter: HomeCategoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val viewMain = inflater!!.inflate(R.layout.fragment_main_home, container, false)

        // set search font
        val iconFont = FontManager.getTypeface(activity, FontManager.FONTAWESOME)
        FontManager.markAsIconContainer(viewMain.text_search_mark, iconFont)

        viewMain.imgview_map.setOnClickListener(this)

        // init list
        val layoutManager = LinearLayoutManager(activity)
        viewMain.list.setLayoutManager(layoutManager)

        this.adapter = HomeCategoryAdapter(activity, this.aryCategory)
        viewMain.list.setAdapter(this.adapter)
        viewMain.list.setItemAnimator(DefaultItemAnimator())

        viewMain.swiperefresh.setOnRefreshListener(this)

        // load data
        Handler().postDelayed({ getItems(true, true) }, 500)

        // Inflate the layout for this fragment
        return viewMain
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

        // clear
        if (bAnimation) {
            this@MainHomeFragment.adapter!!.notifyItemRangeRemoved(0, aryCategory.count())
        }
        aryCategory.clear()


        // add
        for (i in 0..10) {
            aryCategory.add(Category())
        }

        updateList(bAnimation)

        if (aryCategory.isEmpty()) {
            this@MainHomeFragment.text_empty_notice.visibility = View.VISIBLE
        }
    }

    fun updateList(bAnimation: Boolean) {
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
    interface OnFragmentInteractionListener {
        fun onHomeClickMap()
    }

    companion object {
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
        }
    }

}// Required empty public constructor
