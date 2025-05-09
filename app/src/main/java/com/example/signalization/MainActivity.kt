package com.example.signalization

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.signalization.network.RetrofitInstance
import com.example.signalization.data.MarkAsDecidedRequest
import com.example.signalization.data.UnauthAccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.File
import android.net.Uri
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import java.io.InputStream
import java.io.OutputStream
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Base64
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private lateinit var llContainer: LinearLayout
    private lateinit var imageView: ImageView
    private lateinit var btnSelectPhoto: Button
    private lateinit var btnSendPhotoWithName: Button
    private lateinit var etFullName: EditText
    private var selectedImageFile: File? = null

    private fun createFileFromUri(uri: Uri): File? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "upload_${System.currentTimeMillis()}.jpg")
            val outputStream: OutputStream = file.outputStream()
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageView.setImageURI(it)
                createFileFromUri(it)?.let { file ->
                    selectedImageFile = file
                    Log.d("DEBUG", "Selected image file: ${file.absolutePath}")
                }
            }
        }

    private fun uploadImage(imageFile: File, distanceValue: String) {
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())

        // Ключ должен быть "image", а не "file"
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        val distance = distanceValue.toRequestBody("text/plain".toMediaTypeOrNull())

        val call = RetrofitInstance.apiService.uploadPeopleImage(imagePart, distance)

        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()?.string()
                    Log.d("Upload", "Success: $result")
                } else {
                    Log.e("Upload", "Failed: ${response.code()} ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Upload", "Error: ${t.localizedMessage}")
            }
        })
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
                    Log.d("Upload", "Success: $result")
                } else {
                    Log.e("Upload", "Failed: ${response.code()} ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Upload", "Error: ${t.localizedMessage}")
            }
        })
    }

    private fun getPathFromUri(context: Context, uri: Uri): String? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)
        return cursor?.use {
            it.moveToFirst()
            val columnIndex = it.getColumnIndexOrThrow(filePathColumn[0])
            it.getString(columnIndex)
        }
    }

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnGetAllRecords: Button      = findViewById(R.id.btnGetAllRecords)
        val btnGetLastUndecided: Button  = findViewById(R.id.btnGetLastUndecided)
        val etDistance: EditText = findViewById(R.id.etDistance)
        val btnUploadImage: Button = findViewById(R.id.btnUploadImage)

        llContainer = findViewById(R.id.llContainer)

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

        val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val imageFile = createFileFromUri(it)
                val distanceValue = findViewById<EditText>(R.id.etDistance).text.toString()
                if (imageFile != null && distanceValue.isNotBlank()) {
                    uploadImage(imageFile, distanceValue)
                } else {
                    Toast.makeText(this, "Missing image or distance", Toast.LENGTH_SHORT).show()
                }
            }
        }




        btnUploadImage.setOnClickListener {
            val distanceText = etDistance.text.toString()
            if (distanceText.isBlank()) {
                Toast.makeText(this, "Please enter a distance", Toast.LENGTH_SHORT).show()
            } else {
                imagePickerLauncher.launch("image/*")
            }
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Получили токен
            val token = task.result
            Log.d("FCM", "FCM Token (manual fetch): $token")
        }

        // 1) Get All Records → рисуем карточки
        btnGetAllRecords.setOnClickListener {
            llContainer.removeAllViews()
            RetrofitInstance.apiService.getAllRecords()
                .enqueue(object : Callback<List<UnauthAccess>> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(call: Call<List<UnauthAccess>>, resp: Response<List<UnauthAccess>>) {
                        if (!resp.isSuccessful) return
                        resp.body()?.forEach { record ->
                            // inflate карточку
                            val card = layoutInflater
                                .inflate(R.layout.item_record, llContainer, false)
                            card.findViewById<TextView>(R.id.tvId).text        = "ID: ${record.id}"
                            card.findViewById<TextView>(R.id.tvCreatedAt).text = "Created: ${record.created_at}"
                            card.findViewById<TextView>(R.id.tvDistance).text  = "Distance: ${record.distance}"
                            card.findViewById<TextView>(R.id.tvIsDecided).text = "Decided: ${record.is_decided}"

                            val ivPhoto = card.findViewById<ImageView>(R.id.ivPhoto)
                            if (record.photo_base64 != null) {
                                try {
                                    val decodedBytes = Base64.decode(record.photo_base64, Base64.DEFAULT)
                                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                                    ivPhoto.setImageBitmap(bitmap)
                                    ivPhoto.visibility = View.VISIBLE
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {
                                ivPhoto.visibility = View.GONE
                            }

                            val btn = card.findViewById<Button>(R.id.btnItemMarkAsDecided)
                            btn.visibility = if (record.is_decided) View.GONE else View.VISIBLE
                            btn.setOnClickListener {
                                // mark this one as decided
                                RetrofitInstance.apiService
                                    .markAsDecided(MarkAsDecidedRequest(id = record.id))
                                    .enqueue(object: Callback<Map<String,String>> {
                                        override fun onResponse(c: Call<Map<String,String>>, r: Response<Map<String,String>>) {
                                            if (r.isSuccessful) {
                                                record.is_decided = true
                                                btn.visibility = View.GONE
                                            }
                                        }
                                        override fun onFailure(c: Call<Map<String,String>>, t: Throwable){}
                                    })
                            }

                            llContainer.addView(card)
                        }
                    }
                    override fun onFailure(call: Call<List<UnauthAccess>>, t: Throwable) { /*…*/ }
                })
        }

        btnGetLastUndecided.setOnClickListener {
            llContainer.removeAllViews()
            RetrofitInstance.apiService.getLastUndecided()
                .enqueue(object: Callback<UnauthAccess>{
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(c: Call<UnauthAccess>, r: Response<UnauthAccess>) {
                        if (!r.isSuccessful) return
                        r.body()?.let { record ->
                            val card = layoutInflater.inflate(R.layout.item_record, llContainer, false)
                            card.findViewById<TextView>(R.id.tvId).text = "ID: ${record.id}"
                            card.findViewById<TextView>(R.id.tvCreatedAt).text = "Created: ${record.created_at}"
                            card.findViewById<TextView>(R.id.tvDistance).text = "Distance: ${record.distance}"
                            card.findViewById<TextView>(R.id.tvIsDecided).text = "Decided: ${record.is_decided}"
                            val btn = card.findViewById<Button>(R.id.btnItemMarkAsDecided)
                            btn.visibility = View.VISIBLE
                            btn.setOnClickListener {
                                RetrofitInstance.apiService
                                    .markAsDecided(MarkAsDecidedRequest(id = record.id))
                                    .enqueue(object: Callback<Map<String,String>>{
                                        override fun onResponse(c2: Call<Map<String,String>>, r2: Response<Map<String,String>>) {
                                            if (r2.isSuccessful) btn.visibility = View.GONE
                                        }
                                        override fun onFailure(c2: Call<Map<String,String>>, t2: Throwable){}
                                    })
                            }
                            llContainer.addView(card)
                        }
                    }
                    override fun onFailure(c: Call<UnauthAccess>, t: Throwable) { /*…*/ }
                })
        }
    }
}
