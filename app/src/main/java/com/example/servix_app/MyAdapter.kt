package com.example.servix_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale

class MyAdapter(var announements: List<Announcement>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return announements.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val announcement = announements[position]

        val images: ImageView = holder.itemView.findViewById(R.id.list_item_images)
        val title: TextView = holder.itemView.findViewById(R.id.list_item_title)
        val state: TextView = holder.itemView.findViewById(R.id.list_item_state)
        val location: TextView = holder.itemView.findViewById(R.id.list_item_location)
        val date: TextView = holder.itemView.findViewById(R.id.list_item_date)

        if (announcement.images.isNotEmpty()) {
            Picasso.get().load(announcement.images[0]).into(images)
        }

        title.text = announcement.title
        state.text = announcement.state
        location.text = announcement.location

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        date.text = sdf.format(announcement.date.toDate())
    }
}