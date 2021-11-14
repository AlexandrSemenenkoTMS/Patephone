package dev.fest.patephone.utils

import android.app.Activity
import com.google.android.material.navigation.NavigationView
import dev.fest.patephone.R

object DrawerMenuItemViewManager {

    private fun showDrawerItem(
        activity: Activity,
        isVisibleSignUp: Boolean,
        isVisibleSignIn: Boolean,
        isVisibleSignOut: Boolean
    ) {
        val navigationView: NavigationView = activity.findViewById(R.id.navView)
        val navMenu = navigationView.menu

        navMenu.findItem(R.id.id_sign_up).isVisible = isVisibleSignUp
        navMenu.findItem(R.id.id_sign_in).isVisible = isVisibleSignIn
        navMenu.findItem(R.id.id_sign_out).isVisible = isVisibleSignOut
    }

    fun showItemWhenLogin(activity: Activity) {
        showDrawerItem(activity,
            isVisibleSignUp = false,
            isVisibleSignIn = false,
            isVisibleSignOut = true
        )
    }

    fun showItemWhenLogout(activity: Activity) {
        showDrawerItem(activity,
            isVisibleSignUp = true,
            isVisibleSignIn = true,
            isVisibleSignOut = false
        )
    }
}