package hughes.alex.marinerlicenceprep.fragments

import android.graphics.Color
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.entity.Book
import hughes.alex.marinerlicenceprep.models.BooksCategoriesSubcategories
import hughes.alex.marinerlicenceprep.models.StudyExpandableListItem
import hughes.alex.marinerlicenceprep.uiAdapters.ExpandableListAdapterForDeck
import hughes.alex.marinerlicenceprep.uiAdapters.StudyExpandableListAdapter
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
        view.welcomeText.text = "Welcome,\n${MyApp.defaultUser!!.username}"
        val entries = ArrayList<PieEntry>()
        val set = PieDataSet(entries, "")
        set.colors = ColorTemplate.JOYFUL_COLORS.asList()
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
        results.forEach { println(it.bookScore)
            if(it.bookScore>0)
                entries.add(PieEntry(it.bookScore, it.bookName))
        }
        set.valueTextSize = 15f
        view.pieChart.data = PieData(set)
        view.pieChart.setEntryLabelColor(Color.BLACK)
        view.pieChart.setEntryLabelTextSize(12f)
        view.pieChart.setTouchEnabled(false)
        view.pieChart.animateY(1000, Easing.EasingOption.EaseOutBack)
        return view
    }
}