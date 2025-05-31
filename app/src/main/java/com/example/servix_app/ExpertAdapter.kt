package com.example.servix_app

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ExpertAdapter(private val expertList: List<Expert>) : RecyclerView.Adapter<ExpertAdapter.ExpertViewHolder>() {

    inner class ExpertViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.expert_profile_image)
        val name: TextView = view.findViewById(R.id.expert_name)
        val expertise: TextView = view.findViewById(R.id.expert_expertise)
        val description: TextView = view.findViewById(R.id.expert_description)
        val rating: TextView = view.findViewById(R.id.expert_rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpertViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expert, parent, false)
        return ExpertViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpertViewHolder, position: Int) {
        val expert = expertList[position]

        if (!expert.profilePictureUrl.isNullOrEmpty()) {
            Log.d("ExpertAdapter", "Loading image from URL: ${expert.profilePictureUrl}")
            Picasso.get()
                .load(expert.profilePictureUrl)
                .fit()
                .centerCrop()
                .into(holder.profileImage)
        } else {
            Log.d("ExpertAdapter", "No image found, loading placeholder")
            holder.profileImage.setImageResource(R.drawable.person)
        }

        holder.name.text = expert.name
        holder.expertise.text = expert.expertise
        holder.description.text = expert.description
        holder.rating.text = "Rating: ${"%.1f".format(expert.rating)}"
    }

    override fun getItemCount(): Int = expertList.size
}
