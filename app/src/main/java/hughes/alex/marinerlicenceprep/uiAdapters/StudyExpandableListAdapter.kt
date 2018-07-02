package hughes.alex.marinerlicenceprep.uiAdapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.activities.Home
import hughes.alex.marinerlicenceprep.activities.Study
import hughes.alex.marinerlicenceprep.fragments.StudyFragment
import hughes.alex.marinerlicenceprep.models.StudyExpandableListItem
import kotlinx.android.synthetic.main.study_fragment.*
import kotlinx.android.synthetic.main.study_list_child.view.*
import kotlinx.android.synthetic.main.study_list_group.view.*
import org.jetbrains.anko.forEachChild

class StudyExpandableListAdapter(var context: Context?, val listOfGroups: ArrayList<StudyExpandableListItem>) : BaseExpandableListAdapter() {
    companion object {
        var groupChecked: Int = -1
        var uncheckThis: Int = -1
    }

    override fun getGroup(p0: Int): Any {
        return listOfGroups[p0]
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.study_list_group, parent, false)
        view.groupName.text = listOfGroups[groupPosition].groupName
        if (groupChecked == groupPosition) {
            view.groupName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.checked, 0)
            activeTextView = view.groupName
        } else if (uncheckThis == groupPosition)

            view.groupName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return listOfGroups[groupPosition].childNames.size
    }

    override fun getChild(p0: Int, p1: Int): Any {
        return listOfGroups[p0].childNames[p1]
    }

    override fun getGroupId(p0: Int): Long {
        return p0.toLong()
    }

    private var activeTextView: TextView? = null
    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.study_list_child, parent, false)
        view.childName.text = listOfGroups[groupPosition].childNames[childPosition].subcategoryName

        view.childName.setOnClickListener {

            uncheckThis = groupChecked
            groupChecked = -1
            parent?.forEachChild {
                try {
                    it.groupName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                } catch (e: Exception) {
                }
            }
            activeTextView?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            view.childName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.checked, 0)
            activeTextView = view.childName
            (context as Home).startStudying.text = "Study: " + listOfGroups[groupPosition].childNames[childPosition].subcategoryName
        }
        return view
    }

    override fun getChildId(p0: Int, p1: Int): Long {
        return 1
    }

    override fun getGroupCount(): Int {
        return listOfGroups.size
    }


}