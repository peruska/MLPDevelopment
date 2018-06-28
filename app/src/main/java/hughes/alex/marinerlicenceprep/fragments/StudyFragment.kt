package hughes.alex.marinerlicenceprep.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.uiAdapters.StudyExpandableListAdapter
import kotlinx.android.synthetic.main.study_fragment.view.*


class StudyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.study_fragment, container, false)
        val adapter = StudyExpandableListAdapter(context)
        view.expandableListView.setAdapter(adapter)
        return view
    }
}