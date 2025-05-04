package com.example.signalization

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.signalization.data.UnauthAccess

class UnauthAdapter(
    private var items: MutableList<UnauthAccess>,
    private val onMarkAsDecided: (UnauthAccess) -> Unit
) : RecyclerView.Adapter<UnauthAdapter.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newItems: List<UnauthAccess>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvId: TextView = itemView.findViewById(R.id.tvId)
        private val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)
        private val tvDistance: TextView = itemView.findViewById(R.id.tvDistance)
        private val tvIsDecided: TextView = itemView.findViewById(R.id.tvIsDecided)
        private val btnMark: Button = itemView.findViewById(R.id.btnItemMarkAsDecided)

        @SuppressLint("SetTextI18n")
        fun bind(record: UnauthAccess) {
            tvId.text = "ID: ${record.id}"
            tvCreatedAt.text = "Created at: ${record.created_at}"
            tvDistance.text = "Distance: ${record.distance}"
            tvIsDecided.text = "Decided: ${record.is_decided}"

            btnMark.visibility = if (record.is_decided) View.GONE else View.VISIBLE
            btnMark.setOnClickListener {
                onMarkAsDecided(record)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
