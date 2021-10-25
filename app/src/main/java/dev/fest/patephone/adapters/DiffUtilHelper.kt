package dev.fest.patephone.adapters

import androidx.recyclerview.widget.DiffUtil
import dev.fest.patephone.model.Ad

class DiffUtilHelper(val oldList: List<Ad>, val newList: List<Ad>) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].key == newList[newItemPosition].key

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}