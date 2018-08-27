package com.brainyapps.simplyfree.views.main

import android.content.Context
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.utils.FontManager
import kotlinx.android.synthetic.main.layout_main_app_bar.view.*

/**
 * Created by Administrator on 4/10/18.
 */
class ViewToolbar : Toolbar {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    fun init() {
        View.inflate(context, R.layout.layout_main_app_bar, this)

        // font
        text_toolbar_title.typeface = FontManager.getTypeface(context, FontManager.ENCHANTING)

        setContentInsetsAbsolute(0, 0)
    }
}