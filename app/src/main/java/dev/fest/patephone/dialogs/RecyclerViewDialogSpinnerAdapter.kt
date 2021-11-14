package dev.fest.patephone.dialogs

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.fest.patephone.R

class RecyclerViewDialogSpinnerAdapter(private var textViewSelection: TextView, private var dialog: AlertDialog) :
    RecyclerView.Adapter<RecyclerViewDialogSpinnerAdapter.SpinnerViewHolder>() {

    private val mainList = ArrayList<String>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpinnerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.spinner_list_item, parent, false)
        return SpinnerViewHolder(view, textViewSelection, dialog)
    }

    override fun onBindViewHolder(holder: SpinnerViewHolder, position: Int) {
        holder.setData(mainList[position])
    }

    override fun getItemCount() = mainList.size


    class SpinnerViewHolder(
        itemView: View,
        private var textViewSelection: TextView,
        private var dialog: AlertDialog
    ) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var itemText = ""
        fun setData(text: String) {
            val textViewSpinnerItem = itemView.findViewById<TextView>(R.id.textViewSpinnerItem)
            textViewSpinnerItem.text = text
            itemText = text
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            textViewSelection.text = itemText
            dialog.dismiss()
        }
    }

    fun updateAdapter(list: ArrayList<String>) {
        mainList.clear()
        mainList.addAll(list)
        notifyDataSetChanged()
    }
}