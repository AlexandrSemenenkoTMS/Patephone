package dev.fest.patephone.activity

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.fest.patephone.R
import dev.fest.patephone.adapters.ImageAdapter
import dev.fest.patephone.model.Ad
import dev.fest.patephone.model.DbManager
import dev.fest.patephone.databinding.ActivityEditAdsBinding
import dev.fest.patephone.dialogs.DialogSpinnerHelper
import dev.fest.patephone.fragment.FragmentCloseInterface
import dev.fest.patephone.fragment.ImageListFragment
import dev.fest.patephone.utils.CityHelper
import dev.fest.patephone.utils.ImagePicker
import java.io.ByteArrayOutputStream

import android.widget.ArrayAdapter
import dev.fest.patephone.utils.ImageManager.fillImageArray


class EditAdsActivity : AppCompatActivity(), FragmentCloseInterface {

    var chooseImageFragment: ImageListFragment? = null
    lateinit var activityEditAdsBinding: ActivityEditAdsBinding
    private val dialogSpinnerHelper = DialogSpinnerHelper()
    lateinit var imageAdapter: ImageAdapter
    private val dbManager = DbManager()
    var editImagePosition = 0
    private var imageIndex = 0
    private var isEditState = false
    private var ad: Ad? = null
    private val mAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityEditAdsBinding = ActivityEditAdsBinding.inflate(layoutInflater)
        setContentView(activityEditAdsBinding.root)
        init()
        checkEditState()
        imageChangeCounter()
    }

    private fun checkEditState() {
        isEditState = isEditState()
        if (isEditState) {
            ad = intent.getSerializableExtra(MainActivity.AD_DATA) as Ad
            if (ad != null) fillViews(ad!!)
            activityEditAdsBinding.buttonAddAd.text = getString(R.string.button_apply)
        }
    }


    //проверяет состояние объявления для редактирования
    private fun isEditState(): Boolean {
        return intent.getBooleanExtra(MainActivity.EDIT_STATE, false)
    }

    private fun fillViews(ad: Ad) = with(activityEditAdsBinding) {
        textViewSelectCountry.text = ad.country
        textViewSelectCity.text = ad.city
        editTextTelephone.setText(ad.phone)
        textViewSelectTypeInstrument.text = ad.type
        editTextNameInstrument.setText(ad.nameInstrument)
        editTextPrice.setText(ad.price)
        editTextDescriptionInstrument.setText(ad.description)
        fillImageArray(ad, imageAdapter)
    }

    private fun init() {
        imageAdapter = ImageAdapter()
        activityEditAdsBinding.viewPagerImages.adapter = imageAdapter
        onClickSelectTypeMoney()
    }

    //onClicks
    fun onClickSelectCountry(view: View) {
        val listCountry = CityHelper.getAllCountries(this)
        dialogSpinnerHelper.showSpinnerDialog(
            this,
            listCountry,
            activityEditAdsBinding.textViewSelectCountry
        )
        activityEditAdsBinding.textViewSelectCountry.setTextColor(Color.BLACK)
        if (activityEditAdsBinding.textViewSelectCity.text.toString() != getString(R.string.select_city)) {
            activityEditAdsBinding.textViewSelectCity.setText(R.string.select_city)
        }
    }

    fun onClickSelectCity(view: View) {
        val selectedCountry = activityEditAdsBinding.textViewSelectCountry.text.toString()
        if (selectedCountry != getString(R.string.select_country)) {
            val listCity = CityHelper.getAllCities(selectedCountry, this)
            dialogSpinnerHelper.showSpinnerDialog(
                this,
                listCity,
                activityEditAdsBinding.textViewSelectCity
            )
        } else {
            activityEditAdsBinding.textViewSelectCountry.setTextColor(Color.RED)
        }
    }

    private fun onClickSelectTypeMoney() {
        val spinner = activityEditAdsBinding.spinnerSelectTypeMoney
        ArrayAdapter.createFromResource(
            this,
            R.array.typeMoney,
            R.layout.spinner_style
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    fun onClickSelectTypeInstrument(view: View) {
        val listInstrument =
            resources.getStringArray(R.array.typeInstrument).toMutableList() as ArrayList<String>
        dialogSpinnerHelper.showSpinnerDialog(
            this,
            listInstrument,
            activityEditAdsBinding.textViewSelectTypeInstrument
        )
    }

    //запуск выбора фото
    fun onClickGetImages(view: View) {
        if (imageAdapter.mainList.size == 0) {
            ImagePicker.getMultiImages(this, 3)
            Log.d("EditAdsAct", "size 0")
        } else {
            openChooseImageFragment(null)
            chooseImageFragment?.updateAdapterFromEdit(imageAdapter.mainList)
            Log.d("EditAdsAct", "size not 0")
        }
    }


    fun onClickPublishAd(view: View) {
        ad = mAuth.currentUser?.let { fillAd(it) }
        if (isEditState) {
            ad?.copy(adKey = ad?.adKey)
                ?.let { dbManager.publishAd(it, onPublishFinish()) }
        } else {
            uploadImages()
        }
    }

    private fun onPublishFinish(): DbManager.FinishWorkListener {
        return object : DbManager.FinishWorkListener {
            override fun onFinish() {
                finish()
            }
        }
    }

    //заполнение объявки
    private fun fillAd(user: FirebaseUser): Ad {
        val ad: Ad
        activityEditAdsBinding.apply {
            val textPrice = if (editTextPrice.text.isNullOrEmpty()) {
                "Договорная"
            } else {
                editTextPrice.text.toString()
            }

            val textTypeMoney = if (editTextPrice.text.isNullOrEmpty()) {
                ""
            } else {
                spinnerSelectTypeMoney.selectedItem.toString()
            }

            val textCountry =
                if (textViewSelectCountry.text.isNullOrEmpty() || textViewSelectCountry.text == getString(
                        R.string.select_country
                    )
                ) {
                    ""
                } else {
                    textViewSelectCountry.text.toString()
                }
            val textCity =
                if (textViewSelectCity.text.isNullOrEmpty() || textViewSelectCity.text == getString(
                        R.string.select_city
                    )
                ) {
                    ""
                } else {
                    textViewSelectCity.text.toString()
                }

            ad = Ad(
                dbManager.db.push().key,
                textCountry,
                textCity,
                editTextTelephone.text.toString(),
                textViewSelectTypeInstrument.text.toString(),
                editTextNameInstrument.text.toString(),
                textPrice,
                textTypeMoney,
//                spinnerSelectTypeMoney.selectedItem.toString(),
                editTextDescriptionInstrument.text.toString(),
                "empty",
                "empty",
                "empty",
                dbManager.auth.uid,
                user.email.toString(),
                user.displayName,
                user.photoUrl.toString(),
                System.currentTimeMillis().toString()
            )
        }
        return ad
    }

    //запускается, когда onDetach
    override fun onFragmentClose(list: ArrayList<Bitmap>) {
        activityEditAdsBinding.scrollView.visibility = View.VISIBLE
        imageAdapter.updateAdapter(list)
        chooseImageFragment = null
    }

    fun openChooseImageFragment(newList: ArrayList<Uri>?) {
        chooseImageFragment = ImageListFragment(this)
        if (newList != null) chooseImageFragment?.resizeSelectedImages(newList, true, this)
        activityEditAdsBinding.scrollView.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.placeHolder, chooseImageFragment!!)
        fm.commit()
    }

    private fun uploadImages() {
        if (imageAdapter.mainList.size == imageIndex) {
            dbManager.publishAd(ad!!, onPublishFinish())
            return
        }
        val byteArray = prepareImageByteArray(imageAdapter.mainList[imageIndex])
        uploadImage(byteArray) { uri ->
            dbManager.publishAd(ad!!, onPublishFinish())
            nextImage(uri.result.toString())
        }
    }

    private fun nextImage(uri: String) {
        setImageUriToAd(uri)
        imageIndex++
        uploadImages()
    }

    private fun setImageUriToAd(uri: String) {
        when (imageIndex) {
            0 -> ad = ad?.copy(mainImage = uri)
            1 -> ad = ad?.copy(image2 = uri)
            2 -> ad = ad?.copy(image3 = uri)
        }
    }

    private fun prepareImageByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
        return outputStream.toByteArray()
    }

    private fun uploadImage(byteArray: ByteArray, listener: OnCompleteListener<Uri>) {
        val imageStorageReference = dbManager.dbStorage
            .child(dbManager.auth.uid!!)
            .child("image_${System.currentTimeMillis()}")
        val uploadTask = imageStorageReference.putBytes(byteArray)
        uploadTask.continueWithTask { imageStorageReference.downloadUrl }
            .addOnCompleteListener(listener)
    }

    private fun imageChangeCounter() {
        activityEditAdsBinding.viewPagerImages.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val imageCounter =
                    "${position + 1}/${activityEditAdsBinding.viewPagerImages.adapter?.itemCount}"
                activityEditAdsBinding.textViewImageCounter.text = imageCounter
            }
        })
    }
}