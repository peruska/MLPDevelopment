package hughes.alex.marinerlicenceprep.uiAdapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.activities.Home
import hughes.alex.marinerlicenceprep.fragments.StudyFragment
import hughes.alex.marinerlicenceprep.models.CategoryWithSubcategories
import hughes.alex.marinerlicenceprep.uiAdapters.ExpandableListAdapterForDeck.Companion.activeGroupPosition
import kotlinx.android.synthetic.main.study_deck_first_child.view.*
import kotlinx.android.synthetic.main.study_deck_list_second_child.view.*
import kotlinx.android.synthetic.main.study_fragment.*

class SecondLevelAdapter(val context: Context, var categoriesWithSubcategories: ArrayList<CategoryWithSubcategories>, var bookID: String, val secondLevelExpandableListView: SecondLevelExpandableListView) : BaseExpandableListAdapter() {
    override fun getGroup(groupPosition: Int): Any {
        return groupPosition
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun onGroupExpanded(groupPosition: Int) {
        (context as Home).startStudying.text = "Study: " + categoriesWithSubcategories[groupPosition].categoryName
        StudyFragment.setValues(bookID, categoriesWithSubcategories[groupPosition].categoryID)
        secondLevelExpandableListView.setItemChecked(groupPosition, true)
        resetTopLevelGroupCheckMark()
        super.onGroupExpanded(groupPosition)
    }

    fun resetTopLevelGroupCheckMark(){
        (context as Home).expandableListView.setItemChecked(activeGroupPosition, false)
    }
    override fun onGroupCollapsed(groupPosition: Int) {
        (context as Home).startStudying.text = "Study: " + categoriesWithSubcategories[groupPosition].categoryName
        StudyFragment.setValues(bookID, categoriesWithSubcategories[groupPosition].categoryID)
        secondLevelExpandableListView.setItemChecked(groupPosition, true)
        resetTopLevelGroupCheckMark()
        super.onGroupExpanded(groupPosition)
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.study_deck_first_child, parent, false)
        view.first_child_text.text = categoriesWithSubcategories[groupPosition].categoryName
        return view!!
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return categoriesWithSubcategories[groupPosition].subcategories.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return childPosition
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }


    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view2 = inflater.inflate(R.layout.study_deck_list_second_child, parent, false)
        view2.second_child_text.text = categoriesWithSubcategories[groupPosition].subcategories[childPosition].subcategoryName
        view2.setOnClickListener {
            (context as Home).startStudying.text = "Study: " + categoriesWithSubcategories[groupPosition].subcategories[childPosition].subcategoryName
            val index = secondLevelExpandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition))
            secondLevelExpandableListView.setItemChecked(index, true)
            StudyFragment.setValues(bookID, categoriesWithSubcategories[groupPosition].categoryID, categoriesWithSubcategories[groupPosition].subcategories[childPosition].subcategoryID)
        }

        return view2!!
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return categoriesWithSubcategories.size
    }
}