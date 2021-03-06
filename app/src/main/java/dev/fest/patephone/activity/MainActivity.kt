package dev.fest.patephone.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import dev.fest.patephone.R
import dev.fest.patephone.accounthelper.AccountHelper
import dev.fest.patephone.adapters.AdAdapter
import dev.fest.patephone.databinding.ActivityMainBinding
import dev.fest.patephone.dialoghelper.DialogConst
import dev.fest.patephone.dialoghelper.DialogHelper
import dev.fest.patephone.model.Ad
import dev.fest.patephone.utils.DrawerMenuItemViewManager
import dev.fest.patephone.utils.FilterManager
import dev.fest.patephone.viewmodel.FirebaseViewModel


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    AdAdapter.AdHolder.ItemListener {
    private lateinit var binding: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    private lateinit var textViewAccount: TextView
    private lateinit var imageViewAccount: ImageView
    lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var filterLauncher: ActivityResultLauncher<Intent>
    val mAuth = Firebase.auth
    val adapter = AdAdapter(this)
    private val firebaseViewModel: FirebaseViewModel by viewModels()
    private var clearUpdate: Boolean = true
    private var currentType: String? = null
    private var filter: String = "empty"
    private var filterDb: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initMainContentRecyclerView()
        initViewModel()
        scrollListener()
        firebaseViewModel.loadAllAdsFirstPage(filterDb)
        bottomMenuOnClick()
        onActivityResultFilter()
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    override fun onResume() {
        super.onResume()
        binding.mainContent.bottomNavView.selectedItemId = R.id.id_home
    }

    private fun onActivityResult() {
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    dialogHelper.accountHelper.signInFirebaseWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.d("MyLog", "Api error: ${e.message}")
            }
        }
    }

    private fun onActivityResultFilter() {
        filterLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    filter = it.data?.getStringExtra(FilterActivity.FILTER_KEY)!!
                    Log.d("MyLog", "filter: $filter")
                    Log.d("MyLog", "getFilter: ${FilterManager.getFilter(filter)}")
                    filterDb = FilterManager.getFilter(filter)
                } else if (it.resultCode == RESULT_CANCELED) {
                    filterDb = ""
                    filter = "empty"

                }
            }
    }

    private fun initViewModel() {
        firebaseViewModel.liveDataAd.observe(this, {
            val list = getAdsByCategory(it)
            if (!clearUpdate) {
                adapter.updateAdapter(list)
            } else {
                adapter.updateAdapterWithClear(list)
            }
            binding.mainContent.textViewEmpty.visibility =
                if (adapter.itemCount == 0) View.VISIBLE else View.GONE
        })
    }


    private fun getAdsByCategory(list: ArrayList<Ad>): ArrayList<Ad> {
        val tempList = ArrayList<Ad>()
        tempList.addAll(list)
        if (currentType != getString(R.string.ad_all_ads)) {
            tempList.clear()
            list.forEach {
                if (currentType == it.type) tempList.add(it)
            }
        }
        tempList.reverse()
        return tempList
    }

    private fun init() {
        currentType = getString(R.string.ad_all_ads)
        setSupportActionBar(binding.mainContent.toolbar)
        binding.mainContent.toolbar.subtitle = getString(R.string.ad_all_ads)
        navViewSettings()
        onActivityResult()
        val toggle =
            ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.mainContent.toolbar,
                R.string.open,
                R.string.close
            )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        textViewAccount = binding.navView.getHeaderView(0).findViewById(R.id.textViewAccountEmail)
        imageViewAccount = binding.navView.getHeaderView(0).findViewById(R.id.imageViewAccountImage)

        binding.mainContent.floatingActionButtonFilter.setOnClickListener {
            val intent = Intent(this@MainActivity, FilterActivity::class.java).apply {
                putExtra(FilterActivity.FILTER_KEY, filter)
            }
            filterLauncher.launch(intent)
        }
    }

    private fun bottomMenuOnClick() = with(binding) {
        mainContent.bottomNavView.setOnNavigationItemSelectedListener { item ->
            clearUpdate = true
            when (item.itemId) {

                R.id.id_home -> {
                    currentType = getString(R.string.ad_all_ads)
                    firebaseViewModel.loadAllAdsFirstPage(filterDb)
                    mainContent.toolbar.title = getString(R.string.ad_title_sale)
                    mainContent.toolbar.subtitle = getString(R.string.ad_all_ads)
                }
                R.id.id_fav -> {
                    firebaseViewModel.loadMyFavs()
                    mainContent.toolbar.title = getString(R.string.my_favs)
                    mainContent.toolbar.subtitle = null
                }
                R.id.id_add -> {
                    val intent = Intent(this@MainActivity, EditAdsActivity::class.java)
                    if (mAuth.currentUser?.isAnonymous == true || mAuth.currentUser == null) {
                        dialogHelper.createSignDialog(DialogConst.SIGN_UP_STATE)
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.message_for_register),
                            Toast.LENGTH_LONG
                        )
                            .show()
                    } else startActivity(intent)
                }
//                R.id.id_chat -> {
//                }
//                R.id.id_account -> {
//                    if (mAuth.currentUser?.isAnonymous == true || mAuth.currentUser == null) {
////                        dialogHelper.createSignDialog(DialogConst.SIGN_UP_STATE)
//                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
//                        startActivity(intent)
//                    } else {
//                        openLoginFragment()
//                        Toast.makeText(
//                            this@MainActivity,
//                            "OK",
//                            Toast.LENGTH_LONG
//                        )
//                            .show()
//                    }
//                }
            }
            true
        }
    }

    private fun initMainContentRecyclerView() {
        binding.apply {
            mainContent.recyclerViewContent.layoutManager = LinearLayoutManager(this@MainActivity)
            mainContent.recyclerViewContent.adapter = adapter
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        clearUpdate = true
        when (item.itemId) {

            R.id.id_my_ads -> {
                firebaseViewModel.loadMyAd()
                binding.mainContent.toolbar.subtitle = getString(R.string.ad_my_ads)
            }
            R.id.id_sign_up -> {
                dialogHelper.createSignDialog(DialogConst.SIGN_UP_STATE)
            }
            R.id.id_sign_in -> {
                dialogHelper.createSignDialog(DialogConst.SIGN_IN_STATE)
            }
            R.id.id_sign_out -> {
                if (mAuth.currentUser?.isAnonymous == true) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    return true
                }
                uiUpdate(null)
                mAuth.signOut()
                dialogHelper.accountHelper.signOutClientWithGoogle()
            }

            R.id.ad_title_sale_guitar -> {
                getAdsFromType(getString(R.string.ad_title_sale_guitar))
                binding.mainContent.toolbar.subtitle =
                    getString(R.string.ad_title_sale_guitar)
            }
            R.id.ad_title_sale_keyboards -> {
                getAdsFromType(getString(R.string.ad_title_sale_keyboards))
                binding.mainContent.toolbar.subtitle =
                    getString(R.string.ad_title_sale_keyboards)
            }
            R.id.ad_title_sale_drums -> {
                getAdsFromType(getString(R.string.ad_title_sale_drums))
                binding.mainContent.toolbar.subtitle =
                    getString(R.string.ad_title_sale_drums)
            }
            R.id.ad_title_sale_strings -> {
                getAdsFromType(getString(R.string.ad_title_sale_strings))
                binding.mainContent.toolbar.subtitle =
                    getString(R.string.ad_title_sale_strings)
            }
            R.id.ad_title_sale_spiritual -> {
                getAdsFromType(getString(R.string.ad_title_sale_spiritual))
                binding.mainContent.toolbar.subtitle =
                    getString(R.string.ad_title_sale_spiritual)
            }
            R.id.ad_title_sale_accessories -> {
                getAdsFromType(getString(R.string.ad_title_sale_accessories))
                binding.mainContent.toolbar.subtitle =
                    getString(R.string.ad_title_sale_accessories)
            }
            R.id.ad_title_sale_other -> {
                getAdsFromType(getString(R.string.ad_title_sale_other))
                binding.mainContent.toolbar.subtitle =
                    getString(R.string.ad_title_sale_other)
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getAdsFromType(type: String) {
        currentType = type
        firebaseViewModel.loadAllAdsFromType(type, filterDb)
    }

    fun uiUpdate(user: FirebaseUser?) {
        if (user == null) {
            dialogHelper.accountHelper.signInAnonymously(object : AccountHelper.Listener {
                override fun onComplete() {
                    DrawerMenuItemViewManager.showItemWhenLogout(this@MainActivity)
                    textViewAccount.text = getString(R.string.title_guest)
                    imageViewAccount.setImageResource(R.drawable.ic_account_default)
                }
            })
        } else if (user.isAnonymous) {
            DrawerMenuItemViewManager.showItemWhenLogout(this)
            textViewAccount.text = getString(R.string.title_guest)
            imageViewAccount.setImageResource(R.drawable.ic_account_default)
        } else if (!user.isAnonymous) {
            DrawerMenuItemViewManager.showItemWhenLogin(this)
            textViewAccount.text = user.displayName
            Picasso.get().load(user.photoUrl).into(imageViewAccount)
        }
    }

//    fun openLoginFragment() {
//        loginFragment = LoginFragment()
//        val fm = supportFragmentManager.beginTransaction()
//        fm.replace(R.id.placeHolder, loginFragment!!)
//        fm.commit()
//    }

    override fun onDeleteItem(ad: Ad) {
        firebaseViewModel.deleteAd(ad)
    }

    override fun onAdViewed(ad: Ad) {
        firebaseViewModel.adViewed(ad)
        val i = Intent(this, DescriptionAdActivity::class.java)
        i.putExtra(AD, ad)
        startActivity(i)
    }

    override fun onFavClicked(ad: Ad) {
        firebaseViewModel.onFavouriteClick(ad)
    }

    private fun navViewSettings() = with(binding) {
        val menu = navView.menu
        val titleAccount = menu.findItem(R.id.title_account)
        val titleSale = menu.findItem(R.id.ad_title_sale)
        val spanTitleAccount = SpannableString(titleAccount.title)
        val spanTitleSale = SpannableString(titleSale.title)
        spanTitleAccount.setSpan(
            ForegroundColorSpan
                (ContextCompat.getColor(this@MainActivity, R.color.colorPrimary)),
            0,
            titleAccount.title.length,
            0
        )
        spanTitleSale.setSpan(
            ForegroundColorSpan
                (ContextCompat.getColor(this@MainActivity, R.color.colorPrimary)),
            0,
            titleSale.title.length,
            0
        )
        titleAccount.title = spanTitleAccount
        titleSale.title = spanTitleSale
    }

    private fun scrollListener() = with(binding.mainContent) {
        recyclerViewContent.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(SCROLL_DOWN) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    clearUpdate = false
                    val adsList = firebaseViewModel.liveDataAd.value!!
                    if (adsList.isNotEmpty()) {
                        getAdsFromType(adsList)
                    }
                }
            }
        })
    }

    private fun getAdsFromType(adsList: List<Ad>) {
        adsList[0].let {
            if (currentType == getString(R.string.ad_all_ads)) {
                firebaseViewModel.loadAllAdsNextPage(it.timePublishAd)
            } else {
                val typeTime = "${it.type}_${it.timePublishAd}"
                firebaseViewModel.loadAllAdsFromTypeNextPage(typeTime)
            }
        }
    }

    companion object {
        const val EDIT_STATE = "edit_state"
        const val AD_DATA = "ad_data"
        const val AD = "AD"
        const val SCROLL_DOWN = 1
    }

}