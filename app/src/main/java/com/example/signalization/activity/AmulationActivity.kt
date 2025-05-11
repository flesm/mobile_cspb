package com.example.signalization.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.signalization.R
import com.example.signalization.network.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class AmulationActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var btnSelectPhoto: Button
    private lateinit var btnUpload: Button
    private lateinit var etDistance: EditText

    private var selectedImageFile: File? = null

    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageView.setImageURI(it)
                createFileFromUri(it)?.let { file ->
                    selectedImageFile = file
                    Log.d("Amulation", "Selected image: ${file.absolutePath}")
                }
            }
        }

    private fun createFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "upload_${System.currentTimeMillis()}.jpg")
            val outputStream = file.outputStream()
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun uploadImage(imageFile: File, distanceValue: String) {
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
        val distance = distanceValue.toRequestBody("text/plain".toMediaTypeOrNull())

        RetrofitInstance.apiService.uploadPeopleImage(imagePart, distance).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AmulationActivity, "Upload successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@AmulationActivity, "Upload failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@AmulationActivity, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.amulation_activity)

        imageView = findViewById(R.id.imageViewPreview)
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto)
        btnUpload = findViewById(R.id.btnUploadImage)
        etDistance = findViewById(R.id.etDistance)

        btnSelectPhoto.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        btnUpload.setOnClickListener {
            val distance = etDistance.text.toString()
            val file = selectedImageFile
            if (distance.isBlank() || file == null) {
                Toast.makeText(this, "Введите дистанцию и выберите фото", Toast.LENGTH_SHORT).show()
            } else {
                uploadImage(file, distance)
            }
        }
    }
}
