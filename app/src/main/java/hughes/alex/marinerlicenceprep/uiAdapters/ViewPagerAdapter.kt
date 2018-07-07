package hughes.alex.marinerlicenceprep.uiAdapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter
import hughes.alex.marinerlicenceprep.fragments.GraphFragment
import hughes.alex.marinerlicenceprep.fragments.WeakestQuestionsFragment
import hughes.alex.marinerlicenceprep.fragments.WelcomeFragment

class ViewPagerAdapter(fragmentManager: FragmentManager?) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return NUM_ITEMS
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 // Fragment # 0 - This will show FirstFragment
            -> WelcomeFragment.newInstance()
            1 // Fragment # 0 - This will show FirstFragment different title
            -> GraphFragment.newInstance(1, "Page # 2")
            2 // Fragment # 1 - This will show SecondFragment
            -> WeakestQuestionsFragment.newInstance(2, "Page # 3")
            else -> null
        }
    }

    // Returns the page title for the top indicator
    override fun getPageTitle(position: Int): CharSequence? {
        return ""
    }

    companion object {
        private val NUM_ITEMS = 3
    }

}