package dev.fest.patephone.model

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dev.fest.patephone.utils.FilterManager

class DbManager {

    val db = Firebase.database.getReference(MAIN_NODE)
    val dbStorage = Firebase.storage.getReference(MAIN_NODE)
    val auth = Firebase.auth

    fun publishAd(ad: Ad, finishWorkListener: FinishWorkListener) {
        if (auth.uid != null) db.child(ad.adKey ?: EMPTY)
            .child(auth.uid!!)
            .child(AD_NODE)
            .setValue(ad)
            .addOnCompleteListener {
                val adFilter = FilterManager.createFilter(ad)
//                AdFilter(ad.time, "${ad.type}_${ad.time}")
                db.child(ad.adKey ?: EMPTY)
                    .child(FILTER_AD_NODE)
                    .setValue(adFilter)
                    .addOnCompleteListener {
                        if (it.isSuccessful) finishWorkListener.onFinish()
                    }
            }
    }

    fun publishResume(resume: Resume, finishWorkListener: FinishWorkListener) {
        if (auth.uid != null) db.child(resume.keyResume ?: EMPTY)
            .child(auth.uid!!)
            .child(RESUME_NODE)
            .setValue(resume)
            .addOnCompleteListener {
                val resumeFilter =
                    ResumeFilter(resume.time, "${resume.namePersonResume}_${resume.time}")
                db.child(resume.keyResume ?: EMPTY)
                    .child(FILTER_RESUME_NODE)
                    .setValue(resumeFilter)
                    .addOnCompleteListener {
                        if (it.isSuccessful) finishWorkListener.onFinish()
                    }
            }
    }

    fun adViewed(ad: Ad) {
        var counter = ad.viewsCounter.toInt()
        counter++
        if (auth.uid != null) db.child(ad.adKey ?: EMPTY)
            .child(INFO_NODE)
            .setValue(InfoItem(counter.toString(), ad.emailsCounter, ad.callsCounter))
    }

//    fun placeViewed(ad: Ad) {
//        var country = ad.country
//        var city = ad.city
//        if(country.isNullOrEmpty() && city.isNullOrEmpty())
//    }

    fun onFavClick(ad: Ad, finishWorkListener: FinishWorkListener) {
        if (ad.isFavourite) {
            removeFromFavourites(ad, finishWorkListener)
        } else {
            addToFavourites(ad, finishWorkListener)
        }
    }

    private fun addToFavourites(ad: Ad, finishWorkListener: FinishWorkListener) {
        ad.adKey?.let {
            auth.uid?.let { uid ->
                db.child(it).child(FAVOURITES_NODE).child(uid).setValue(uid).addOnCompleteListener {
                    if (it.isSuccessful) finishWorkListener.onFinish()
                }
            }
        }
    }

