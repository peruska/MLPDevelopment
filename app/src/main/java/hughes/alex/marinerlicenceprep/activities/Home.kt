package hughes.alex.marinerlicenceprep.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import hughes.alex.marinerlicenceprep.AuthService
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.fragments.*
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.doAsync


class Home : FragmentActivity() {
    var activeFragment: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        AuthService(this).retrieveUserInfo()
        navigationView.disableShiftMode()
        val firstTransition = supportFragmentManager.beginTransaction()
        activeFragment = HomeFragment()
        firstTransition.replace(R.id.fragmentPlaceholder, activeFragment)
        firstTransition.commit()
        navigationView.setOnNavigationItemSelectedListener { item ->
            fragmentPlaceholder.removeAllViews()
            supportFragmentManager.popBackStack()
            when (item.itemId) {
                R.id.menu_home -> {
                    activeFragment = HomeFragment()
                }
                R.id.menu_study -> {
                    activeFragment = StudyFragment()
                }
                R.id.menu_search -> {
                    activeFragment = SearchFragment()
                }
                R.id.menu_bookmarked -> {
                    activeFragment = BookmarkedFragment()
                }
                R.id.menu_settings -> {
                    activeFragment = SettingsFragment()
                }
            }
            val transactionToFragment = supportFragmentManager.beginTransaction()
            transactionToFragment.replace(R.id.fragmentPlaceholder, activeFragment)
            transactionToFragment.commit()
            return@setOnNavigationItemSelectedListener true
        }

        doAsync {
            (application as MyApp).fetchLicenseBooksAsListItem()
        }
    }


    private fun BottomNavigationView.disableShiftMode() {
        val menuView = getChildAt(0) as BottomNavigationMenuView

        menuView.javaClass.getDeclaredField("mShiftingMode").apply {
            isAccessible = true
            setBoolean(menuView, false)
            isAccessible = false
        }

        @SuppressLint("RestrictedApi")
        for (i in 0 until menuView.childCount) {
            (menuView.getChildAt(i) as BottomNavigationItemView).apply {
                setShiftingMode(false)
                setChecked(false)
            }
        }
    }
}
