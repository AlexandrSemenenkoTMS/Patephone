package dev.fest.patephone.act

import androidx.viewpager2.widget.ViewPager2

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import dev.fest.patephone.adapters.ImageAdapter
import dev.fest.patephone.databinding.ActivityDescriptionAdBinding
import dev.fest.patephone.model.Ad
import dev.fest.patephone.utils.ImageManager.fillImageArray

// давай пакет переименуем, act звучит непонятно, как-то структура проекта разорвана
// очень жаль не видеть ViewModel, большая часть логики по-хорошему выносится туда
class DescriptionAdActivity : AppCompatActivity() {

    //private поля
    lateinit var activityDescriptionAdBinding: ActivityDescriptionAdBinding
    lateinit var adapter: ImageAdapter
    private var ad: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDescriptionAdBinding = ActivityDescriptionAdBinding.inflate(layoutInflater)
        setContentView(activityDescriptionAdBinding.root)
        init()
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
        }
        getIntentFromMainActivity()
        imageChangeCounter()
    }

    private fun getIntentFromMainActivity() {
        ad = intent.getSerializableExtra("AD") as Ad
        if (ad != null) updateUIDescriptionAd(ad!!)
        // замена на
        // ad?.let { updateUIDescriptionAd(it) }
        // избавит от необходимости писать "!!"
    }


    private fun fillDescriptionAd(ad: Ad) = with(activityDescriptionAdBinding) {
        // почему это не dataBinding?..
        textViewTitleAd.text = ad.type
        textViewPriceAd.text = ad.price
        textViewContentDescriptionAd.text = ad.description
//        textViewContentInfoSalesman.text = ad.uid.displayName.toString()
    }

    private fun updateUIDescriptionAd(ad: Ad) {
        fillImageArray(ad, adapter)
        fillDescriptionAd(ad)
    }

    private fun sendCall() {
        val callUri = "tel:${ad?.phone}"
        val iCall = Intent(Intent.ACTION_DIAL)
        iCall.data = callUri.toUri()
        startActivity(iCall)
    }

    private fun sendEmail() {
        // имя переменной яблочное) но у тебя переменная должна отражать суть, не гонись так за сокращениями
        // собирание эмейла не задача активити, создай класс, который будет этим заниматься.
        // кроме того, строковые ресурсы - в ресурсы, если делать локализацию в таком виде, будет каша
        val iEmail = Intent(Intent.ACTION_SEND)
        iEmail.type = "message/rfc822"
        iEmail.apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(ad?.email))
            putExtra(Intent.EXTRA_SUBJECT, "Объявление ${ad?.type}")
            putExtra(Intent.EXTRA_TEXT, "Здравствуйте, меня интересует объявление ${ad?.type}!")
        }
        try {
            startActivity(Intent.createChooser(iEmail, "Открыть с "))
        } catch (e: ActivityNotFoundException) {
            // за такую обработку ошибок на работе будут бить))
            // раз уж отлавливаешь, то хоть лог выведи
        }
    }

    private fun imageChangeCounter() {
        activityDescriptionAdBinding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val imageCounter = "${position + 1}/${activityDescriptionAdBinding.viewPager.adapter?.itemCount}"
                activityDescriptionAdBinding.textViewImageCounter.text = imageCounter
            }
        })
    }
}