package dev.fest.patephone.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.fest.patephone.R
import dev.fest.patephone.utils.CityHelper

class DialogSpinnerHelper {

    fun showSpinnerDialog(context: Context, list: ArrayList<String>, textViewSelection: TextView) {
        val builder = AlertDialog.Builder(context)
        val dialog = builder.create()
        val rootView = LayoutInflater.from(context).inflate(R.layout.spinner_layout, null)
        val adapter = RecyclerViewDialogSpinnerAdapter(textViewSelection, dialog)
        val recyclerViewSearch = rootView.findViewById<RecyclerView>(R.id.recyclerViewSearch)
        val searchView = rootView.findViewById<SearchView>(R.id.spinnerSearch)
        recyclerViewSearch.layoutManager = LinearLayoutManager(context)
        recyclerViewSearch.adapter = adapter
        dialog.setView(rootView)
        adapter.updateAdapter(list)
        setSearchViewListener(adapter, searchView, list)
        dialog.show()
    }

    private fun setSearchViewListener(
        adapter: RecyclerViewDialogSpinnerAdapter,
        searchView: SearchView?,
        list: ArrayList<String>
    ) {
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val tempList = CityHelper.filterListData(list, newText)
                adapter.updateAdapter(tempList)
                return true
            }
        })
    }
}