package com.example.signalization.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.signalization.R
import com.example.signalization.LastUndecidedLoader

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val container = findViewById<LinearLayout>(R.id.llLastAlertContainer)

        LastUndecidedLoader(this, layoutInflater, container).load()


        findViewById<Button>(R.id.btnAllAlerts).setOnClickListener {
            startActivity(Intent(this, AllRecordsActivity::class.java))
        }

        findViewById<Button>(R.id.btnAddEmployee).setOnClickListener {
            startActivity(Intent(this, StuffActivity::class.java))
        }

        findViewById<Button>(R.id.btnSimulateIntrusion).setOnClickListener {
            startActivity(Intent(this, AmulationActivity::class.java))
        }

    }
}


//class MainActivity : AppCompatActivity() {
//
//    private lateinit var llContainer: LinearLayout
//    private lateinit var imageView: ImageView
//    private lateinit var btnSelectPhoto: Button
//    private lateinit var btnSendPhotoWithName: Button
//    private lateinit var etFullName: EditText
//    private var selectedImageFile: File? = null
//
//    private fun createFileFromUri(uri: Uri): File? {
//        return try {
//            val inputStream: InputStream? = contentResolver.openInputStream(uri)
//            val file = File(cacheDir, "upload_${System.currentTimeMillis()}.jpg")
//            val outputStream: OutputStream = file.outputStream()
//            inputStream?.copyTo(outputStream)
//            inputStream?.close()
//            outputStream.close()
//            file
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    private val selectImageLauncher =
//        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//            uri?.let {
//                imageView.setImageURI(it)
//                createFileFromUri(it)?.let { file ->
//                    selectedImageFile = file
//                    Log.d("DEBUG", "Selected image file: ${file.absolutePath}")
//                }
//            }
//        }
//
//    private fun uploadImage(imageFile: File, distanceValue: String) {
//        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
//
//        // Ключ должен быть "image", а не "file"
//        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
//
//        val distance = distanceValue.toRequestBody("text/plain".toMediaTypeOrNull())
//
//        val call = RetrofitInstance.apiService.uploadPeopleImage(imagePart, distance)
//
//        call.enqueue(object : retrofit2.Callback<ResponseBody> {
//            override fun onResponse(
//                call: Call<ResponseBody>,
//                response: retrofit2.Response<ResponseBody>
//            ) {
//                if (response.isSuccessful) {
//                    val result = response.body()?.string()
//                    Log.d("Upload", "Success: $result")
//                } else {
//                    Log.e("Upload", "Failed: ${response.code()} ${response.errorBody()?.string()}")
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Log.e("Upload", "Error: ${t.localizedMessage}")
//            }
//        })
//    }
//
//
//    @SuppressLint("CutPasteId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        val btnGetAllRecords: Button      = findViewById(R.id.btnGetAllRecords)
//        val btnGetLastUndecided: Button  = findViewById(R.id.btnGetLastUndecided)
//        val etDistance: EditText = findViewById(R.id.etDistance)
//        val btnUploadImage: Button = findViewById(R.id.btnUploadImage)
//
//        llContainer = findViewById(R.id.llContainer)
//
//        imageView = findViewById(R.id.imageViewPreview)
//        btnSelectPhoto = findViewById(R.id.btnSelectPhoto)
//        btnSendPhotoWithName = findViewById(R.id.btnSendPhotoWithName)
//        etFullName = findViewById(R.id.etFullName)
//
//        btnSelectPhoto.setOnClickListener {
//            selectImageLauncher.launch("image/*")
//        }
//
//        val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//            uri?.let {
//                val imageFile = createFileFromUri(it)
//                val distanceValue = findViewById<EditText>(R.id.etDistance).text.toString()
//                if (imageFile != null && distanceValue.isNotBlank()) {
//                    uploadImage(imageFile, distanceValue)
//                } else {
//                    Toast.makeText(this, "Missing image or distance", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//        btnUploadImage.setOnClickListener {
//            val distanceText = etDistance.text.toString()
//            if (distanceText.isBlank()) {
//                Toast.makeText(this, "Please enter a distance", Toast.LENGTH_SHORT).show()
//            } else {
//                imagePickerLauncher.launch("image/*")
//            }
//        }
//
//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
//                return@addOnCompleteListener
//            }
//
//            // Получили токен
//            val token = task.result
//            Log.d("FCM", "FCM Token (manual fetch): $token")
//        }
