package com.brainyapps.simplyfree.activities.admin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.helpers.UserDetailHelper
import com.brainyapps.simplyfree.models.Report
import com.brainyapps.simplyfree.models.User
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_admin_report_detail.*

class AdminReportDetailActivity : BaseActivity(), View.OnClickListener {

    companion object {
        const val KEY_REPORT = "report"
        const val REPORT_DETAIL_CODE = 100
    }

    lateinit var report: Report
    lateinit var banHelper: AdminBanUserHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_report_detail)

        // get report from intent
        val bundle = intent.extras
        report = bundle?.getParcelable<Report>(KEY_REPORT)!!

        setNavbar(report.userReported!!.userFullName(), true)

        text_content.text = report.content
        but_delete.setOnClickListener(this)

        // fetch user info
        User.readFromDatabase(report.userId, object: User.FetchDatabaseListener {
            override fun onFetchedItems() {
            }

            override fun onFetchedNotifications() {
            }

            override fun onFetchedUser(user: User?, success: Boolean)
            {
                but_user.text = user?.userFullName()
                but_user.setOnClickListener(View.OnClickListener {
                    val intent = Intent(this@AdminReportDetailActivity, AdminUserDetailActivity::class.java)
                    intent.putExtra(UserDetailHelper.KEY_USER, user)
                    startActivity(intent)
                })
            }

            override fun onFetchedReviews() {
            }
        })

        banHelper = AdminBanUserHelper(this, report.userReported!!)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.but_delete -> {
                // remove from database
                report.userReported?.let {
                    val database = FirebaseDatabase.getInstance().reference.child(User.TABLE_NAME)
                    database.child(it.id).child(User.FIELD_REPORTS).child("0").removeValue()

                    setResult(1)
                    finish()
                }
            }
        }
    }
}
