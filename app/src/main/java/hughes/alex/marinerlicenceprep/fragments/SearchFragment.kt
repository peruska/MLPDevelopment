package hughes.alex.marinerlicenceprep.fragments

import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.models.BooksCategoriesSubcategories
import hughes.alex.marinerlicenceprep.models.StudyExpandableListItem
import hughes.alex.marinerlicenceprep.uiAdapters.SearchAdapter
import kotlinx.android.synthetic.main.search_fragment.view.*


class SearchFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val finalListOfIDs = ArrayList<Int>()
        val listBookSubcategory: ArrayList<StudyExpandableListItem>
        val listBookCategorySubcategory: ArrayList<BooksCategoriesSubcategories>
        val spinnerArray = ArrayList<String>()
        val view = inflater.inflate(R.layout.search_fragment, container, false)
        val prefs = context!!.getSharedPreferences(MyApp.USER_LICENSE_DATA_VALUES, 0)
        val json = prefs.getString(MyApp.USER_LICENSE_DATA_VALUES, "")
        val arrayOfIndexes = ArrayList<String>()
        val bookCategory = prefs.getString(MyApp.CATEGORY, "")
        var selectedSpinnerPosition = 0
        val adapter = ArrayAdapter<String>(context, R.layout.spinner_item, spinnerArray)
        val spinner = view.searchSelectionSpinner
        val searchWord: ArrayList<String> = arrayListOf("", "")
        view.searchRecyclerView.layoutManager = LinearLayoutManager(context)
        val mSearchAdapter = SearchAdapter(finalListOfIDs, context!!, searchWord)
        val mSearchView = view.search_view
        view.searchRecyclerView.adapter = mSearchAdapter

        if (bookCategory == "1") {
            listBookSubcategory = Gson().fromJson<ArrayList<StudyExpandableListItem>>(json, object : TypeToken<ArrayList<StudyExpandableListItem>>() {}.type)
            listBookSubcategory.forEach {
                spinnerArray.add("Search " + it.groupName)
                arrayOfIndexes.add(it.bookID)

            }
        } else {
            listBookCategorySubcategory = Gson().fromJson<ArrayList<BooksCategoriesSubcategories>>(json, object : TypeToken<ArrayList<BooksCategoriesSubcategories>>() {}.type)
            listBookCategorySubcategory.forEach {
                spinnerArray.add("Search " + it.groupName)
                arrayOfIndexes.add(it.groupNameID)

            }
        }



        mSearchView.setOnClickListener {
            if (mSearchView.isIconified) {
                mSearchView.isIconified = false
            }
        }


        mSearchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                finalListOfIDs.clear()
                val tempListOfIDs = if (selectedSpinnerPosition == 0) {
                    Queries.getSearchedQuestionIDs(context!!, query, bookCategory, null)
                } else {
                    Queries.getSearchedQuestionIDs(context!!, query, bookCategory, arrayOfIndexes[selectedSpinnerPosition])

                }
                tempListOfIDs.forEach { finalListOfIDs.add(it) }
                searchWord[0] = query
                mSearchAdapter.notifyDataSetChanged()
                return false
            }

        })


        spinner.dropDownWidth = Resources.getSystem().displayMetrics.widthPixels
        spinner.adapter = adapter
        spinner.background.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedSpinnerPosition = position
                mSearchView.setQuery(mSearchView.query, true)
                (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)

            }
        }

        val searchCloseButtonId = mSearchView.context.resources
                .getIdentifier("android:id/search_close_btn", null, null)
        val searchTextId = mSearchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val searchText = mSearchView.findViewById<EditText>(searchTextId)
        val closeButton = mSearchView.findViewById<ImageView>(searchCloseButtonId)
        closeButton.setOnClickListener {
            searchText.setText("")
            finalListOfIDs.clear()
            mSearchAdapter.notifyDataSetChanged()
        }

        return view
    }

}