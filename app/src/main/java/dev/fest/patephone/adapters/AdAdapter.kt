package dev.fest.patephone.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.fest.patephone.MainActivity
import dev.fest.patephone.R
import dev.fest.patephone.act.EditAdsAct
import dev.fest.patephone.databinding.AdListItemBinding
import dev.fest.patephone.model.Ad

class AdAdapter(val activity: MainActivity) : RecyclerView.Adapter<AdAdapter.AdHolder>() {

    val arrayAd = ArrayList<Ad>() //для хранения списка  с бд

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
//            val getCountryString = R.string.select_country.toString()

            textViewTitle.text = ad.nameInstrument
//            if (textViewCountry.text == getCountryString) {
//                textViewCountry.text = ""
//            } else {
//                textViewCountry.text = ad.country
//            }
            textViewCountry.text = ad.country
            textViewCity.text = ad.city
            textViewTitlePrice.text = ad.price

            textViewViewCounter.text = ad.viewsCounter
            textViewFavourite.text = ad.favCounter
            Picasso.get().load(ad.mainImage).into(imageViewContent)
            isFav(ad)
            showEditOwnerPanel(isOwner(ad))
            mainOnClick(ad)
//            showCountry(ad)
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

        // название метода целиком, такие сокращения выглядят неоправдано
        private fun isFav(ad: Ad) {
            if (ad.isFav) {
                binding.imageButtonFavourite.setImageResource(R.drawable.ic_favourite_pressed)
            } else {
                binding.imageButtonFavourite.setImageResource(R.drawable.ic_favourite_normal)
            }
            // можно заменить на:
/*            val icon = if (ad.isFav) {
                R.drawable.ic_favourite_pressed
            } else {
                R.drawable.ic_favourite_normal
            }
            binding.imageButtonFavourite.setImageResource(icon)*/
        }

        private fun onClickEdit(ad: Ad): View.OnClickListener {
            return View.OnClickListener {
                val editIntent = Intent(activity, EditAdsAct::class.java).apply {
                    putExtra(MainActivity.EDIT_STATE, true)
                    putExtra(MainActivity.AD_DATA, ad)
                }
                activity.startActivity(editIntent)
            }
        }

        private fun isOwner(ad: Ad): Boolean {
            return ad.uid == activity.mAuth.uid
        }
//
//        private fun showCountry(ad: Ad) = with(binding) {
//            if (textViewCountry.text != R.string.select_country.toString()) {
//                textViewCountry.text = ad.country
//                Log.d("AdAdapter", "select: ${textViewCountry.text}")
//            } else {
//                Log.d("AdAdapter", "not select: ${textViewCountry.text}")
//
//                textViewCountry.text = ""
//            }
//        }

        private fun showEditOwnerPanel(isOwner: Boolean) {
            if (isOwner) {
                binding.editOwnerPanel.visibility = View.VISIBLE
            } else {
                binding.editOwnerPanel.visibility = View.GONE
            }
        }

        interface ItemListener {
            fun onDeleteItem(ad: Ad)
            fun onAdViewed(ad: Ad)
            fun onFavClicked(ad: Ad)
        }
    }
}