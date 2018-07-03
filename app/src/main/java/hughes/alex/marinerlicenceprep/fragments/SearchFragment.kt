package hughes.alex.marinerlicenceprep.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.entity.Questions
import hughes.alex.marinerlicenceprep.models.BooksCategoriesSubcategories
import hughes.alex.marinerlicenceprep.models.StudyExpandableListItem
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

        if (bookCategory == "1") {
            listBookSubcategory = Gson().fromJson<ArrayList<StudyExpandableListItem>>(json, object : TypeToken<ArrayList<StudyExpandableListItem>>() {}.type)
            listBookSubcategory.forEach {
                spinnerArray.add(it.groupName)
                arrayOfIndexes.add(it.bookID)

            }
        } else {
            listBookCategorySubcategory = Gson().fromJson<ArrayList<BooksCategoriesSubcategories>>(json, object : TypeToken<ArrayList<BooksCategoriesSubcategories>>() {}.type)
            listBookCategorySubcategory.forEach {
                spinnerArray.add(it.groupName)
                arrayOfIndexes.add(it.groupNameID)

            }
        }


        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerArray)
        val spinner = view.searchSelectionSpinner
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                listOfFoundQuestions = if (numberOfCalls == 0) {
                    Queries.getQuestions(context!!, bookCategory, "-1", dlNumber, "-1", "-1")
                } else {
                    Queries.getQuestions(context!!, bookCategory, arrayOfIndexes[spinner.selectedItemId.toInt()], dlNumber, "-1", "-1")
                }
                numberOfCalls++
            }

        }

        view.search_view.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {

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
                return false
            }

        })
        return view
    }
}