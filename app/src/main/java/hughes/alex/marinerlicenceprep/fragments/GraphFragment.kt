package hughes.alex.marinerlicenceprep.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.entity.Book
import hughes.alex.marinerlicenceprep.models.BooksCategoriesSubcategories
import hughes.alex.marinerlicenceprep.models.StudyExpandableListItem
import kotlinx.android.synthetic.main.graph_fragment.view.*
import com.github.mikephil.charting.components.XAxis



class GraphFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.graph_fragment, container, false)
        val entries = ArrayList<BarEntry>()
        val prefs = context!!.getSharedPreferences(MyApp.USER_LICENSE_DATA_VALUES, 0)
        StudyFragment.dlNumber = prefs.getString(MyApp.DL_NUMBER, "")
        StudyFragment.bookCategoryID = prefs.getString(MyApp.CATEGORY, "")
        val json = prefs.getString(MyApp.USER_LICENSE_DATA_VALUES, "")
        val bookList: ArrayList<Book> = ArrayList()
        if (StudyFragment.bookCategoryID == "1") {
            val list = Gson().fromJson<ArrayList<StudyExpandableListItem>>(json, object : TypeToken<ArrayList<StudyExpandableListItem>>() {}.type)
            list.forEach { bookList.add(Book(it.bookID.toInt(), it.groupName)) }
        } else {
            val list = Gson().fromJson<ArrayList<BooksCategoriesSubcategories>>(json, object : TypeToken<ArrayList<BooksCategoriesSubcategories>>() {}.type)
            list.forEach { bookList.add(Book(it.groupNameID.toInt(), it.groupName)) }
        }
        val results = Queries.getStatisticsForBook(context!!, bookList)
        var i = 0f
        val labels = ArrayList<String>()
        results.forEach { println(it.bookScore)
            if(it.bookScore>0) {
                entries.add(BarEntry(i++, it.bookScore))
                labels.add(it.bookName)
            }
        }
        val set = BarDataSet(entries, "")
        set.colors = ColorTemplate.JOYFUL_COLORS.asList()
        view.barChart.setTouchEnabled(false)
        view.barChart.animateY(1000, Easing.EasingOption.EaseOutBack)
        view.barChart.xAxis.valueFormatter =  IndexAxisValueFormatter(labels)
        val xAxis = view.barChart.xAxis
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        view.barChart.data=BarData(set)
        view.barChart.description.isEnabled = false
        return view
    }
}