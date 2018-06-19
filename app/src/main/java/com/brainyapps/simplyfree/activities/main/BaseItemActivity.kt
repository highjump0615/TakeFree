package com.brainyapps.simplyfree.activities.main

import android.widget.Toast
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.models.Item

/**
 * Created by Administrator on 6/20/18.
 */
open class BaseItemActivity : BaseActivity() {

    var item: Item? = null

    /**
     * check if item has been deleted
     */
    fun checkItemRemoval(): Boolean {
        if (item?.deletedAt != null) {
            Toast.makeText(this, "The item has already been deleted", Toast.LENGTH_LONG).show()
            return true
        }

        return false
    }

}