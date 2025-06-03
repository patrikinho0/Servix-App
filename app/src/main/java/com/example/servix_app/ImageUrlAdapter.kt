package com.example.servix_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import android.util.TypedValue

class ImageUrlAdapter(private val imageUrls: List<String>) : RecyclerView.Adapter<ImageUrlAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_service_detail_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]

        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams

        val marginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            4f,
            holder.itemView.context.resources.displayMetrics
        ).toInt()

        if (position < imageUrls.size - 1) {
            layoutParams.rightMargin = marginPx
        } else {
            layoutParams.rightMargin = 0
        }
        holder.itemView.layoutParams = layoutParams

        Picasso.get()
            .load(imageUrl)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.add_photo)
            .error(R.drawable.add_photo)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.service_detail_image_view)
    }
}