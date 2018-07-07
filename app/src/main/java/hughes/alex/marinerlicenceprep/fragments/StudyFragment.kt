package hughes.alex.marinerlicenceprep.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.activities.Study
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.models.BooksCategoriesSubcategories
import hughes.alex.marinerlicenceprep.models.StudyExpandableListItem
import hughes.alex.marinerlicenceprep.uiAdapters.ExpandableListAdapterForDeck
import hughes.alex.marinerlicenceprep.uiAdapters.StudyExpandableListAdapter
import kotlinx.android.synthetic.main.study_fragment.*
import kotlinx.android.synthetic.main.study_fragment.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.indeterminateProgressDialog
import java.util.*


class StudyFragment : Fragment() {

    companion object {
        var bookCategoryID = "0"
        var dlNumber = "0"
        var categoryID = "-1"
        var bookID = "-1"
        var subcategoryID = "-1"
        fun setValues(
                bookID: String = "-1",
                categoryID: String = "-1",
                subcategoryID: String = "-1") {
            StudyFragment.bookID = bookID
            StudyFragment.categoryID = categoryID
            StudyFragment.subcategoryID = subcategoryID
        }
    }

    override fun onResume() {
        try {
            val resumePrefs = context!!.getSharedPreferences(MyApp.RESUME_DATA, 0)
            val resumeButtonName = resumePrefs.getString("resumeButtonNameString", "")
            if(resumeButtonName.isNotBlank()){
                view!!.resumeStudying.text = resumeButtonName.replace("Study: ", "Resume: ")
                view!!.resumeStudying.isEnabled = true
            }
            else {
                view!!.resumeStudying.background.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC)
            }
        }catch (e:Exception){}
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.study_fragment, container, false)
        val prefs = context!!.getSharedPreferences(MyApp.USER_LICENSE_DATA_VALUES, 0)
        val resumePrefs = context!!.getSharedPreferences(MyApp.RESUME_DATA, 0)
        StudyFragment.dlNumber = prefs.getString(MyApp.DL_NUMBER, "")
        StudyFragment.bookCategoryID = prefs.getString(MyApp.CATEGORY, "")

        val json = prefs.getString(MyApp.USER_LICENSE_DATA_VALUES, "")
        val context = context
        val adapter =
                if (bookCategoryID == "1")
                    StudyExpandableListAdapter(context, Gson().fromJson<ArrayList<StudyExpandableListItem>>(json, object : TypeToken<ArrayList<StudyExpandableListItem>>() {}.type))
                else
                    ExpandableListAdapterForDeck(context!!, Gson().fromJson<ArrayList<BooksCategoriesSubcategories>>(json, object : TypeToken<ArrayList<BooksCategoriesSubcategories>>() {}.type))
        view.expandableListView.setAdapter(adapter)
        view.startStudying.setOnClickListener {
            val intent = Intent(context, Study::class.java)
            intent.putExtra("autoNext", autoNextSwitch.isChecked)
            intent.putExtra("shuffleQuestions", shuffleQuestionsSwitch.isChecked)
            intent.putExtra("logAnswers", logAnswersSwitch.isChecked)
            intent.putExtra("showAnswers", showAnswersSwitch.isChecked)
            intent.putExtra("callingIntent", "StudyFragment")
            val dialog = context!!.indeterminateProgressDialog ("Loading questions")
            context.doAsync {
                PlaceholderFragment.questions = Queries.getQuestionIDs(context, bookCategoryID,
                    when (view.startStudying.text.toString()) {
                        "Study: All Engine" -> "All Engine"
                        "Study: All Deck" -> "All Deck"
                        else -> bookID
                    }, dlNumber, categoryID, subcategoryID)
                val editor = resumePrefs.edit()
                editor.putString("resumeButtonNameString", view.startStudying.text.toString())
                editor.apply()
                context.startActivity(intent)
                dialog.dismiss()
            }

        }
        view.resumeStudying.setOnClickListener {
            val intent = Intent(context, Study::class.java)
            intent.putExtra("autoNext", autoNextSwitch.isChecked)
            intent.putExtra("shuffleQuestions", shuffleQuestionsSwitch.isChecked)
            intent.putExtra("logAnswers", logAnswersSwitch.isChecked)
            intent.putExtra("showAnswers", showAnswersSwitch.isChecked)
            val dlNumber = resumePrefs.getString("dlNumber", "")
            val bookCategoryID = resumePrefs.getString("bookCategoryID", "")
            val bookID = resumePrefs.getString("bookID", "")
            val categoryID = resumePrefs.getString("categoryID", "")
            val subcategoryID = resumePrefs.getString("subcategoryID", "")
            intent.putExtra("callingIntent", "StudyFragment")
            intent.putExtra("resumeQuestionNumber", resumePrefs.getInt("resumeQuestionNumber", 0))
            PlaceholderFragment.questions = Queries.getQuestionIDs(context!!, bookCategoryID,
                    when (view.resumeStudying.text.toString()) {
                        "Resume: All Engine" -> "All Engine"
                        "Resume: All Deck" -> "All Deck"
                        else -> bookID
                    }, dlNumber, categoryID, subcategoryID)
            context.startActivity(intent)
        }
        return view
    }
}