package com.example.signalization.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.signalization.R
import com.example.signalization.data.MarkAsDecidedRequest
import com.example.signalization.data.UnauthAccess
import com.example.signalization.network.RetrofitInstance
import android.graphics.BitmapFactory
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.signalization.data.FormateDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AllRecordsActivity : AppCompatActivity() {
    private lateinit var llContainer: LinearLayout

    @SuppressLint("MissingInflatedId")
    @OptIn(ExperimentalEncodingApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_records_activity)

        llContainer = findViewById(R.id.containerAllRecords)
        val layoutInflater = LayoutInflater.from(this)

        llContainer.removeAllViews()
        RetrofitInstance.apiService.getAllRecords()
            .enqueue(object : Callback<List<UnauthAccess>> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<List<UnauthAccess>>, resp: Response<List<UnauthAccess>>) {
                    if (!resp.isSuccessful) return
                    resp.body()?.forEach { record ->
                        val card = layoutInflater.inflate(R.layout.item_record, llContainer, false)
                        val formattedDate = FormateDate.formatDate(record.created_at)

                        card.findViewById<TextView>(R.id.tvId).text = "${record.id}."
                        val tvCreatedAt = card.findViewById<TextView>(R.id.tvCreatedAt)
                        tvCreatedAt.text = "Время: $formattedDate"

                        card.findViewById<TextView>(R.id.tvDistance).text = "Дистанция: ${record.distance} метра"

                        val ivPhoto = card.findViewById<ImageView>(R.id.ivPhoto)
                        if (record.photo_base64 != null) {
                            try {
                                val decodedBytes = Base64.decode(record.photo_base64)
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
                            RetrofitInstance.apiService
                                .markAsDecided(MarkAsDecidedRequest(id = record.id))
                                .enqueue(object : Callback<Map<String, String>> {
                                    override fun onResponse(c: Call<Map<String, String>>, r: Response<Map<String, String>>) {
                                        if (r.isSuccessful) {
                                            record.is_decided = true
                                            btn.visibility = View.GONE
                                        }
                                    }
                                    override fun onFailure(c: Call<Map<String, String>>, t: Throwable) {}
                                })
                        }

                        llContainer.addView(card)
                    }
                }

                override fun onFailure(call: Call<List<UnauthAccess>>, t: Throwable) {}
            })
    }
}
