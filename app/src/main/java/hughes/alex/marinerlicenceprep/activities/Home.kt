package hughes.alex.marinerlicenceprep.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentActivity
import hughes.alex.marinerlicenceprep.AuthService
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.fragments.*
import kotlinx.android.synthetic.main.activity_home.*


class Home : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        navigationView.disableShiftMode()
        val firstTransition = supportFragmentManager.beginTransaction()
        firstTransition.replace(R.id.fragmentPlaceholder, HomeFragment())
        firstTransition.commit()
        navigationView.setOnNavigationItemSelectedListener { item ->
            fragmentPlaceholder.removeAllViews()
            when (item.itemId) {
                R.id.menu_home -> {
                    val transactionToHome = supportFragmentManager.beginTransaction()
                    transactionToHome.replace(R.id.fragmentPlaceholder, HomeFragment())
                    transactionToHome.commit()
                }
                R.id.menu_study -> {
                    val transactionToStudyFragment = supportFragmentManager.beginTransaction()
                    transactionToStudyFragment.replace(R.id.fragmentPlaceholder, StudyFragment())
                    transactionToStudyFragment.commit()
                }
                R.id.menu_search -> {
                    val transactionToSearchFragment = supportFragmentManager.beginTransaction()
                    transactionToSearchFragment.replace(R.id.fragmentPlaceholder, SearchFragment())
                    transactionToSearchFragment.commit()
                }
                R.id.menu_bookmarked -> {
                    val transactionToBookmarkedFragment = supportFragmentManager.beginTransaction()
                    transactionToBookmarkedFragment.replace(R.id.fragmentPlaceholder, BookmarkedFragment())
                    transactionToBookmarkedFragment.commit()
                }
                R.id.menu_settings -> {
                    val transactionToSettingsFragment = supportFragmentManager.beginTransaction()
                    transactionToSettingsFragment.replace(R.id.fragmentPlaceholder, SettingsFragment())
                    transactionToSettingsFragment.commit()
                }
            }
            return@setOnNavigationItemSelectedListener true
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
