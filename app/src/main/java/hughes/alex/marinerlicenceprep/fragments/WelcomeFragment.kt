package hughes.alex.marinerlicenceprep.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.database.Queries
import kotlinx.android.synthetic.main.welcome_fragment.view.*


class WelcomeFragment : Fragment() {
    companion object {
        fun newInstance(): WelcomeFragment {
            return WelcomeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.welcome_fragment, container, false)
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(20f, Queries.getStatisticsForBook(context!!, 1.toString())))
        entries.add(PieEntry(30f, Queries.getStatisticsForBook(context!!, 2.toString())))
        entries.add(PieEntry(40f, Queries.getStatisticsForBook(context!!, 2.toString())))
        entries.add(PieEntry(50f, Queries.getStatisticsForBook(context!!, 2.toString())))
        val set = PieDataSet(entries, "")
        set.colors = ColorTemplate.JOYFUL_COLORS.asList()
        view.pieChart.data= PieData(set)

        view.pieChart.animateY(2000, Easing.EasingOption.EaseOutBack)
        return view
    }
}