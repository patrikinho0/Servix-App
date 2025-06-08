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

class CommentAdapter(private val comments: MutableList<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userProfilePicture: ImageView = itemView.findViewById(R.id.commentUserProfilePicture)
        val userNameTextView: TextView = itemView.findViewById(R.id.commentUserName)
        val userRoleTextView: TextView = itemView.findViewById(R.id.commentUserRole)
        val dateTextView: TextView = itemView.findViewById(R.id.commentDate)
        val commentTextView: TextView = itemView.findViewById(R.id.commentText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        if (comment.userProfilePictureUrl.isNotEmpty()) {
            Picasso.get()
                .load(comment.userProfilePictureUrl)
                .placeholder(R.drawable.person)
                .error(R.drawable.person)
                .fit()
                .centerCrop()
                .into(holder.userProfilePicture)
        } else {
            holder.userProfilePicture.setImageResource(R.drawable.person)
        }

        holder.userNameTextView.text = comment.userName

        if (comment.userRole.isNotEmpty()) {
            holder.userRoleTextView.text = "(${comment.userRole.replaceFirstChar { it.uppercase() }})"
            holder.userRoleTextView.visibility = View.VISIBLE
        } else {
            holder.userRoleTextView.visibility = View.GONE
        }

        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        holder.dateTextView.text = sdf.format(comment.timestamp.toDate())

        holder.commentTextView.text = comment.text
    }

    override fun getItemCount(): Int = comments.size

    fun addComment(comment: Comment) {
        comments.add(0, comment)
        notifyItemInserted(0)
    }

    fun setComments(newComments: List<Comment>) {
        comments.clear()
        comments.addAll(newComments.sortedByDescending { it.timestamp })
        notifyDataSetChanged()
    }
}
