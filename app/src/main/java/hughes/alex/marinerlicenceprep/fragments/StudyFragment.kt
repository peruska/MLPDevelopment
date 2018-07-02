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
import hughes.alex.marinerlicenceprep.database.Queries
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
    var activeTextView: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.study_fragment, container, false)
        val prefs = context!!.getSharedPreferences(MyApp.USER_LICENSE_DATA, 0)
        val dlNumber = prefs.getString(MyApp.DL_NUMBER, "")
        val bookCategory = prefs.getString(MyApp.CATEGORY, "")
        val json = prefs.getString(MyApp.USER_LICENSE_DATA_VALUES, "")
        val context = context
        val adapter =
                if (bookCategory == "1")
                    StudyExpandableListAdapter(context, Gson().fromJson<ArrayList<StudyExpandableListItem>>(json, object: TypeToken<ArrayList<StudyExpandableListItem>>(){}.type))
                else
                    ExpandableListAdapterForDeck(context!!, Gson().fromJson<ArrayList<BooksCategoriesSubcategories>>(json, object: TypeToken<ArrayList<BooksCategoriesSubcategories>>(){}.type))
        view.expandableListView.setAdapter(adapter)
        /*view.expandableListView.setOnGroupClickListener { expandableListView, _, i, l ->
            activeTextView?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            groupChecked = i
            if (expandableListView.isGroupExpanded(i))
                expandableListView.collapseGroup(i)
            else
                expandableListView.expandGroup(i)
            // startStudying.text = "Study: " + adapter.listOfGroups[i].groupName
            true
        }*/
        view.allEngineReplacement.setOnClickListener {
            startStudying.text = "Study: All Engine"
            view.allEngineReplacement.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.checked, 0)
            activeTextView = view.allEngineReplacement
        }
        view.startStudying.setOnClickListener {
            val intent = Intent(context, Study::class.java)
            intent.putExtra("autoNext", autoNextSwitch.isChecked)
            intent.putExtra("shuffleQuestions", shuffleQuestionsSwitch.isChecked)
            intent.putExtra("logAnswers", logAnswersSwitch.isChecked)
            intent.putExtra("showAnswers", showAnswersSwitch.isChecked)
            context?.startActivity(intent)
        }
        return view
    }
}