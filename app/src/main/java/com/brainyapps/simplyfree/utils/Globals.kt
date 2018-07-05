package com.brainyapps.simplyfree.utils

import android.content.Context
import android.location.Location
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.Category
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.models.Notification

/**
 * Created by Administrator on 5/11/18.
 */
object Globals {
    var mLocation: Location? = null

    var Categories = ArrayList<Category>()
    var selectedItem: Item? = null
    var selectedNotification: Notification? = null
    var isBackToRoot = false

    fun initCategories(ctx: Context) {
        val aryCategoryName = ctx.resources.getStringArray(R.array.item_category_array);

        var nIndex = 0
        for (categoryName in aryCategoryName) {
            val categoryNew = Category(nIndex, categoryName)
            Categories.add(categoryNew)

            nIndex++
        }
    }
}