package com.example.signalization

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.signalization.data.FormateDate
import com.example.signalization.data.MarkAsDecidedRequest
import com.example.signalization.data.UnauthAccess
import com.example.signalization.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale
import java.util.TimeZone
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class LastUndecidedLoader(
    private val context: Context,
    private val layoutInflater: LayoutInflater,
    private val container: ViewGroup
) {


    @OptIn(ExperimentalEncodingApi::class)
    fun load() {
        container.removeAllViews()
        RetrofitInstance.apiService.getLastUndecided()
            .enqueue(object : Callback<UnauthAccess> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call<UnauthAccess>, response: Response<UnauthAccess>) {
                    if (!response.isSuccessful) return
                    response.body()?.let { record ->
                        val card = layoutInflater.inflate(R.layout.item_record, container, false)
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
                        btn.visibility = View.VISIBLE
                        btn.setOnClickListener {
                            RetrofitInstance.apiService
                                .markAsDecided(MarkAsDecidedRequest(id = record.id))
                                .enqueue(object : Callback<Map<String, String>> {
                                    override fun onResponse(call2: Call<Map<String, String>>, response2: Response<Map<String, String>>) {
                                        if (response2.isSuccessful) btn.visibility = View.GONE
                                    }
                                    override fun onFailure(call2: Call<Map<String, String>>, t2: Throwable) {}
                                })
                        }

                        container.addView(card)
                    }
                }

                override fun onFailure(call: Call<UnauthAccess>, t: Throwable) {}
            })
    }
}
