package com.example.myapplication

import android.Manifest
//import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.zxing.integration.android.IntentIntegrator
//import com.google.zxing.integration.android.IntentResult
import tp.xmaihh.serialport.SerialHelper
import tp.xmaihh.serialport.bean.ComBean

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView
    private lateinit var scanButton: Button
    private lateinit var serialHelper: SerialHelper

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
        private const val SERIAL_PORT_PATH = "/dev/ttyS3" // Путь к COM-порту
        // private const val SERIAL_PORT_BAUDRATE = 115200 // Скорость передачи
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        serialHelper = object : SerialHelper(SERIAL_PORT_PATH, 115200) {
            override fun onDataReceived(p0: ComBean?) {
                val receiveData = java.lang.String(p0?.bRec)
                Log.d(TAG, "onDataReceived: $receiveData")
                runOnUiThread {
                    resultTextView.text = receiveData
                }

            }
        }
        serialHelper.open()

        resultTextView = findViewById(R.id.textView2)
        scanButton = findViewById(R.id.button)

        scanButton.setOnClickListener {
            if (checkCameraPermission()) {
                startQRScanner()
            } else {
                requestCameraPermission()
            }
        }

    }

    private fun checkCameraPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRScanner()
            } else {
                Toast.makeText(this, "Разрешение камеры не предоставлено", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun startQRScanner() {
        val integrator = IntentIntegrator(this).apply {
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            setPrompt("Сканируйте QR-код")
            setCameraId(0)
            setOrientationLocked(true)
            setBeepEnabled(true)
        }
        integrator.initiateScan()
    }
}