    private fun removeFromFavourites(ad: Ad, finishWorkListener: FinishWorkListener) {
        ad.adKey?.let {
            auth.uid?.let { uid ->
                db.child(it).child(FAVOURITES_NODE).child(uid).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) finishWorkListener.onFinish()
                }
            }
        }
    }


    fun getMyAd(readDataCallback: ReadDataCallback) {
        val query = db.orderByChild(auth.uid + "/ad/uid").equalTo(auth.uid)
        readDataFromDb(query, readDataCallback)
    }

    fun getMyFavourites(readDataCallback: ReadDataCallback) {
        val query = db.orderByChild("/favs/${auth.uid}").equalTo(auth.uid)
        readDataFromDb(query, readDataCallback)
    }

    fun getAllAdsFirstPage(filter: String, readDataCallback: ReadDataCallback) {
        val query = if (filter.isEmpty()) {
            db.orderByChild(PATH_FILTER_TIME_AD).limitToLast(ADS_LIMIT)
        } else {
            getAllAdsByFilterFirstPage(filter)
        }
        readDataFromDb(query, readDataCallback)
    }

    private fun getAllAdsByFilterFirstPage(tempFilter: String): Query {
        val orderBy = tempFilter.split(DELIMITER)[0]
        val filter = tempFilter.split(DELIMITER)[1]
        return db.orderByChild("/adFilter/$orderBy").startAfter(filter).endAt(filter + "\uf8ff")
            .limitToLast(ADS_LIMIT)
    }

    fun getAllAdsNextPage(time: String, readDataCallback: ReadDataCallback) {
        val query = db.orderByChild(PATH_FILTER_TIME_AD).endBefore(time).limitToLast(ADS_LIMIT)
        readDataFromDb(query, readDataCallback)
    }

    fun getAllAdsFromTypeFirstPage(
        type: String,
        filter: String,
        readDataCallback: ReadDataCallback
    ) {
        val query =
            if (filter.isEmpty()) {
                db.orderByChild(PATH_FILTER_TYPE_TIME_AD).startAfter(type).endAt(type + "_\uf8ff")
                    .limitToLast(ADS_LIMIT)
            } else {
                getAllAdsFromTypeByFilterFirstPage(type, filter)
            }
        readDataFromDb(query, readDataCallback)
    }

    private fun getAllAdsFromTypeByFilterFirstPage(type: String, tempFilter: String): Query {
        val orderBy = "type_" + tempFilter.split(DELIMITER)[0]
        val filter = type + "_" + tempFilter.split(DELIMITER)[1]
        Log.d("DbManager", "filter: ${filter}, orderBy: $orderBy")
        return db.orderByChild("/adFilter/$orderBy").startAfter(filter).endAt(filter + "\uf8ff")
            .limitToLast(ADS_LIMIT)
    }

    fun getAllAdsFromTypeNextPage(typeTime: String, readDataCallback: ReadDataCallback) {
        val query =
            db.orderByChild(PATH_FILTER_TYPE_TIME_AD).endBefore(typeTime).limitToFirst(ADS_LIMIT)
        readDataFromDb(query, readDataCallback)
    }

    fun deleteAd(ad: Ad, finishWorkListener: FinishWorkListener) {
        if (ad.adKey == null || ad.uid == null) return
        db.child(ad.adKey).child(ad.uid).removeValue()
            .addOnCompleteListener {
//                if (it.isSuccessful)
                finishWorkListener.onFinish()
            }
    }

    private fun readDataFromDb(query: Query, callback: ReadDataCallback?) {
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adArray = ArrayList<Ad>()

                for (item in snapshot.children) {
                    var ad: Ad? = null

                    item.children.forEach {
                        if (ad == null) ad =
                            it.child(AD_NODE).getValue(Ad::class.java)
                    }

                    val infoItem = item.child(INFO_NODE).getValue(InfoItem::class.java)
                    val favCounter = item.child(FAVOURITES_NODE).childrenCount
                    val isFav = auth.uid?.let {
                        item.child(FAVOURITES_NODE).child(it).getValue(String::class.java)
                    }

                    ad?.isFavourite = isFav != null

                    ad?.favCounter = favCounter.toString()

                    ad?.viewsCounter = infoItem?.viewsCounter ?: DEFAULT_COUNTER
                    ad?.emailsCounter = infoItem?.emailsCounter ?: DEFAULT_COUNTER
                    ad?.callsCounter = infoItem?.callsCounter ?: DEFAULT_COUNTER

                    if (ad != null) adArray.add(ad!!)

                }
                callback?.readData(adArray)
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        )
    }

    interface ReadDataCallback {
        fun readData(list: ArrayList<Ad>)
    }

    interface FinishWorkListener {
        fun onFinish()
    }

    companion object {
        const val AD_NODE = "ad"
        const val RESUME_NODE = "resume"
        const val FILTER_AD_NODE = "adFilter"
        const val PATH_FILTER_TIME_AD = "/adFilter/time"
        const val PATH_FILTER_TYPE_TIME_AD = "/adFilter/type_time"
        const val FILTER_RESUME_NODE = "resumeFilter"
        const val PATH_FILTER_TIME_RESUME = "/resumeFilter/time"
        const val PATH_FILTER_TYPE_TIME_RESUME = "/resumeFilter/type_time"
        const val MAIN_NODE = "main"
        const val INFO_NODE = "info"
        const val FAVOURITES_NODE = "favs"
        const val ADS_LIMIT = 10
        const val DEFAULT_COUNTER = "0"
        const val DELIMITER = "|"
        const val EMPTY = "empty"
    }
}