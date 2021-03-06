package com.brainyapps.simplyfree.utils

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import android.view.ViewGroup





/**
 * Created by Administrator on 3/24/18.
 */
object FontManager {

    val ROOT = "fonts/"
    val FONTAWESOME = ROOT + "fontawesome-webfont.ttf"
    val ENCHANTING = ROOT + "enchanting-celebrations.ttf"

    fun getTypeface(context: Context, font: String): Typeface {
        return Typeface.createFromAsset(context.getAssets(), font)
    }

    fun markAsIconContainer(v: View, typeface: Typeface) {
        if (v is ViewGroup) {
            for (i in 0 until v.childCount) {
                val child = v.getChildAt(i)
                markAsIconContainer(child, typeface)
            }
        } else if (v is TextView) {
            v.typeface = typeface
        }
    }
}