package com.brainyapps.simplyfree.api

import android.net.Uri
import android.text.TextUtils
import okhttp3.*
import kotlin.collections.HashMap

/**
 * Created by Administrator on 3/14/18.
 */
object APIManager {

    val JSON = MediaType.parse("application/json; charset=utf-8")

    /**
     * API for sending fcm message
     */
    fun sendFcmMessage(jsonParam: String,
                       authorization: String?,
                       responseCallback: Callback) {

        sendToServiceByPost("https://fcm.googleapis.com/fcm/send", authorization, jsonParam, responseCallback)
    }

    /**
     * Send Post request
     */
    private fun sendToServiceByPost(serviceAPIURL: String,
                                    authorization: String?,
                                    jsonParam: String,
                                    responseCallback: Callback) {

        val requestBody = RequestBody.create(JSON, jsonParam)

        val requestBuilder = Request.Builder()
        if (!TextUtils.isEmpty(authorization)) {
            requestBuilder.addHeader("Authorization", authorization)
        }

        val request = requestBuilder
                .url(serviceAPIURL)
                .post(requestBody)
                .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(responseCallback)
    }
}