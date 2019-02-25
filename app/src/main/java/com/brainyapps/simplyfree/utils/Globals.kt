package com.brainyapps.simplyfree.utils

import android.content.Context
import android.location.Location
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.main.HomeActivity
import com.brainyapps.simplyfree.models.Category
import com.brainyapps.simplyfree.models.Item
import com.brainyapps.simplyfree.models.Notification
import com.firebase.geofire.GeoLocation

/**
 * Created by Administrator on 5/11/18.
 */
object Globals {
    var mLocation: Location? = null

    var Categories = ArrayList<Category>()
    var selectedItem: Item? = null
    var selectedNotification: Notification? = null
    var isBackToRoot = false

    var activityMain: HomeActivity? = null
    var hasNewNotification = false
    var hasNewMessage = false

    val items = ArrayList<Item>()

    fun initCategories(ctx: Context) {
        val aryCategoryName = ctx.resources.getStringArray(R.array.item_category_array);

        var nIndex = 0
        for (categoryName in aryCategoryName) {
            val categoryNew = Category(nIndex, categoryName)
            Categories.add(categoryNew)

            nIndex++
        }
    }

    fun clearItems() {
        items.clear()
    }

    fun addItem(data: Item?, location: GeoLocation?) {
        if (data == null) {
            return
        }

        if (data.deletedAt != null) {
            // deleted item, skip it
            return
        }

        data.location = location

        items.add(data)
    }

    fun addNewItem(data: Item) {
        mLocation?.let {
            data.location = GeoLocation(it.latitude, it.longitude)
        }

        Globals.items.add(0, data)
    }
}