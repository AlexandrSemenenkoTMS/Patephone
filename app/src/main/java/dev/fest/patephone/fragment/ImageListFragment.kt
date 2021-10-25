package dev.fest.patephone.fragment

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import dev.fest.patephone.R
import dev.fest.patephone.act.EditAdsAct
import dev.fest.patephone.databinding.ListImageFragmentBinding
import dev.fest.patephone.dialoghelper.ProgressDialog
import dev.fest.patephone.utils.AdapterCallback
import dev.fest.patephone.utils.ImageManager
import dev.fest.patephone.utils.ImagePicker
import dev.fest.patephone.utils.ItemTouchMoveCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageListFragment(private val fragmentCloseInterface: FragmentCloseInterface) :
    BaseAdsFragment(), AdapterCallback {
    val adapter = SelectImageAdapter(this)
    val dragCallback = ItemTouchMoveCallback(adapter)
    val touchHelper = ItemTouchHelper(dragCallback)
    private var job: Job? = null
    private var addImageItem: MenuItem? = null
    private lateinit var binding: ListImageFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ListImageFragmentBinding.inflate(layoutInflater)
        adView = binding.adView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        binding.apply {
            touchHelper.attachToRecyclerView(recyclerViewImageItem)
            recyclerViewImageItem.layoutManager = LinearLayoutManager(activity)
            recyclerViewImageItem.adapter = adapter
        }

    }

    override fun onClose() {
        super.onClose()
        activity?.supportFragmentManager?.beginTransaction()?.remove(this@ImageListFragment)
            ?.commit()
        fragmentCloseInterface.onFragmentClose(adapter.mainList)
        job?.cancel()
    }

    fun updateAdapterFromEdit(bitmapList: List<Bitmap>) {
        adapter.updateAdapter(bitmapList, true)
    }

    fun resizeSelectedImages(newList: ArrayList<Uri>, needClear: Boolean, activity: Activity) {
        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog = ProgressDialog.createProgressDialog(activity)
            val bitmapList = ImageManager.imageResize(newList, activity)
            //закрытие диалога(прогресс бара) после изменения размеров изображений
            dialog.dismiss()
            adapter.updateAdapter(bitmapList, needClear)
            if (adapter.mainList.size > 2) addImageItem?.isVisible = false

        }
    }

    override fun onItemDelete() {
        addImageItem?.isVisible = true
    }



    //настройка тулбара
    private fun setUpToolbar() {

        binding.apply {

            toolbarImage.inflateMenu(R.menu.menu_choose_image)
            val deleteImageItem = toolbarImage.menu.findItem(R.id.delete_image)
            addImageItem = toolbarImage.menu.findItem(R.id.add_image)
            if (adapter.mainList.size > 2) addImageItem?.isVisible = false

            toolbarImage.setNavigationOnClickListener {
                showInterAd()
            }

            deleteImageItem.setOnMenuItemClickListener {
                adapter.updateAdapter(ArrayList(), true)
                addImageItem?.isVisible = true
                true
            }

            addImageItem?.setOnMenuItemClickListener {
                val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainList.size
                ImagePicker.addImages(
                    activity as EditAdsAct,
                    imageCount
                )
                true
            }
        }
    }

    //добавление изображений, если уже добавлены несколько needClear=false
    fun updateAdapter(newList: ArrayList<Uri>, activity: Activity) {
        resizeSelectedImages(newList, false, activity)
    }

    //uri-->bitmap
    fun setSingleImage(uri: Uri, position: Int) {
        val progressBarImageItem =
            binding.recyclerViewImageItem.findViewById<ProgressBar>(R.id.progressBarImageItem)
        job = CoroutineScope(Dispatchers.Main).launch {
            progressBarImageItem.visibility = View.VISIBLE
            val bitmapList = ImageManager.imageResize(arrayListOf(uri), activity as Activity)
            progressBarImageItem.visibility = View.GONE
            adapter.mainList[position] = bitmapList[0]
            adapter.notifyItemChanged(position)
        }

    }

}