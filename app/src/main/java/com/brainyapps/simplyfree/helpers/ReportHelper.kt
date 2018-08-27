package com.brainyapps.simplyfree.helpers

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.models.Report
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Utils
import kotlinx.android.synthetic.main.layout_dialog_edit.view.*

/**
 * Created by Administrator on 5/21/18.
 */
class ReportHelper(private val owner: Activity) {
    init {
    }

    fun addReport(user: User) {

        // show edit dialog
        val builder = AlertDialog.Builder(owner)
        val inflater = owner.layoutInflater
        val view = inflater.inflate(R.layout.layout_dialog_edit, null)

        // placeholder
        view.edit_content.hint = "Text content about report"

        val dialog = builder.setTitle("Report")
                .setView(view)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(true)
                .create()

        dialog.setOnShowListener {
            val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val editContent = dialog.findViewById<EditText>(R.id.edit_content)

                if (submitReport(editContent.text.toString(), user)) {
                    //Dismiss once everything is OK.
                    dialog.dismiss()
                }
                else {
                    Toast.makeText(owner, "Report content cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }

    private fun submitReport(content: String, user: User) : Boolean {
        if (Utils.isStringEmpty(content)) {
            return false
        }

        // add report
        val newReport = Report()
        newReport.userId = User.currentUser!!.id
        newReport.user = User.currentUser
        newReport.content = content

        newReport.saveToDatabase(parent = user.id)

        Toast.makeText(owner, "Reported successfully", Toast.LENGTH_SHORT).show()

        return true
    }
}