package dev.fest.patephone.fragment

import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.fest.patephone.R
import dev.fest.patephone.act.EditAdsAct
import dev.fest.patephone.databinding.SelectImageFragmentItemBinding
import dev.fest.patephone.utils.AdapterCallback
import dev.fest.patephone.utils.ImageManager
import dev.fest.patephone.utils.ImagePicker
import dev.fest.patephone.utils.ItemTouchMoveCallback

class SelectImageAdapter(val adapterCallback: AdapterCallback) :
    RecyclerView.Adapter<SelectImageAdapter.ImageHolder>(),
    ItemTouchMoveCallback.ItemTouchAdapter {

    val mainList = ArrayList<Bitmap>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val viewBinding = SelectImageFragmentItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageHolder(viewBinding, parent.context, this)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.setData(mainList[position])
    }

    override fun getItemCount() = mainList.size

    override fun onMove(startPosition: Int, targetPosition: Int) {
        val targetItem = mainList[targetPosition]
        mainList[targetPosition] = mainList[startPosition]
        mainList[startPosition] = targetItem
        notifyItemMoved(startPosition, targetPosition)
    }

    override fun onClear() {
        notifyDataSetChanged()
    }

    fun updateAdapter(list: List<Bitmap>, needClear: Boolean) {
        if (needClear) mainList.clear()
        mainList.addAll(list)
        notifyDataSetChanged()
    }

    class ImageHolder(
        private val viewBinding: SelectImageFragmentItemBinding,
        val context: Context,
        val adapter: SelectImageAdapter
    ) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun setData(bitmap: Bitmap) {
            viewBinding.imageButtonEdit.setOnClickListener {
                ImagePicker.getSingleImage(context as EditAdsAct)
                context.editImagePosition = adapterPosition
            }

            viewBinding.imageButtonDelete.setOnClickListener {
                adapter.mainList.removeAt(adapterPosition)
                adapter.notifyItemRemoved(adapterPosition)
                for (n in 0 until adapter.mainList.size) adapter.notifyItemChanged(n)
                adapter.adapterCallback.onItemDelete()
            }
            viewBinding.textViewTitle.text =
                context.resources.getStringArray(R.array.title_array)[adapterPosition]
            ImageManager.chooseScaleType(viewBinding.imageViewContent, bitmap)
            viewBinding.imageViewContent.setImageBitmap(bitmap)
        }
    }

}