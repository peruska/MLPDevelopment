package hughes.alex.marinerlicenceprep.uiAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.activities.Home
import hughes.alex.marinerlicenceprep.fragments.StudyFragment.Companion.setValues
import hughes.alex.marinerlicenceprep.models.BooksCategoriesSubcategories
import kotlinx.android.synthetic.main.study_deck_list_group.view.*
import kotlinx.android.synthetic.main.study_fragment.*

class ExpandableListAdapterForDeck(var context: Context, var listOfMembers: ArrayList<BooksCategoriesSubcategories>) : BaseExpandableListAdapter() {
    override fun getGroup(groupPosition: Int): Any {
        return listOfMembers[groupPosition]
    }

    override fun onGroupExpanded(groupPosition: Int) {
        (context as Home).startStudying.text = "Study: " + listOfMembers[groupPosition].groupName
        setValues(listOfMembers[groupPosition].groupNameID)
    }

    override fun onGroupCollapsed(groupPosition: Int) {
        (context as Home).startStudying.text = "Study: " + listOfMembers[groupPosition].groupName
        setValues(listOfMembers[groupPosition].groupNameID)
    }
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.study_deck_list_group, parent, false)
        view.deck_group_name.text = listOfMembers[groupPosition].groupName
        return view!!
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return listOfMembers[groupPosition].categories[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val secondLevelExpandableListView = SecondLevelExpandableListView(context)
        secondLevelExpandableListView.setPadding(20, 0, 0, 0)
        secondLevelExpandableListView.setAdapter(
                SecondLevelAdapter(
                        context,
                        listOfMembers[groupPosition].categories,
                        listOfMembers[groupPosition].groupNameID
                )
        )

        return secondLevelExpandableListView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return listOfMembers.size
    }
}