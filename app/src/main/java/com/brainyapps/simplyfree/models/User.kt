package com.brainyapps.simplyfree.models

/**
 * Created by Administrator on 3/24/18.
 */
class User {

    companion object {
        val USER_TYPE_ADMIN = 0
        val USER_TYPE_CUSTOMER = 1

        var currentUser: User? = null
    }

    var type: Int = USER_TYPE_ADMIN
}