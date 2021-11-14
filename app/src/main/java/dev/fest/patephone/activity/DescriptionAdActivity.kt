package dev.fest.patephone.activity

import androidx.viewpager2.widget.ViewPager2

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import dev.fest.patephone.R
import dev.fest.patephone.adapters.ImageAdapter
import dev.fest.patephone.databinding.ActivityDescriptionAdBinding
import dev.fest.patephone.model.Ad
import dev.fest.patephone.utils.ImageManager.fillImageArray


class DescriptionAdActivity : AppCompatActivity() {
    private lateinit var activityDescriptionAdBinding: ActivityDescriptionAdBinding
    private lateinit var adapter: ImageAdapter
    private var ad: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDescriptionAdBinding = ActivityDescriptionAdBinding.inflate(layoutInflater)
        setContentView(activityDescriptionAdBinding.root)
        actionBarSettings()
        init()
        activityDescriptionAdBinding.ad = ad
        Glide
            .with(activityDescriptionAdBinding.root)
            .load(ad?.avatarAccount)
            .circleCrop()
            .into(activityDescriptionAdBinding.imageViewAvatar)
        activityDescriptionAdBinding.buttonCallPhone.setOnClickListener {
            sendCall()
        }
        activityDescriptionAdBinding.buttonSendEmail.setOnClickListener {
            sendEmail()
        }
    }

    private fun init() {
        adapter = ImageAdapter()
        activityDescriptionAdBinding.apply {
            viewPager.adapter = adapter
//            if (viewPager.adapter?.itemCount == null) viewPager.setBackgroundResource(R.drawable.ic_default_image)
        }

        getIntentFromMainActivity()
        imageChangeCounter()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun actionBarSettings() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = ""
    }

    private fun getIntentFromMainActivity() {
        ad = intent.getSerializableExtra("AD") as Ad
        ad?.let { updateUIDescriptionAd(it) }

    }


    private fun updateUIDescriptionAd(ad: Ad) {
        fillImageArray(ad, adapter)
    }

    private fun sendCall() {
        val callUri = "tel:${ad?.phone}"
        val intentCall = Intent(Intent.ACTION_DIAL)
        intentCall.data = callUri.toUri()
        startActivity(intentCall)
    }

    private fun sendEmail() {
        val intentEmail = Intent(Intent.ACTION_SEND)
        intentEmail.type = TYPE_MESSAGE
        intentEmail.apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(ad?.email))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.ad_title_email) + " ${ad?.nameInstrument}")
            putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.ad_text_message) + " ${ad?.nameInstrument}!"
            )
        }
        try {
            startActivity(Intent.createChooser(intentEmail, getString(R.string.ad_open_with)))
        } catch (e: ActivityNotFoundException) {
            Log.d("DescriptionAdActivity", "ActivityNotFoundException")
        }
    }

    private fun imageChangeCounter() {
        activityDescriptionAdBinding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d(
                    "DescriptionAdActivity",
                    "post ${activityDescriptionAdBinding.viewPager.adapter?.itemCount}"
                )
                val imageCounter =
                    "${position + 1}/${activityDescriptionAdBinding.viewPager.adapter?.itemCount}"
                activityDescriptionAdBinding.textViewImageCounter.text = imageCounter
            }

        })
    }

    companion object {
        const val TYPE_MESSAGE = "message/rfc822"
    }
}