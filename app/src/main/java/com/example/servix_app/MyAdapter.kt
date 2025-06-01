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

// Define an interface for click listener
interface OnServiceClickListener {
    fun onServiceClick(announcement: Announcement)
}

class MyAdapter(
    private var announcements: MutableList<Announcement>,
    private val listener: OnServiceClickListener
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.list_item_images)
        val title: TextView = itemView.findViewById(R.id.list_item_title)
        val location: TextView = itemView.findViewById(R.id.list_item_location)
        val date: TextView = itemView.findViewById(R.id.list_item_date)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onServiceClick(announcements[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val announcement = announcements[position]

        if (announcement.images.isNotEmpty()) {
            Picasso.get()
                .load(announcement.images[0])
                .placeholder(R.drawable.person)
                .error(R.drawable.person)
                .fit()
                .centerCrop()
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.person)
        }

        holder.title.text = announcement.title
        holder.location.text = announcement.location

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        holder.date.text = sdf.format(announcement.date.toDate())
    }

    override fun getItemCount(): Int = announcements.size

    fun updateData(newList: MutableList<Announcement>) {
        this.announcements = newList
        notifyDataSetChanged()
    }
}