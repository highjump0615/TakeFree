package com.brainyapps.simplyfree.fragments.main

import android.support.v4.app.Fragment

/**
 * Created by Administrator on 4/12/18.
 */
open class MainBaseFragment() : Fragment() {

    open fun getInteractionListener() : OnFragmentInteractionListener? {
        return null
    }

    interface OnFragmentInteractionListener  {
    }

}