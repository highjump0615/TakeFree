package com.brainyapps.simplyfree.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.utils.FontManager
import kotlinx.android.synthetic.main.fragment_signup_board.view.*

/**
 * Created by Administrator on 3/26/18.
 */
class SignupOnboardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_signup_board, container, false)
        rootView.text_content.typeface = FontManager.getTypeface(context!!, FontManager.ENCHANTING)

        rootView.text_title.text = ""

        val index = this.arguments!!.getInt(ARG_BOARD_INDEX)
        if (index == 0) {
            rootView.text_title.text = "THERE'RE SEVERAL TYPES OF CATEGORIES:"
        }

        rootView.text_content.text = resources.getStringArray(R.array.onboard_contents)[index]

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_BOARD_INDEX = "index_board"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(index: Int): SignupOnboardFragment {
            val fragment = SignupOnboardFragment()
            val args = Bundle()
            args.putInt(ARG_BOARD_INDEX, index)
            fragment.arguments = args

            return fragment
        }
    }
}