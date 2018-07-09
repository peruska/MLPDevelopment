package hughes.alex.marinerlicenceprep.fragments


import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.activities.Home
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.models.BooksCategoriesSubcategories
import hughes.alex.marinerlicenceprep.models.StudyExpandableListItem
import hughes.alex.marinerlicenceprep.uiAdapters.SearchAdapter
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_fragment.view.*
import kotlinx.android.synthetic.main.weakest_question_fragment.view.*

class WeakestQuestionsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.weakest_question_fragment, container, false)
        view.weakestQuestionsRecycler.layoutManager = LinearLayoutManager(context)
        val spinnerArray = ArrayList<String>()
        val adapter = ArrayAdapter<String>(context, R.layout.spinner_item, spinnerArray)
        val spinner : Spinner = (context!! as Home).spinnerWeakestQuestionCategories
        val prefs = context!!.getSharedPreferences(MyApp.USER_LICENSE_DATA_VALUES, 0)
        val bookCategory = prefs.getString(MyApp.CATEGORY, "")
        val arrayOfIndexes = ArrayList<String>()
        val listBookSubcategory: ArrayList<StudyExpandableListItem>
        val listBookCategorySubcategory: ArrayList<BooksCategoriesSubcategories>
        val json = prefs.getString(MyApp.USER_LICENSE_DATA_VALUES, "")
        val listOfWeakestQuestionIDs = ArrayList<Int>()
        val mSearchAdapter = SearchAdapter(listOfWeakestQuestionIDs, context!!, arrayListOf("", "weakest!"))

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
        view.weakestQuestionsRecycler.adapter = mSearchAdapter
        spinner.dropDownWidth = Resources.getSystem().displayMetrics.widthPixels
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                listOfWeakestQuestionIDs.clear()
                val temporaryListOfQuestionIDs = if(spinner.selectedItem.toString().contains("All")){
                    Queries.getWeakestQuestions(context!!, bookCategory, spinner.selectedItem.toString())
                }else{
                    Queries.getWeakestQuestions(context!!, bookCategory, arrayOfIndexes[position])
                }
                temporaryListOfQuestionIDs.forEach{
                    listOfWeakestQuestionIDs.add(it)
                }

                (parent!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                mSearchAdapter.notifyDataSetChanged()
            }
        }
        return view
    }
}