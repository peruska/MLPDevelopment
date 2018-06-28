package hughes.alex.marinerlicenceprep.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.uiAdapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.home_fragment.view.*

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)
        view.viewPager.adapter = ViewPagerAdapter(fragmentManager)
        view.tabDots.setupWithViewPager(view.viewPager, true)
        return view
    }

}