package com.brainyapps.simplyfree.utils

import android.app.Activity
import android.view.View

/**
 * Created by Administrator on 2/19/18.
 */

interface SFUpdateImageListener {
    fun getActivity(): Activity
    fun updatePhotoImageView(byteData: ByteArray)
}
