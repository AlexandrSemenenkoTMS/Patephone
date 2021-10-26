package dev.fest.patephone.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.fest.patephone.model.DbManager
import dev.fest.patephone.model.Ad

class FirebaseViewModel : ViewModel() {

    private val dbManager = DbManager()

    val liveDataAd = MutableLiveData<ArrayList<Ad>>()

    fun loadAllAdsFirstPage() {
        dbManager.getAllAdsFirstPage(object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ad>) {
                liveDataAd.value = list
            }
        })
    }

    fun loadAllAdsNextPage(time: String) {
        dbManager.getAllAdsNextPage(time, object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ad>) {
                liveDataAd.value = list
            }
        })
    }

    fun loadAllAdsFromType(type: String) {
        dbManager.getAllAdsFromTypeFirstPage(type, object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ad>) {
                liveDataAd.value = list
            }
        })
    }

    fun loadAllAdsFromTypeNextPage(typeTime: String) {
        dbManager.getAllAdsFromTypeNextPage(typeTime, object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ad>) {
                liveDataAd.value = list
            }
        })
    }

    fun onFavClick(ad: Ad) {
        dbManager.onFavClick(ad, object : DbManager.FinishWorkListener {
            override fun onFinish() {
                val updatedList = liveDataAd.value
                val pos = updatedList?.indexOf(ad)
                if (pos != -1) {
                    pos?.let {
                        val favCounter =
                            if (ad.isFav) ad.favCounter.toInt() - 1 else ad.favCounter.toInt() + 1
                        updatedList[pos] = updatedList[pos].copy(
                            isFav = !ad.isFav,
                            favCounter = favCounter.toString()
                        )
                    }
                }
                liveDataAd.postValue(updatedList!!)
            }

        })
    }

    fun adViewed(ad: Ad) {
        dbManager.adViewed(ad)
    }

    fun loadMyAd() {
        dbManager.getMyAd(object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ad>) {
                liveDataAd.value = list
            }
        })
    }

    fun loadMyFavs() {
        dbManager.getMyFavs(object : DbManager.ReadDataCallback {
            override fun readData(list: ArrayList<Ad>) {
                liveDataAd.value = list
            }
        })
    }

    fun deleteAd(ad: Ad) {
        dbManager.deleteAd(ad, object : DbManager.FinishWorkListener {
            override fun onFinish() {
                val updatedList = liveDataAd.value
                updatedList?.remove(ad)
                liveDataAd.postValue(updatedList!!)
            }

        })
    }
}
