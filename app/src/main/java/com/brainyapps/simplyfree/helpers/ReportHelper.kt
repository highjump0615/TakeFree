package com.brainyapps.simplyfree.helpers

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.widget.EditText
import android.widget.Toast
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.Report
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Utils

/**
 * Created by Administrator on 5/21/18.
 */
class ReportHelper(val owner: Activity) {
    init {
    }

    fun addReport(user: User) {
        Utils.createEditDialog(owner,
                "Report",
                "Text content about report",
                DialogInterface.OnClickListener { dialog, which ->
                    val dlg = dialog as AlertDialog
                    val editContent = dlg.findViewById<EditText>(R.id.edit_content)

                    submitReport(editContent.text.toString(), user)
                })
                .show()
    }

    private fun submitReport(content: String, user: User) {
        // add report
        val newReport = Report()
        newReport.userId = User.currentUser!!.id
        newReport.user = User.currentUser
        newReport.content = content

        user.reports.add(newReport)
        user.saveToDatabaseChild(User.FIELD_REPORTS, user.reports)

        Toast.makeText(owner, "Reported successfully", Toast.LENGTH_SHORT).show()
    }
}