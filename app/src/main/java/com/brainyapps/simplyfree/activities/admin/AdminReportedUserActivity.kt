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
import java.util.*

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

        var countFound = 0
        var countFetched = 0

        val database = FirebaseDatabase.getInstance().reference.child(Report.TABLE_NAME)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                aryReport.clear()

                for (userItem in dataSnapshot.children) {
                    val reports = userItem as DataSnapshot
                    val userId = reports.key
                    var r: Report? = null

                    for (reportItem in reports.children) {
                        r = reportItem.getValue(Report::class.java)
                        r!!.id = reportItem.key

                        countFound++

                        break
                    }

                    // not found report
                    if (r == null) {
                        continue
                    }

                    // fetch its user
                    User.readFromDatabase(userId, object: User.FetchDatabaseListener {
                        override fun onFetchedUser(u: User?, success: Boolean) {
                            r.userReported = u
                            aryReport.add(r)

                            countFetched++

                            // if all notification users are fetched
                            if (countFound == countFetched) {
                                // sort
                                Collections.sort(aryReport, Collections.reverseOrder())

                                updateList()
                            }
                        }

                        override fun onFetchedItems() {
                        }

                        override fun onFetchedReviews() {
                        }
                    })
                }

                if (countFound == 0) {
                    updateList()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                stopRefresh()
            }
        })
    }

    fun updateList() {
        stopRefresh()
        adapter!!.notifyDataSetChanged()

        if (aryReport.isEmpty()) {
            this@AdminReportedUserActivity.text_empty_notice.visibility = View.VISIBLE
        }
        else {
            this@AdminReportedUserActivity.text_empty_notice.visibility = View.GONE
        }
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
