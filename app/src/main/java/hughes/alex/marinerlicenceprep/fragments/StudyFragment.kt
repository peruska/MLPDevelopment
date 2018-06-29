package hughes.alex.marinerlicenceprep.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.activities.Study
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.uiAdapters.StudyExpandableListAdapter
import kotlinx.android.synthetic.main.study_fragment.*
import kotlinx.android.synthetic.main.study_fragment.view.*


class StudyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.study_fragment, container, false)
        val adapter = StudyExpandableListAdapter(context, ArrayList(Queries.getBooksWithSubcategories(context!!, 1)))
        view.expandableListView.setAdapter(adapter)
        view.expandableListView.setOnGroupExpandListener{ startStudying.text = "Study: " + adapter.listOfGroups[it].groupName}
        view.expandableListView.setOnChildClickListener { expandableListView, view, groupPosition, childPosition, l ->

            startStudying.text = "Study: " + adapter.listOfGroups[groupPosition].childNames[childPosition]
            true
        }
        view.startStudying.setOnClickListener { context?.startActivity(Intent(context, Study::class.java)) }
        return view
    }
}