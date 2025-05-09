package com.example.signalization

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import java.net.HttpURLConnection
import java.net.URL

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "FCM Token: $token")

        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        val url = URL("https://yourserver.com/api/save_fcm_token/")

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            doOutput = true

            val json = """{"token": "$token"}"""

            outputStream.write(json.toByteArray())
            outputStream.flush()
            outputStream.close()

            Log.d("FCM", "Token sent, response: $responseCode")
        }
    }

}
