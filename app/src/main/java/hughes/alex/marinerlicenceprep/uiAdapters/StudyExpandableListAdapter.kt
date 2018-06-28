package hughes.alex.marinerlicenceprep.uiAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.entity.StudyExpandableListItem
import kotlinx.android.synthetic.main.study_list_group.view.*

class StudyExpandableListAdapter(var context: Context?, val listOfGroups: ArrayList<StudyExpandableListItem>): BaseExpandableListAdapter() {
    override fun getGroup(p0: Int): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return listOfGroups[groupPosition].childNames.size
    }

    override fun getChild(p0: Int, p1: Int): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGroupId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.study_list_child, parent, false)
        view.groupName.text = listOfGroups[groupPosition].groupName
        return view
    }

    override fun getChildId(p0: Int, p1: Int): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getGroupCount(): Int {
        return listOfGroups.size
    }


}