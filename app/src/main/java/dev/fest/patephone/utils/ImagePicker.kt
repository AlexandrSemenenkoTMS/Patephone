package dev.fest.patephone.utils

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import dev.fest.patephone.R
import dev.fest.patephone.act.EditAdsAct
import io.ak1.pix.helpers.PixEventCallback
import io.ak1.pix.helpers.addPixToActivity
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ImagePicker {
    const val MAX_IMAGE_COUNT = 3
    private fun getOptions(imageCounter: Int): Options {
        val options = Options().apply {
            count = imageCounter
            isFrontFacing = false
            mode = Mode.Picture
            path = "Pix/Camera"
        }
        return options
    }

    fun getMultiImages(editActivity: EditAdsAct, imageCounter: Int) {
        editActivity.addPixToActivity(R.id.placeHolder, getOptions(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    getMultiSelectedImages(editActivity, result.data)
                }
            }
        }
    }

    fun addImages(editActivity: EditAdsAct, imageCounter: Int) {
        editActivity.addPixToActivity(R.id.placeHolder, getOptions(imageCounter)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    openChooseImageFragment(editActivity)
                    editActivity.chooseImageFragment?.updateAdapter(result.data as ArrayList<Uri>, editActivity)
                }
            }
        }
    }

    fun  getSingleImage(editActivity: EditAdsAct) {
        editActivity.addPixToActivity(R.id.placeHolder, getOptions(1)) { result ->
            when (result.status) {
                PixEventCallback.Status.SUCCESS -> {
                    openChooseImageFragment(editActivity)
                    singleImage(editActivity, result.data[0])
                }
            }
        }
    }

    private fun openChooseImageFragment(editActivity: EditAdsAct) {
        editActivity.supportFragmentManager.beginTransaction()
            .replace(R.id.placeHolder, editActivity.chooseImageFragment!!).commit()

    }

    private fun closePixFragment(editActivity: EditAdsAct) {
        val fList = editActivity.supportFragmentManager.fragments
        fList.forEach { fragment ->
            if (fragment.isVisible) editActivity.supportFragmentManager.beginTransaction()
                .remove(fragment).commit()
        }
    }

    fun getMultiSelectedImages(editActivity: EditAdsAct, uris: List<Uri>) {
        if (uris.size > 1 && editActivity.chooseImageFragment == null) {
            editActivity.openChooseImageFragment(uris as ArrayList<Uri>)
        } else if (uris.size == 1 && editActivity.chooseImageFragment == null) {
            CoroutineScope(Dispatchers.Main).launch {
                editActivity.activityEditAdsBinding.progressBarEditAds.visibility = View.VISIBLE
                val bitmapArray =
                    ImageManager.imageResize(
                        uris as ArrayList<Uri>,
                        editActivity
                    ) as ArrayList<Bitmap>
                editActivity.activityEditAdsBinding.progressBarEditAds.visibility = View.GONE
                editActivity.imageAdapter.updateAdapter(bitmapArray)
                closePixFragment(editActivity)
            }
        }
    }

    private fun singleImage(editActivity: EditAdsAct, uri: Uri) {
        editActivity.chooseImageFragment?.setSingleImage(uri, editActivity.editImagePosition)
    }
}

