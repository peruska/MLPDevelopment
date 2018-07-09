package hughes.alex.marinerlicenceprep.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
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
        view.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
               if(position == 2){
                   view.spinnerToolbar.visibility = View.VISIBLE
                   view.spinnerWeakestQuestionCategories.setSelection(0)
               }
                else
                   view.spinnerToolbar.visibility = View.INVISIBLE
            }

        })
        view.tabDots.setupWithViewPager(view.viewPager, true)
        return view
    }
}