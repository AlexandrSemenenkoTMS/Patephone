package dev.fest.patephone.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso
import dev.fest.patephone.adapters.ImageAdapter
import dev.fest.patephone.model.Ad
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ImageManager {

    private const val MAX_IMAGE_SIZE = 1280
    private const val IMAGE_WIDTH = 0
    private const val IMAGE_HEIGHT = 1

    //проверка размера изображения
    fun getImageSize(uri: Uri, activity: Activity): List<Int> {
        val inputStream = activity.contentResolver.openInputStream(uri)

        val options = BitmapFactory.Options().apply {
            //по краям
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inputStream, null, options)
        return listOf(options.outWidth, options.outHeight)
    }


    //проверка ориентации изображения
//    fun imageRotation(imageFile: File): Int {
//        val rotation: Int
//        val exif = ExifInterface(imageFile.absolutePath)
//        val orientation =
//            exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
//        rotation =
//            if (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270) {
//                90
//            } else {
//                0
//            }
//        return rotation
//    }

    fun chooseScaleType(imageView: ImageView, bitmap: Bitmap) {
        if (bitmap.width > bitmap.height) {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        }
    }

    suspend fun imageResize(uris: ArrayList<Uri>, activity: Activity): List<Bitmap> =
        withContext(Dispatchers.IO) {
            val tempList = ArrayList<List<Int>>()
            val bitMapList = ArrayList<Bitmap>()
            for (n in uris.indices) {
                val size = getImageSize(uris[n], activity)
                Log.d(
                    "ImageManager",
                    "image $n -- width: ${size[IMAGE_WIDTH]}, height: ${size[IMAGE_HEIGHT]}"
                )

                val imageRatio = size[IMAGE_WIDTH].toFloat() / size[IMAGE_HEIGHT].toFloat()
                if (imageRatio > 1) {
                    if (size[IMAGE_WIDTH] > MAX_IMAGE_SIZE) {
                        tempList.add(listOf(MAX_IMAGE_SIZE, (MAX_IMAGE_SIZE / imageRatio).toInt()))
                    } else {
                        tempList.add(listOf(size[IMAGE_WIDTH], size[IMAGE_HEIGHT]))
                    }
                } else {
                    if (size[IMAGE_HEIGHT] > MAX_IMAGE_SIZE) {
                        tempList.add(listOf((MAX_IMAGE_SIZE * imageRatio).toInt(), MAX_IMAGE_SIZE))
                    } else {
                        tempList.add(listOf(size[IMAGE_WIDTH], size[IMAGE_HEIGHT]))
                    }
                }
                Log.d(
                    "ImageManager",
                    "image $n --- width: ${tempList[n][IMAGE_WIDTH]}, height: ${tempList[n][IMAGE_HEIGHT]}"
                )
            }

            for (i in uris.indices) {
                kotlin.runCatching {
                    bitMapList.add(
                        Picasso.get().load(uris[i])
                            .resize(tempList[i][IMAGE_WIDTH], tempList[i][IMAGE_HEIGHT]).get()
                    )
                }
            }

            return@withContext bitMapList
        }

    private suspend fun getBitmapFromUris(uris: List<String?>): List<Bitmap> =
        withContext(Dispatchers.IO) {
            val bitMapList = ArrayList<Bitmap>()
            for (i in uris.indices) {
                kotlin.runCatching {
                    bitMapList.add(Picasso.get().load(uris[i]).get())
                }
            }
            return@withContext bitMapList
        }

    fun fillImageArray(ad: Ad, adapter: ImageAdapter) {
        val listUris = listOf(ad.mainImage, ad.image2, ad.image3)
        CoroutineScope(Dispatchers.Main).launch {
            val bitmapList = getBitmapFromUris(listUris)
            adapter.updateAdapter(bitmapList as ArrayList<Bitmap>)
        }
    }


}