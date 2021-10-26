package dev.fest.patephone.model

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DbManager {

    val db = Firebase.database.getReference(MAIN_NODE)
    val dbStorage = Firebase.storage.getReference(MAIN_NODE)
    val auth = Firebase.auth


    fun publishAd(ad: Ad, finishWorkListener: FinishWorkListener) {
        if (auth.uid != null) db.child(ad.key ?: "empty")
            .child(auth.uid!!)
            .child(AD_NODE)
            .setValue(ad)
            .addOnCompleteListener {
                val adFilter = AdFilter(ad.time, "${ad.type}_${ad.time}")
                db.child(ad.key ?: "empty")
                    .child(FILTER_AD_NODE)
                    .setValue(adFilter)
                    .addOnCompleteListener {
                        if (it.isSuccessful) finishWorkListener.onFinish()
                    }
            }
    }

    fun publishResume(resume: Resume, finishWorkListener: FinishWorkListener) {
        if (auth.uid != null) db.child(resume.keyResume ?: "empty")
            .child(auth.uid!!)
            .child(RESUME_NODE)
            .setValue(resume)
            .addOnCompleteListener {
                val resumeFilter =
                    ResumeFilter(resume.time, "${resume.namePersonResume}_${resume.time}")
                db.child(resume.keyResume ?: "empty")
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
        if (auth.uid != null) db.child(ad.key ?: "empty")
            .child(INFO_NODE)
            .setValue(InfoItem(counter.toString(), ad.emailsCounter, ad.callsCounter))
    }

    fun onFavClick(ad: Ad, finishWorkListener: FinishWorkListener) {
        if (ad.isFav) {
            removeFromFavs(ad, finishWorkListener)
        } else {
            addToFavs(ad, finishWorkListener)
        }
    }

    private fun addToFavs(ad: Ad, finishWorkListener: FinishWorkListener) {
        ad.key?.let {
            auth.uid?.let { uid ->
                db.child(it).child(FAVS_NODE).child(uid).setValue(uid).addOnCompleteListener {
                    if (it.isSuccessful) finishWorkListener.onFinish()
                }
            }
        }
    }

    private fun removeFromFavs(ad: Ad, finishWorkListener: FinishWorkListener) {
        ad.key?.let {
            auth.uid?.let { uid ->
                db.child(it).child(FAVS_NODE).child(uid).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) finishWorkListener.onFinish()
                }
            }
        }
    }


    fun getMyAd(readDataCallback: ReadDataCallback) {
        val query = db.orderByChild(auth.uid + "/ad/uid").equalTo(auth.uid)
        readDataFromDb(query, readDataCallback)
    }

    fun getMyFavs(readDataCallback: ReadDataCallback) {
        val query = db.orderByChild("/favs/${auth.uid}").equalTo(auth.uid)
        readDataFromDb(query, readDataCallback)
    }

    fun getAllAdsFirstPage(readDataCallback: ReadDataCallback) {
        val query = db.orderByChild(PATH_FILTER_TIME_AD).limitToLast(ADS_LIMIT)
        readDataFromDb(query, readDataCallback)
    }

    fun getAllAdsNextPage(time: String, readDataCallback: ReadDataCallback) {
        val query = db.orderByChild(PATH_FILTER_TIME_AD).endBefore(time).limitToLast(ADS_LIMIT)
        readDataFromDb(query, readDataCallback)
    }

    fun getAllAdsFromTypeFirstPage(type: String, readDataCallback: ReadDataCallback) {
        val query =
            db.orderByChild(PATH_FILTER_TYPE_TIME_AD).startAfter(type).endAt(type + "_\uf8ff")
                .limitToLast(ADS_LIMIT)
        readDataFromDb(query, readDataCallback)
    }

    fun getAllAdsFromTypeNextPage(typeTime: String, readDataCallback: ReadDataCallback) {
        val query =
            db.orderByChild(PATH_FILTER_TYPE_TIME_AD).endBefore(typeTime).limitToFirst(ADS_LIMIT)
        readDataFromDb(query, readDataCallback)
    }

    fun deleteAd(ad: Ad, finishWorkListener: FinishWorkListener) {
        if (ad.key == null || ad.uid == null) return
        db.child(ad.key).child(ad.uid).removeValue()
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
                    val favCounter = item.child(FAVS_NODE).childrenCount
                    val isFav = auth.uid?.let {
                        item.child(FAVS_NODE).child(it).getValue(String::class.java)
                    }

                    ad?.isFav = isFav != null

                    ad?.favCounter = favCounter.toString()

                    ad?.viewsCounter = infoItem?.viewsCounter ?: "0"
                    ad?.emailsCounter = infoItem?.emailsCounter ?: "0"
                    ad?.callsCounter = infoItem?.callsCounter ?: "0"

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
        const val PATH_FILTER_TYPE_TIME_AD = "/adFilter/typeTime"
        const val FILTER_RESUME_NODE = "resumeFilter"
        const val PATH_FILTER_TIME_RESUME = "/resumeFilter/time"
        const val PATH_FILTER_TYPE_TIME_RESUME = "/resumeFilter/typeTime"
        const val MAIN_NODE = "main"
        const val INFO_NODE = "info"
        const val FAVS_NODE = "favs"
        const val ADS_LIMIT = 10
    }
}