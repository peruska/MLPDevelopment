package hughes.alex.marinerlicenceprep.uiAdapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import hughes.alex.marinerlicenceprep.fragments.GraphFragment
import hughes.alex.marinerlicenceprep.fragments.WeakestQuestionsFragment
import hughes.alex.marinerlicenceprep.fragments.WelcomeFragment

class ViewPagerAdapter(fragmentManager: FragmentManager?) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> WelcomeFragment()
            1 -> GraphFragment()
            2 -> WeakestQuestionsFragment()
            else -> null
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return ""
    }
}