package dev.fest.patephone.model

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DbManagerResume {

    val db = Firebase.database.getReference(MAIN_NODE)
    val dbStorage = Firebase.storage.getReference(MAIN_NODE)
    val auth = Firebase.auth

    private fun publishResume(resume: Resume, finishWorkListener: FinishWorkListener) {
        if (auth.uid != null) db.child(resume.keyResume ?: "empty")
            .child(auth.uid!!)
            .child(RESUME_NODE)
            .setValue(resume)
            .addOnCompleteListener {
                if (it.isSuccessful) finishWorkListener.onFinish()
            }
    }

    interface FinishWorkListener {
        fun onFinish()
    }

    companion object {
        const val RESUME_NODE = "resume"
        const val MAIN_NODE = "main"
        const val INFO_NODE = "info"
        const val FAVS_NODE = "favs"
        const val ADS_LIMIT = 2
    }
}