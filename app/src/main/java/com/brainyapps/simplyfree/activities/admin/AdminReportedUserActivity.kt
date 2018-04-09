package com.brainyapps.simplyfree.activities.admin

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
import com.brainyapps.simplyfree.models.Report
import com.brainyapps.simplyfree.models.User
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

        val layoutManager = LinearLayoutManager(this)
        recyclerView.setLayoutManager(layoutManager)

        this.adapter = ReportItemAdapter(this, this.aryReport)
        recyclerView.setAdapter(this.adapter)
        recyclerView.setItemAnimator(DefaultItemAnimator())

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

//        val database = FirebaseDatabase.getInstance().reference
//        val query = database.child(Report.TABLE_NAME).orderByChild(BaseModel.FIELD_DATE)
//
//        query.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//
                aryReport.clear()
                for (i in 0..20) {
                    aryReport.add(Report())
                }
//                for (reportItem in dataSnapshot.children) {
//                    val report = reportItem.getValue(Report::class.java)
//                    report?.id = reportItem.key
//
//                    // check existence
//                    var bExist = false
//                    for (r in aryReport) {
//                        if (TextUtils.equals(r.userReportedId, report!!.userReportedId)) {
//                            bExist = true
//                            break
//                        }
//                    }
//
//                    if (bExist) {
//                        continue
//                    }
//
//                    aryReport.add(report!!)
//
//                    // fetch user
//                    User.readFromDatabase(report.userReportedId, mFetchUserListner)
//                }
//
                updateList()

                if (aryReport.isEmpty()) {
                    this@AdminReportedUserActivity.text_empty_notice.visibility = View.VISIBLE
                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                stopRefresh()
//            }
//        })
    }

    fun updateList() {
        stopRefresh()
        this@AdminReportedUserActivity.adapter!!.notifyDataSetChanged()
    }
}
