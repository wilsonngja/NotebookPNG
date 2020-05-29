package com.example.notebook

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import kotlinx.android.synthetic.main.camera_page.*
import java.lang.StringBuilder

class CameraActivity : AppCompatActivity() {
    private lateinit var svScanner: SurfaceView
    private lateinit var tvText: TextView

    private lateinit var cameraSource : CameraSource
    private lateinit var textRecognizer : TextRecognizer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_page)

        svScanner = findViewById(R.id.sv_scanner)
        tvText = findViewById(R.id.tv_text)

        textRecognizer = TextRecognizer.Builder(this).build()
        if (textRecognizer.isOperational){
            //Start Scanning
            startScanner()
            camID.setOnClickListener {
                copy2clipboard(tv_text.text.toString())
                Toast.makeText(this, "Text has been copied to clipboard as ", Toast.LENGTH_LONG).show()
                cameraSource.stop()
                textRecognizer.release()
                finish()
            }
        }else{
            Log.w("NOTE","Your text recognizer is not operational")
        }

    }

    fun copy2clipboard(text: CharSequence){
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("copy text", text)
        clipboard.setPrimaryClip(clip)
    }


    private fun startScanner() {
        cameraSource = CameraSource.Builder(this,textRecognizer)
            .setFacing(CameraSource.CAMERA_FACING_BACK).setRequestedFps(2.0f)
            .setRequestedPreviewSize(1024,768).setAutoFocusEnabled(true).build()

        svScanner.holder.addCallback(object: SurfaceHolder.Callback2 {
            override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {}
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                cameraSource.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                if (ActivityCompat.checkSelfPermission(this@CameraActivity,
                        android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraSource.start(holder)
                }
                else{
                    ActivityCompat.requestPermissions(this@CameraActivity, arrayOf(android.Manifest.permission.CAMERA),123)
                }

            }

        })
        textRecognizer.setProcessor(object: Detector.Processor<TextBlock>{
            override fun release() {}

            override fun receiveDetections(detection: Detector.Detections<TextBlock>?) {
                val textItems = detection?.detectedItems

                if (textItems != null) {
                    if (textItems.size() <= 0){
                        return
                    }
                }

                tvText.post {
                    val stringBuilder = StringBuilder()
                    if (textItems != null) {
                        for (i in 0 until textItems.size()) {
                            val item = textItems?.valueAt(i)
                            stringBuilder.append(item.value)
                            stringBuilder.append(" ")
                            println("textItem Size = " + textItems.size())
                        }
                    }
                    println("TVTEXT " + tvText.text)
                    tvText.text = stringBuilder.toString()
                }
            }

        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123 && grantResults.isNotEmpty()){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                cameraSource.start(svScanner.holder)
            } else Toast.makeText(this, "Scanner won't work without permission", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onDestroy(){
        super.onDestroy()
        textRecognizer.release()
        cameraSource.stop()
        cameraSource.release()
    }
}