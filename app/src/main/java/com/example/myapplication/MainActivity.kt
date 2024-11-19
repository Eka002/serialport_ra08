package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import tp.xmaihh.serialport.SerialHelper
import tp.xmaihh.serialport.bean.ComBean
import android.util.Base64
import org.json.JSONObject
import java.nio.charset.Charset

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var resultTextView: TextView
    private lateinit var serialHelper: SerialHelper

    companion object {
        private const val SERIAL_PORT_PATH = "/dev/ttyS3"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultTextView = findViewById(R.id.textView2)
        serialHelper = object : SerialHelper(SERIAL_PORT_PATH, 115200) {

            @SuppressLint("SetTextI18n")
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
                                val jwt = receiveData
                                val publicKey = "Ваш публичный ключ"

                                val headerJson = JSONObject(header).toString(4)
                                val payloadJson = JSONObject(payload).toString(4)

                                resultTextView.text = """
                                    Sample App For Generate JWT With
                                    Algorithm: EC-256
                                    HeaderJson: $headerJson
                                    PayloadJson: $payloadJson
                                    JWT: $jwt
                                    PublicKey: $publicKey
                                """.trimIndent()
                            } else {
                                resultTextView.text = "Данные: $receiveData не соответсвуют кодировке JWT"
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
