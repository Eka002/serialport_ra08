package com.example.myapplication

import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.core.app.ActivityScenario
import org.junit.matchers.JUnitMatchers.containsString

@RunWith(AndroidJUnit4::class)
class JwtScanTest {

    private fun decodeBase64Url(encoded: String): String {
        val base64Url = encoded.replace("-", "+").replace("__", "/")
        val decodedBytes = Base64.decode(base64Url, Base64.DEFAULT)
        return String(decodedBytes, Charsets.UTF_8)
    }

    @Suppress("DEPRECATION")
    @Test
    fun testJwtScanning() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            val jwtSample = "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
            val parts = jwtSample.split(".")

            val headerJson = decodeBase64Url(parts[0])
            val payloadJson = decodeBase64Url(parts[1])

            scenario.onActivity { activity ->
                activity.resultTextView.text = """
                    Sample App For Generate JWT With
                    Algorithm: EC-256
                    HeaderJson: ${JSONObject(headerJson).toString(4)}
                    PayloadJson: ${JSONObject(payloadJson).toString(4)}
                    JWT: $jwtSample
                    PublicKey: Ваш публичный ключ
                """.trimIndent()
            }
            onView(withId(R.id.textView2)).check(matches(withText(containsString("Sample App For Generate JWT With"))))
            onView(withId(R.id.textView2)).check(matches(withText(containsString("Algorithm: EC-256"))))
            onView(withId(R.id.textView2)).check(matches(withText(containsString("HeaderJson:"))))
            onView(withId(R.id.textView2)).check(matches(withText(containsString("PayloadJson:"))))
            onView(withId(R.id.textView2)).check(matches(withText(containsString(jwtSample))))
            onView(withId(R.id.textView2)).check(matches(withText(containsString("PublicKey: Ваш публичный ключ"))))
        }
    }
}
