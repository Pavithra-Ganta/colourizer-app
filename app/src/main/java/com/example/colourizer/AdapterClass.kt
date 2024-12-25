package com.example.recyclerview

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.colourizer.Gallery
import com.example.colourizer.MainActivity3
import com.example.colourizer.MainActivity4
import com.example.colourizer.R

data class RecyclerItem(val img: Int, val foodname: String)

class AdapterClass(private val dataList: ArrayList<RecyclerItem>) : RecyclerView.Adapter<AdapterClass.ViewHolderClass>() {

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodImage: ImageView = itemView.findViewById(R.id.imgview)
        val foodName: TextView = itemView.findViewById(R.id.itemname)
        val cardView: CardView = itemView.findViewById(R.id.card_view) // Reference to CardView
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
        holder.foodImage.setImageResource(currentItem.img)
        holder.foodName.text = currentItem.foodname

        // Set click listener for CardView
        holder.cardView.setOnClickListener {
            // Use the context from the itemView
            val context = holder.itemView.context
            val intent = Intent(context, MainActivity3::class.java)
            context.startActivity(intent)
        }
    }
}
