package com.example.recyclerview

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.colourizer.MainActivity3
import com.example.colourizer.R
import com.squareup.picasso.Picasso // Library for loading images from URLs

data class ImageData(val imgUrl: String, val title: String) // Data model for images

class AdapterClass(private val dataList: ArrayList<ImageData>) : RecyclerView.Adapter<AdapterClass.ViewHolderClass>() {

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imgview)
        val titleText: TextView = itemView.findViewById(R.id.itemname)
        val cardView: CardView = itemView.findViewById(R.id.card_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolderClass(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]

        // Load image using Picasso
        Picasso.get().load(currentItem.imgUrl).placeholder(R.drawable.pics).into(holder.imageView)
        holder.titleText.text = currentItem.title

        // Set click listener for CardView
        holder.cardView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, MainActivity3::class.java)
            context.startActivity(intent)
        }
    }
}
