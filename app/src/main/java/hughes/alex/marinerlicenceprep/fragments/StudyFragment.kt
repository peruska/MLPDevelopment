package hughes.alex.marinerlicenceprep.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.activities.Study
import hughes.alex.marinerlicenceprep.uiAdapters.StudyExpandableListAdapter
import hughes.alex.marinerlicenceprep.uiAdapters.StudyExpandableListAdapter.Companion.groupChecked
import kotlinx.android.synthetic.main.study_fragment.*
import kotlinx.android.synthetic.main.study_fragment.view.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.models.BooksCategoriesSubcategories
import hughes.alex.marinerlicenceprep.models.StudyExpandableListItem
import hughes.alex.marinerlicenceprep.uiAdapters.ExpandableListAdapterForDeck


class StudyFragment : Fragment() {

    companion object {
        //Values of active selection
        var bookCategoryID = "0"
        var dlNumber = "0"
        var categoryID = "-1"
        var bookID = "-1"
        var subcategoryID = "-1"
        fun setValues(
                bookID: String = "-1",
                categoryID: String = "-1",                //Optional values based on selection
                subcategoryID: String = "-1") {
            StudyFragment.bookID = bookID
            StudyFragment.categoryID = categoryID
            StudyFragment.subcategoryID = subcategoryID
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.study_fragment, container, false)
        val prefs = context!!.getSharedPreferences(MyApp.USER_LICENSE_DATA_VALUES, 0)
        StudyFragment.dlNumber = prefs.getString(MyApp.DL_NUMBER, "")
        StudyFragment.bookCategoryID = prefs.getString(MyApp.CATEGORY, "")
        val json = prefs.getString(MyApp.USER_LICENSE_DATA_VALUES, "")
        val context = context
        view.allEngineReplacement.text = if (bookCategoryID == "1") "All Engine" else "All Deck"
        val adapter =
                if (bookCategoryID == "1")
                    StudyExpandableListAdapter(context, Gson().fromJson<ArrayList<StudyExpandableListItem>>(json, object : TypeToken<ArrayList<StudyExpandableListItem>>() {}.type))
                else
                    ExpandableListAdapterForDeck(context!!, Gson().fromJson<ArrayList<BooksCategoriesSubcategories>>(json, object : TypeToken<ArrayList<BooksCategoriesSubcategories>>() {}.type))
        view.expandableListView.setAdapter(adapter)
        view.allEngineReplacement.setOnClickListener {
            startStudying.text = "Study: " + allEngineReplacement.text.toString()
            setValues("-1", "-1", "-1")
        }
        view.startStudying.setOnClickListener {
            val intent = Intent(context, Study::class.java)
            intent.putExtra("autoNext", autoNextSwitch.isChecked)
            intent.putExtra("shuffleQuestions", shuffleQuestionsSwitch.isChecked)
            intent.putExtra("logAnswers", logAnswersSwitch.isChecked)
            intent.putExtra("showAnswers", showAnswersSwitch.isChecked)
            intent.putExtra("dlNumber", StudyFragment.dlNumber)
            intent.putExtra("bookCategoryID", StudyFragment.bookCategoryID)
            intent.putExtra("bookID", StudyFragment.bookID)
            intent.putExtra("categoryID", StudyFragment.categoryID)
            intent.putExtra("subcategoryID", StudyFragment.subcategoryID)
            context?.startActivity(intent)
        }
        return view
    }
}