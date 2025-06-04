package com.example.servix_app

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ImageAdapter(private val imageUris: ArrayList<Uri>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private var onDeleteClickListener: ((Int) -> Unit)? = null

    fun setOnDeleteClickListener(listener: (Int) -> Unit) {
        this.onDeleteClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = imageUris[position]
        Picasso.get()
            .load(imageUri)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.add_photo)
            .error(R.drawable.add_photo)
            .into(holder.imageView)

        holder.deleteButton.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION) {
                imageUris.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
                notifyItemRangeChanged(adapterPosition, imageUris.size)
                onDeleteClickListener?.invoke(adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.selected_image_view)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_image_button)
    }
}