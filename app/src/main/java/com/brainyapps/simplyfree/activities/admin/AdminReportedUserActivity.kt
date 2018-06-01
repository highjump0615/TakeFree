package com.brainyapps.simplyfree.activities.admin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.adapters.admin.ReportItemAdapter
import com.brainyapps.simplyfree.models.BaseModel
import com.brainyapps.simplyfree.models.Report
import com.brainyapps.simplyfree.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_admin_reported_user.*
import java.util.ArrayList

class AdminReportedUserActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {

    var aryReport = ArrayList<Report>()

    var adapter: ReportItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_reported_user)

        setNavbar("Reported User", true)

        // init list
        val recyclerView = findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(this)

        this.adapter = ReportItemAdapter(this, this.aryReport)
        recyclerView.adapter = this.adapter
        recyclerView.itemAnimator = DefaultItemAnimator()

        this.swiperefresh.setOnRefreshListener(this)

        // load data
        Handler().postDelayed({ getReports(true, true) }, 500)
    }

    override fun onRefresh() {
        getReports(true, false)
    }

    fun stopRefresh() {
        this.swiperefresh.isRefreshing = false
    }

    /**
     * get User data
     */
    private fun getReports(bRefresh: Boolean, bAnimation: Boolean) {

        if (bAnimation) {
            if (!this.swiperefresh.isRefreshing) {
                this.swiperefresh.isRefreshing = true
            }
        }

        val database = FirebaseDatabase.getInstance().reference
        val query = database.child(User.TABLE_NAME).orderByChild(BaseModel.FIELD_DATE)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                aryReport.clear()

                for (reportItem in dataSnapshot.children) {
                    val u = reportItem.getValue(User::class.java)
                    u?.id = reportItem.key
                    if (u?.reports!!.isEmpty()) {
                        continue
                    }

                    val report = u.reports[0]
                    report.userReported = u
                    aryReport.add(report)
                }

                updateList()

                if (aryReport.isEmpty()) {
                    this@AdminReportedUserActivity.text_empty_notice.visibility = View.VISIBLE
                }
                else {
                    this@AdminReportedUserActivity.text_empty_notice.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                stopRefresh()
            }
        })
    }

    fun updateList() {
        stopRefresh()
        this@AdminReportedUserActivity.adapter!!.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AdminReportDetailActivity.REPORT_DETAIL_CODE) {
            if (resultCode == 1) {
                // deleted report, refresh list
                getReports(true, false)
            }
        }
    }
}
