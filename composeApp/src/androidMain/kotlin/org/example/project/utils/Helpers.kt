package org.example.project.utils

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.example.project.utils.ShiftCodec.DM
import java.io.IOException
import java.net.URLDecoder
import java.util.Locale

fun decodeUtf8(encoded: String?): String =
    URLDecoder.decode(encoded, "UTF-8")

fun requestNotify(registry: ActivityResultRegistry) {
    val launcher = registry.register(
        "requestPermissionKey",
        ActivityResultContracts.RequestPermission()
    ) {  }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

fun regToken() {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val fcmToken: String =
                runCatching { FirebaseMessaging.getInstance().token.await() }
                    .getOrElse { "null" }
            val locale = Locale.getDefault().toLanguageTag()
            val url = "${ShiftCodec.decode(DM)}/naxmo/"
            val client = OkHttpClient()

            val fullUrl = "$url?" +
                    "y9i2r0s5=${Firebase.analytics.appInstanceId.await()}" +
                    "&kv1740nv=${decodeUtf8(fcmToken)}"

            val request = Request.Builder().url(fullUrl)
                .addHeader("Accept-Language", locale)
                .get().build()


            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response) {
                    response.close()
                }
            })
        } catch (exc: Exception) {}
    }
}

fun postback(intent: Intent) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val trackingId = intent.getStringExtra("trackingId")
//            Log.d("MYTAG", "trackingId = $trackingId")

            if (trackingId.isNullOrEmpty()) {
                return@launch
            }

            val fcmToken: String =
                runCatching { FirebaseMessaging.getInstance().token.await() }
                    .getOrElse { "null" }

            val url = "${ShiftCodec.decode(DM)}/dqrat91fo/"
            val client = OkHttpClient()

            val fullUrl = "$url?" +
                    "egfo4gvrw=$trackingId" +
                    "&gcg3s14vst=${decodeUtf8(fcmToken)}"

            val request = Request.Builder()
                .url(fullUrl)
                .get()
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                override fun onResponse(call: Call, response: Response) {
                    response.close()
                }
            })

        } catch (exc: Exception) {
        }
    }
}