package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import tp.xmaihh.serialport.SerialHelper
import tp.xmaihh.serialport.bean.ComBean
import android.util.Base64
import java.nio.charset.Charset

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView
    private lateinit var serialHelper: SerialHelper

    companion object {
        private const val SERIAL_PORT_PATH = "/dev/ttyS3"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultTextView = findViewById(R.id.textView2)
        serialHelper = object : SerialHelper(SERIAL_PORT_PATH, 115200) {

            override fun onDataReceived(p0: ComBean?) {
                var receiveData = p0?.bRec?.let { String(it) } ?: ""
                Log.d(TAG, "onDataReceived: $receiveData")
                runOnUiThread {
                    try {
                        if (receiveData.isNotEmpty()) {
                            val parts = receiveData.split(".")
                            if (parts.size == 3) {
                                val header = decodeBase64Url(parts[0])
                                val payload = decodeBase64Url(parts[1])

                                resultTextView.text = "Header: $header\n\nPayload: $payload"
                            } else {
                                resultTextView.text = "Данные: $receiveData"
                            }
                        } else {
                            resultTextView.text = "Нет данных для отображения"
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Ошибка при обработке данных: ${e.message}")
                        resultTextView.text = "Ошибка: ${e.message}"
                    }

                }
            }
            private fun decodeBase64Url(encoded: String): String {
                val base64Url = encoded.replace("-", "+").replace("__", "/")
                val decodedBytes = Base64.decode(base64Url, Base64.DEFAULT)
                return String(decodedBytes, Charset.forName("UTF-8"))
            }
        }
        serialHelper.open()
    }
}
