package dev.fest.patephone.act

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import dev.fest.patephone.R
import dev.fest.patephone.databinding.ActivityFilterBinding
import dev.fest.patephone.dialogs.DialogSpinnerHelper
import dev.fest.patephone.utils.CityHelper
import java.lang.StringBuilder

class FilterActivity : AppCompatActivity() {
    private lateinit var activityFilterBinding: ActivityFilterBinding

    private val dialogSpinnerHelper = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityFilterBinding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(activityFilterBinding.root)
        actionBarSettings()
        onClickSelectCountry()
        onClickSelectCity()
        onClickSelectTypeInstrument()
        onClickDone()
        getFilter()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun onClickSelectCountry() = with(activityFilterBinding) {
        textViewSelectCountry.setOnClickListener {
            val listCountry = CityHelper.getAllCountries(this@FilterActivity)
            dialogSpinnerHelper.showSpinnerDialog(
                this@FilterActivity,
                listCountry,
                textViewSelectCountry
            )
            textViewSelectCountry.setTextColor(Color.BLACK)
            if (textViewSelectCity.text.toString() != getString(R.string.select_city)) {
                textViewSelectCity.setText(R.string.select_city)
            }
        }

    }

    private fun onClickSelectCity() = with(activityFilterBinding) {
        textViewSelectCity.setOnClickListener {
            val selectedCountry = textViewSelectCountry.text.toString()
            if (selectedCountry != getString(R.string.select_country)) {
                val listCity = CityHelper.getAllCities(selectedCountry, this@FilterActivity)
                dialogSpinnerHelper.showSpinnerDialog(
                    this@FilterActivity,
                    listCity,
                    textViewSelectCity
                )
            } else {
                textViewSelectCountry.setTextColor(Color.RED)
            }
        }
    }

    private fun onClickSelectTypeInstrument() = with(activityFilterBinding) {
        textViewSelectTypeInstrument.setOnClickListener {
            val listVacancy = resources.getStringArray(R.array.typeInstrument)
                .toMutableList() as ArrayList<String>
            dialogSpinnerHelper.showSpinnerDialog(
                this@FilterActivity,
                listVacancy,
                textViewSelectTypeInstrument
            )
        }
    }

    private fun onClickDone() = with(activityFilterBinding) {
        buttonDoneFilter.setOnClickListener {
            val intent = Intent().apply {
                putExtra(FILTER_KEY, createFilter())
            }
            setResult(RESULT_OK, intent)
            finish()
        }

    }

    private fun createFilter(): String = with(activityFilterBinding) {
        val stringBuilder = StringBuilder()
        val arrayTempFilter = listOf(
            textViewSelectCountry.text,
            textViewSelectCity.text,
            textViewSelectTypeInstrument.text,
            editTextPrice.text
        )
        for ((i, s) in arrayTempFilter.withIndex()) {
            if (s != getString(R.string.select_country)
                && s != getString(R.string.select_city)
                && s != getString(R.string.select_type_instrument)
                && s.isNotEmpty()
            ) {
                stringBuilder.append(s)
                if (i != arrayTempFilter.size - 1) stringBuilder.append("_")
            } else {
                stringBuilder.append("empty")
                if (i != arrayTempFilter.size - 1) stringBuilder.append("_")
            }
        }
        return stringBuilder.toString()
    }

    fun actionBarSettings() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getFilter() = with(activityFilterBinding) {
        val filter = intent.getStringExtra(FILTER_KEY)
        if (filter != null && filter != "empty") {
            val filterArray = filter.split("_")
            if (filterArray[0] != getString(R.string.select_country)) textViewSelectCountry.text = filterArray[0]
            if (filterArray[1] != getString(R.string.select_city)) textViewSelectCity.text = filterArray[1]
            if (filterArray[2] != getString(R.string.select_type_instrument)) textViewSelectTypeInstrument.text = filterArray[2]
            if (filterArray[3] != "empty") editTextPrice.setText(filterArray[3])

        }
    }

    companion object {
        const val FILTER_KEY = "filter_key"
    }
}