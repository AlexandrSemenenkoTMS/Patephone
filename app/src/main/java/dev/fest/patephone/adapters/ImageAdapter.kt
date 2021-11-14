package dev.fest.patephone.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import dev.fest.patephone.R

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageHolder>() {

    val mainList = ArrayList<Bitmap>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.image_adapter_item, parent, false)
        return ImageHolder(view)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(mainList[position])
    }

    override fun getItemCount() = mainList.size

    class  ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var imageItem: ImageView

        fun setData(bitmap: Bitmap) {
            imageItem = itemView.findViewById(R.id.imageViewItem)
            imageItem.setImageBitmap(bitmap)
        }
    }

    fun updateAdapter(list: ArrayList<Bitmap>) {
        mainList.clear()
        mainList.addAll(list)
        notifyDataSetChanged()
    }
}