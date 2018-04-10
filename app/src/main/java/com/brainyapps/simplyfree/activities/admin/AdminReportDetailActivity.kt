package com.brainyapps.simplyfree.activities.admin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_admin_report_detail.*

class AdminReportDetailActivity : BaseActivity(), View.OnClickListener {

    companion object {
        val KEY_REPORT = "report"
        val REPORT_DETAIL_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_report_detail)

        setNavbar("Bill Watson", true)

        this.but_user.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.but_user -> {
                val intent = Intent(this, AdminUserDetailActivity::class.java)
//        intent.putExtra(UserDetailHelper.KEY_USER, user)
                startActivity(intent)
            }
        }
    }
}
