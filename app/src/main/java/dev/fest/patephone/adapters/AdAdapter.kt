package dev.fest.patephone.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.fest.patephone.activity.MainActivity
import dev.fest.patephone.R
import dev.fest.patephone.activity.EditAdsActivity
import dev.fest.patephone.databinding.AdListItemBinding
import dev.fest.patephone.model.Ad

class AdAdapter(val activity: MainActivity) : RecyclerView.Adapter<AdAdapter.AdHolder>() {

    private val arrayAd = ArrayList<Ad>() //для хранения списка  с бд

    //создание шаблона с разметки
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder {
        val binding =
            AdListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdHolder(binding, activity)
    }

    override fun onBindViewHolder(holder: AdHolder, position: Int) {
        holder.setData(arrayAd[position])


    }

    override fun getItemCount() = arrayAd.size

    fun updateAdapter(list: List<Ad>) {
        val tempArray = ArrayList<Ad>()
        tempArray.addAll(arrayAd)
        tempArray.addAll(list)

        val diffResult = DiffUtil.calculateDiff(DiffUtilHelper(arrayAd, tempArray))
        diffResult.dispatchUpdatesTo(this)
        arrayAd.clear()
        arrayAd.addAll(tempArray)
    }

    fun updateAdapterWithClear(list: List<Ad>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtilHelper(arrayAd, list))
        diffResult.dispatchUpdatesTo(this)
        arrayAd.clear()
        arrayAd.addAll(list)
    }

    class AdHolder(val binding: AdListItemBinding, val activity: MainActivity) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(ad: Ad) = with(binding) {
            textViewTitle.text = ad.nameInstrument
            textViewCountry.text = ad.country
            textViewCity.text = ad.city
            textViewTitlePrice.text = ad.price
            textViewTitleTypeMoney.text = ad.typeMoney
            textViewViewCounter.text = ad.viewsCounter
            textViewFavourite.text = ad.favCounter

            Glide
                .with(root)
                .load(
                    if (ad.mainImage == "empty") {
                        R.drawable.ic_default_image
                    } else {
                        ad.mainImage
                    }
                )
                .into(imageViewContent)

            isFav(ad)
            showEditOwnerPanel(isOwner(ad))
            mainOnClick(ad)
            showIconPlace(ad.country, ad.city)
        }

        private fun mainOnClick(ad: Ad) = with(binding) {
            imageButtonFavourite.setOnClickListener {
                if (activity.mAuth.currentUser?.isAnonymous == false) activity.onFavClicked(ad)
            }
            imageButtonEditVacancy.setOnClickListener(onClickEdit(ad))
            imageButtonDeleteVacancy.setOnClickListener { activity.onDeleteItem(ad) }
            itemView.setOnClickListener {
                activity.onAdViewed(ad)
            }
        }

        private fun isFav(ad: Ad) {
            val icon = if (ad.isFavourite) {
                R.drawable.ic_favourite_pressed
            } else {
                R.drawable.ic_favourite_normal
            }
            binding.imageButtonFavourite.setImageResource(icon)
        }

        private fun onClickEdit(ad: Ad): View.OnClickListener {
            return View.OnClickListener {
                val editIntent = Intent(activity, EditAdsActivity::class.java).apply {
                    putExtra(MainActivity.EDIT_STATE, true)
                    putExtra(MainActivity.AD_DATA, ad)
                }
                activity.startActivity(editIntent)
            }
        }

        private fun isOwner(ad: Ad): Boolean {
            return ad.uid == activity.mAuth.uid
        }

        private fun showEditOwnerPanel(isOwner: Boolean) {
            if (isOwner) {
                binding.editOwnerPanel.visibility = View.VISIBLE
            } else {
                binding.editOwnerPanel.visibility = View.GONE
            }
        }

        private fun showIconPlace(country: String?, city: String?) {
            if (country!!.isEmpty() && city!!.isEmpty()) {
                binding.imageViewPlace.visibility = View.GONE
            } else {
                binding.imageViewPlace.visibility = View.VISIBLE
            }
        }

        interface ItemListener {
            fun onDeleteItem(ad: Ad)
            fun onAdViewed(ad: Ad)
            fun onFavClicked(ad: Ad)
        }
    }
}