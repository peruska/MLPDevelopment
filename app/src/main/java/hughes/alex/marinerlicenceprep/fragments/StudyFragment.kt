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


class StudyFragment : Fragment() {
    var activeTextView: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.study_fragment, container, false)
        val adapter = StudyExpandableListAdapter(context, ArrayList(Queries.getBooksWithSubcategories(context!!, 1)))
        view.expandableListView.setAdapter(adapter)
        view.expandableListView.setOnGroupClickListener { expandableListView, view, i, l ->
            groupChecked = i
            if(expandableListView.isGroupExpanded(i))
                expandableListView.collapseGroup(i)
            else
                expandableListView.expandGroup(i)
            startStudying.text = "Study: " + adapter.listOfGroups[i].groupName
            true
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