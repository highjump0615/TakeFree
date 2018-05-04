package com.brainyapps.simplyfree.activities.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.activities.PhotoActivityHelper
import kotlinx.android.synthetic.main.activity_item_post.*

class ItemPostActivity : BaseActivity(), View.OnClickListener {

    var helper: PhotoActivityHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_post)

        setNavbar("Post New Item", true)

        // init category spinner
        val adapterCategory = ArrayAdapter.createFromResource(this, R.array.item_category_array, android.R.layout.simple_spinner_item)
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.spinner_category.adapter = adapterCategory

        // init condition spinner
        val adapterCondition = ArrayAdapter.createFromResource(this, R.array.item_condition_array, android.R.layout.simple_spinner_item)
        adapterCondition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.spinner_condition.adapter = adapterCondition

        this.but_post.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.but_post -> {
                finish()
            }
        }
    }
}
