package com.brainyapps.simplyfree.fragments.main

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.adapters.main.MessageListAdapter
import com.brainyapps.simplyfree.models.Message
import kotlinx.android.synthetic.main.fragment_main_message.*
import kotlinx.android.synthetic.main.fragment_main_message.view.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MainMessageFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainMessageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainMessageFragment : MainBaseFragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private val TAG = MainMessageFragment::class.java.getSimpleName()

    var aryMessage = ArrayList<Message>()
    var adapter: MessageListAdapter? = null

    var mListener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val viewMain = inflater.inflate(R.layout.fragment_main_message, container, false)

        // init list
        val layoutManager = LinearLayoutManager(activity)
        viewMain.list.setLayoutManager(layoutManager)

        viewMain.list.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))

        this.adapter = MessageListAdapter(activity!!, this.aryMessage)
        viewMain.list.setAdapter(this.adapter)
        viewMain.list.setItemAnimator(DefaultItemAnimator())

        viewMain.swiperefresh.setOnRefreshListener(this)

        // load data
        Handler().postDelayed({ getMessages(true, true) }, 500)

        // Inflate the layout for this fragment
        return viewMain
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onRefresh() {
        getMessages(true, false)
    }

    /**
     * get Message data
     */
    private fun getMessages(bRefresh: Boolean, bAnimation: Boolean) {
        if (bAnimation) {
            if (!this.swiperefresh.isRefreshing) {
                this.swiperefresh.isRefreshing = true
            }
        }

        // clear
        if (bAnimation) {
            this@MainMessageFragment.adapter!!.notifyItemRangeRemoved(0, aryMessage.count())
        }
        aryMessage.clear()

        // add
        for (i in 0..10) {
            val data = Message()

            aryMessage.add(data)
        }

        updateList(bAnimation)

        if (aryMessage.isEmpty()) {
            this@MainMessageFragment.text_empty_notice.visibility = View.VISIBLE
        }
    }

    private fun updateList(bAnimation: Boolean) {
        stopRefresh()

        if (bAnimation) {
            this@MainMessageFragment.adapter!!.notifyItemRangeInserted(0, aryMessage.count())
        }
        else {
            this@MainMessageFragment.adapter!!.notifyDataSetChanged()
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
    }

    companion object {
    }

    override fun onClick(view: View?) {
        when (view?.id) {

        }
    }

    override fun getInteractionListener() = mListener

}// Required empty public constructor
