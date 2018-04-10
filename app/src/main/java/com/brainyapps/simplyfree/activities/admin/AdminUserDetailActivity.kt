package com.brainyapps.simplyfree.activities.admin

import android.os.Bundle
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity

class AdminUserDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_user_detail)

        setNavbar("Bill Watson", true)
    }
}
