package com.example.myapplication

import androidx.test.espresso.Espresso.onView
import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.core.app.ActivityScenario
import org.hamcrest.CoreMatchers.containsString
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class JwtScanTest {

    private fun decodeBase64Url(encoded: String): String {
        val base64Url = encoded.replace("-", "+").replace("__", "/")
        val decodedBytes = Base64.decode(base64Url, Base64.DEFAULT)
        return String(decodedBytes, Charsets.UTF_8)
    }
    @Test
    fun testJwtScanning() {
        val latch = CountDownLatch(1)
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            val jwtSample = "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"

            val parts = jwtSample.split(".")
            val headerJson = decodeBase64Url(parts[0])
            val payloadJson = decodeBase64Url(parts[1])

            val headerObject = JSONObject(headerJson)
            val alg = headerObject.getString("alg").lowercase(Locale.getDefault())

            val payloadObject = JSONObject(payloadJson)
            val name = payloadObject.getString("name")
            val sub = payloadObject.getString("sub")

            scenario.onActivity { activity ->
                activity.resultTextView.text = """
                Alg: $alg
                Sub: $sub
                Name: $name
                JWT: $jwtSample
            """.trimIndent()
            }

            onView(withId(R.id.textView2)).check(matches(withText(containsString(alg))))
            onView(withId(R.id.textView2)).check(matches(withText(containsString(sub))))
            onView(withId(R.id.textView2)).check(matches(withText(containsString(name))))
            onView(withId(R.id.textView2)).check(matches(withText(containsString(jwtSample))))

            try {
                latch.await(30, TimeUnit.SECONDS)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}

