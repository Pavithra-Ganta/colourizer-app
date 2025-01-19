package com.example.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.colourizer.R
import com.squareup.picasso.Picasso

data class ImageData(val imgUrl: String, val title: String, var selected: Boolean = false)

class AdapterClass(
    private val dataList: ArrayList<ImageData>,
    private val onItemLongPress: (Int) -> Unit,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<AdapterClass.ViewHolderClass>() {

    private var isSelectionMode = false

    class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imgview)
        val titleText: TextView = itemView.findViewById(R.id.itemname)
        val cardView: CardView = itemView.findViewById(R.id.card_view)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolderClass(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setSelectionMode(enabled: Boolean) {
        isSelectionMode = enabled
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]

        // Load image using Picasso
        Picasso.get().load(currentItem.imgUrl).placeholder(R.drawable.pics).into(holder.imageView)
        holder.titleText.text = currentItem.title

        // Show CheckBox only in selection mode
        holder.checkBox.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
        holder.checkBox.isChecked = currentItem.selected

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            currentItem.selected = isChecked
        }

        holder.cardView.setOnLongClickListener {
            onItemLongPress(position)
            true
        }

        holder.cardView.setOnClickListener {
            onItemClick(position)
        }
    }

    fun toggleSelection(position: Int) {
        val item = dataList[position]
        item.selected = !item.selected
        notifyItemChanged(position)
    }

    fun selectAll(select: Boolean) {
        for (item in dataList) {
            item.selected = select
        }
        notifyDataSetChanged()
    }

    fun deselectAll() {
        for (item in dataList) {
            item.selected = false
        }
        notifyDataSetChanged()
    }

    fun deleteSelectedItems() {
        dataList.removeAll { it.selected }
        notifyDataSetChanged()
    }
}
