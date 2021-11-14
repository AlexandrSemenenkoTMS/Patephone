package dev.fest.patephone.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.fest.patephone.model.DbManager
import dev.fest.patephone.model.Ad

class FirebaseViewModel : ViewModel() {

    private val dbManager = DbManager()

    val liveDataAd = MutableLiveData<ArrayList<Ad>>()

//    private val compositeDisposable = CompositeDisposable()
////    private val productDao by lazy {getApplication<AdApp>().database.getAdDao() }
//    private val productsList = mutableListOf<Ad>()

    fun loadAllAdsFirstPage(filter:String) {
        dbManager.getAllAdsFirstPage(filter, object : DbManager.ReadDataCallback {
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

    fun loadAllAdsFromType(type: String,filter: String) {
        dbManager.getAllAdsFromTypeFirstPage(type, filter, object : DbManager.ReadDataCallback {
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

    fun onFavouriteClick(ad: Ad) {
        dbManager.onFavClick(ad, object : DbManager.FinishWorkListener {
            override fun onFinish() {
                val updatedList = liveDataAd.value
                val pos = updatedList?.indexOf(ad)
                if (pos != -1) {
                    pos?.let {
                        val favCounter =
                            if (ad.isFavourite) ad.favCounter.toInt() - 1 else ad.favCounter.toInt() + 1
                        updatedList[pos] = updatedList[pos].copy(
                            isFavourite = !ad.isFavourite,
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
        dbManager.getMyFavourites(object : DbManager.ReadDataCallback {
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
