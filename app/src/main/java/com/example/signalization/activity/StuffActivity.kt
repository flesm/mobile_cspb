package com.example.signalization.activity

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.signalization.R
import com.example.signalization.network.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.File
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StuffActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var btnSelectPhoto: Button
    private lateinit var btnSendPhotoWithName: Button
    private lateinit var etFullName: EditText
    private var selectedImageFile: File? = null

    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageView.setImageURI(it)
                createFileFromUri(it)?.let { file ->
                    selectedImageFile = file
                    Log.d("StuffActivity", "Selected image file: ${file.absolutePath}")
                }
            }
        }

    private fun createFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "stuff_upload_${System.currentTimeMillis()}.jpg")
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

    private fun uploadStuffImage(imageFile: File, fullNameValue: String) {
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
        val fullName = fullNameValue.toRequestBody("text/plain".toMediaTypeOrNull())

        val call = RetrofitInstance.apiService.uploadStuffImage(imagePart, fullName)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()?.string()
                    Log.d("StuffUpload", "Success: $result")
                    Toast.makeText(this@StuffActivity, "Успешно отправлено", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("StuffUpload", "Failed: ${response.code()} ${response.errorBody()?.string()}")
                    Toast.makeText(this@StuffActivity, "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("StuffUpload", "Error: ${t.localizedMessage}")
                Toast.makeText(this@StuffActivity, "Сбой подключения", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stuff_activity)

        imageView = findViewById(R.id.imageViewPreview)
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto)
        btnSendPhotoWithName = findViewById(R.id.btnSendPhotoWithName)
        etFullName = findViewById(R.id.etFullName)

        btnSelectPhoto.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        btnSendPhotoWithName.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val imageFile = selectedImageFile

            if (imageFile != null && fullName.isNotEmpty()) {
                uploadStuffImage(imageFile, fullName)
            } else {
                Toast.makeText(this, "Введите ФИО и выберите фото", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
