package com.example.signalization

import android.content.Context
import android.widget.Toast
import com.example.signalization.network.RetrofitInstance.apiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ControlSound(private val context: Context) {
    fun toggleAlarm() {
        apiService.makeNotify().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Сигнал отправлен", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Ошибка: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Сеть недоступна: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
