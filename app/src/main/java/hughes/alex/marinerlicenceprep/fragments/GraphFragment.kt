package hughes.alex.marinerlicenceprep.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.graph_fragment.view.*
import com.google.gson.reflect.TypeToken
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.entity.Book
import hughes.alex.marinerlicenceprep.models.BooksCategoriesSubcategories
import hughes.alex.marinerlicenceprep.models.StudyExpandableListItem
import kotlinx.android.synthetic.main.welcome_fragment.view.*

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
        results.forEach { println(it.bookScore)
            if(it.bookScore>0)
                entries.add(BarEntry( i++, it.bookScore))
        }
        val set = BarDataSet(entries, "")
        set.colors = ColorTemplate.JOYFUL_COLORS.asList()
        view.barChart.setTouchEnabled(false)
        view.barChart.animateY(1000, Easing.EasingOption.EaseOutBack)
        view.barChart.data=BarData(set)
        return view
    }
}