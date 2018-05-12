package com.brainyapps.simplyfree.utils

import android.content.Context
import android.location.Location
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.Category

/**
 * Created by Administrator on 5/11/18.
 */
object Globals {
    var mLocation: Location? = null

    var Categories = ArrayList<Category>()

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