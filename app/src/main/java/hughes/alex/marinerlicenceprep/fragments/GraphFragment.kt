package hughes.alex.marinerlicenceprep.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.graph_fragment.view.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class GraphFragment : Fragment() {
    private var title: String? = null
    private var page: Int = 0

    companion object {
        fun newInstance(page: Int, title: String): GraphFragment {
            val fragmentFirst = GraphFragment()
            val args = Bundle()
            args.putInt("someInt", page)
            args.putString("someTitle", title)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = arguments!!.getInt("someInt", 0)
        title = arguments!!.getString("someTitle")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.graph_fragment, container, false)
        val entries = ArrayList<BarEntry>()

        entries.add(BarEntry(0f, 30f))
        entries.add(BarEntry(1f, 80f))
        entries.add(BarEntry(2f, 60f))
        entries.add(BarEntry(3f, 50f))
        entries.add(BarEntry(4f, 70f))
        entries.add(BarEntry(5f, 60f))


        val set = BarDataSet(entries, "")
        view.barChart.data=BarData(set)
        return view
    }
}