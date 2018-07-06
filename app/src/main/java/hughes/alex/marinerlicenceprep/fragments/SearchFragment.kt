package hughes.alex.marinerlicenceprep.fragments

import android.content.res.Resources
import android.graphics.Color
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
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
import hughes.alex.marinerlicenceprep.entity.Questions
import hughes.alex.marinerlicenceprep.models.BooksCategoriesSubcategories
import hughes.alex.marinerlicenceprep.models.StudyExpandableListItem
import hughes.alex.marinerlicenceprep.uiAdapters.SearchAdapter
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.android.synthetic.main.search_fragment.view.*


class SearchFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val finalListOfQuestions = ArrayList<Questions>()
        val listBookSubcategory: ArrayList<StudyExpandableListItem>
        val listBookCategorySubcategory: ArrayList<BooksCategoriesSubcategories>
        val spinnerArray = ArrayList<String>()
        val view = inflater.inflate(R.layout.search_fragment, container, false)
        val prefs = context!!.getSharedPreferences(MyApp.USER_LICENSE_DATA_VALUES, 0)
        val json = prefs.getString(MyApp.USER_LICENSE_DATA_VALUES, "")
        val arrayOfIndexes = ArrayList<String>()
        val bookCategory = prefs.getString(MyApp.CATEGORY, "")
        val dlNumber = prefs.getString(MyApp.DL_NUMBER, "")
        var listOfFoundQuestions: ArrayList<Questions>? = null
        var numberOfCalls = 0
        view.searchRecyclerView.layoutManager = LinearLayoutManager(context)
        val mSearchAdapter = SearchAdapter(finalListOfQuestions, context!!)
        view.searchRecyclerView.adapter = mSearchAdapter

        if (bookCategory == "1") {
            listBookSubcategory = Gson().fromJson<ArrayList<StudyExpandableListItem>>(json, object : TypeToken<ArrayList<StudyExpandableListItem>>() {}.type)
            listBookSubcategory.forEach {
                spinnerArray.add(it.groupName)
                arrayOfIndexes.add(it.bookID)

            }
        } else {
            listBookCategorySubcategory = Gson().fromJson<ArrayList<BooksCategoriesSubcategories>>(json, object : TypeToken<ArrayList<BooksCategoriesSubcategories>>() {}.type)
            listBookCategorySubcategory.forEach {
                spinnerArray.add("Search " + it.groupName)
                arrayOfIndexes.add(it.groupNameID)

            }
        }

        val mSearchView = view.search_view

        mSearchView.setOnClickListener {
            if(mSearchView.isIconified){
                mSearchView.isIconified = false
            }
        }


        mSearchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                finalListOfQuestions.clear()
                listOfFoundQuestions!!.forEach {
                    if (it.question.toLowerCase().contains(query.toLowerCase()) || it.answerOne.toLowerCase().contains(query.toLowerCase()) ||
                            it.answerTwo.toLowerCase().contains(query.toLowerCase()) || it.answerThree.toLowerCase().contains(query.toLowerCase())
                            || it.answerFour.toLowerCase().contains(query.toLowerCase()) || it.correctAnswer.toLowerCase().contains(query.toLowerCase()) ||
                            it.questionNumber.toLowerCase().contains(query.toLowerCase()) || it.subcategory.toLowerCase().contains(query.toLowerCase())) {
                        finalListOfQuestions.add(it)
                    }
                }
                mSearchAdapter.notifyDataSetChanged()
                return false
            }

        })

        val adapter = ArrayAdapter<String>(context, R.layout.spinner_item, spinnerArray)
        val spinner = view.searchSelectionSpinner
        spinner.dropDownWidth = Resources.getSystem().displayMetrics.widthPixels
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                if (numberOfCalls == 0) {
                    listOfFoundQuestions = Queries.getQuestions(context!!, bookCategory, "-1", dlNumber, "-1", "-1")
                } else {
                    val bookID = if(position == 0){
                        spinner.selectedItem.toString()
                    }else {
                        arrayOfIndexes[spinner.selectedItemId.toInt()]
                    }
                    listOfFoundQuestions = Queries.getQuestions(context!!, bookCategory, bookID, dlNumber, "-1", "-1")
                    mSearchView.setQuery(mSearchView.query, true)
                }

                numberOfCalls++
            }

        }
        val searchCloseButtonId = mSearchView.context.resources
                .getIdentifier("android:id/search_close_btn", null, null)
        val searchTextId = mSearchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val searchText = mSearchView.findViewById<EditText>(searchTextId)
        val closeButton = mSearchView.findViewById<ImageView>(searchCloseButtonId)
        closeButton.setOnClickListener {
            searchText.setText("")
            finalListOfQuestions.clear()
            mSearchAdapter.notifyDataSetChanged()
        }
        return view
    }
}