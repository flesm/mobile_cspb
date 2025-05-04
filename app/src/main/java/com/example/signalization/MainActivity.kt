package com.example.signalization

import android.annotation.SuppressLint
import android.os.Bundle
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

class MainActivity : AppCompatActivity() {

    private lateinit var llContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // старые кнопки
        val btnGetAllRecords: Button      = findViewById(R.id.btnGetAllRecords)
        val btnGetLastUndecided: Button  = findViewById(R.id.btnGetLastUndecided)

        llContainer = findViewById(R.id.llContainer)

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

        // 2) Get Last Undecided → рисуем одну карточку
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
