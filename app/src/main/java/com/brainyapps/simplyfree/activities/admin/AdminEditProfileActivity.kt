package com.brainyapps.simplyfree.activities.admin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.brainyapps.simplyfree.R
import com.brainyapps.simplyfree.activities.BaseActivity
import com.brainyapps.simplyfree.activities.BaseEditProfileActivity
import com.brainyapps.simplyfree.activities.LoginBaseActivity
import com.brainyapps.simplyfree.models.User
import com.brainyapps.simplyfree.utils.Utils
import kotlinx.android.synthetic.main.activity_admin_edit_profile.*

class AdminEditProfileActivity : BaseEditProfileActivity(), View.OnClickListener  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_edit_profile)

        setNavbar("Edit Profile", true)

        initUserInfo()

        this.but_save.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.but_save -> {
                if (!checkPasswordValidate()) {
                    return
                }

                if (TextUtils.isEmpty(editPassword!!.text.toString())) {
                    finish()
                    return
                }

                Utils.createProgressDialog(this, "Updating Profile", "Saving your user information")

                savePassword(object: PasswordSaveListener {
                    override fun onSavedPassword(success: Boolean) {
                        Utils.closeProgressDialog()

                        if (success) {
                            finish()
                        }
                    }
                })
            }
        }
    }
}
