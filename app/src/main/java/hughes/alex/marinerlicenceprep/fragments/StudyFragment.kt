package hughes.alex.marinerlicenceprep.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.activities.Study
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.uiAdapters.ExpandableListAdapterForDeck
import hughes.alex.marinerlicenceprep.uiAdapters.StudyExpandableListAdapter
import kotlinx.android.synthetic.main.study_fragment.*
import kotlinx.android.synthetic.main.study_fragment.view.*


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
        val view = view!!
        val context = context!!
        if(MyApp.checkIfUserIsSubscribed())
        view.shuffleQuestionsSwitch.setOnCheckedChangeListener { compoundButton, b ->
            if(compoundButton.isChecked) {
                view.resumeStudying.isEnabled = false
                view.resumeStudying.text = "Can't Resume on Shuffled Bank"
                view.resumeStudying.background.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC)
            } else {
                val resumePrefs = context!!.getSharedPreferences(MyApp.RESUME_DATA, 0)
                val resumeButtonName = resumePrefs.getString("resumeButtonNameString", "")
                val resumeQuestionNumber = resumePrefs.getInt("resumeQuestionNumber", -1)
                if (resumeButtonName.isNotBlank() && resumeQuestionNumber != -1) {
                    view.resumeStudying.text = resumeButtonName.replace("Study: ", "Resume: ")
                    view.resumeStudying.isEnabled = true
                    view.resumeStudying.background.colorFilter = null
                }
            }
        }
        else view.shuffleQuestionsSwitch.isEnabled = false
        val adapter =
                if (bookCategoryID == "1")
                    StudyExpandableListAdapter(context, MyApp.dataForTwoLevelList)
                else
                    ExpandableListAdapterForDeck(context, MyApp.dataForThreeLevelList)
        view.expandableListView.setAdapter(adapter)
        try {
            val resumePrefs = context.getSharedPreferences(MyApp.RESUME_DATA, 0)
            val resumeButtonName = resumePrefs.getString("resumeButtonNameString", "")
            val resumeQuestionNumber = resumePrefs.getInt("resumeQuestionNumber", -1)
            if (resumeButtonName.isNotBlank() && resumeQuestionNumber != -1) {
                view.resumeStudying.text = resumeButtonName.replace("Study: ", "Resume: ")
                view.resumeStudying.isEnabled = true
                view.resumeStudying.background.colorFilter = null
            } else {
                view.resumeStudying.background.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC)
            }
        } catch (e: Exception) {
        }
        view.startStudying.setOnClickListener {
            val intent = Intent(context, Study::class.java)
            intent.putExtra("autoNext", autoNextSwitch.isChecked)
            intent.putExtra("shuffleQuestions", shuffleQuestionsSwitch.isChecked)
            intent.putExtra("logAnswers", logAnswersSwitch.isChecked)
            intent.putExtra("showAnswers", showAnswersSwitch.isChecked)
            intent.putExtra("callingIntent", "StudyFragment")
            PlaceholderFragment.questions = Queries.getQuestionIDs(context, bookCategoryID,
                    when (view.startStudying.text.toString()) {
                        "Study: All Engine" -> "All Engine"
                        "Study: All Deck" -> "All Deck"
                        else -> bookID
                    }, dlNumber, categoryID, subcategoryID)
            val resumePrefs = context.getSharedPreferences(MyApp.RESUME_DATA, 0)
            val editor = resumePrefs.edit()
            editor.putString("resumeButtonNameString", view.startStudying.text.toString())
            editor.apply()
            context.startActivity(intent)

        }
        view.resumeStudying.setOnClickListener {
            val intent = Intent(context, Study::class.java)
            intent.putExtra("autoNext", autoNextSwitch.isChecked)
            intent.putExtra("shuffleQuestions", shuffleQuestionsSwitch.isChecked)
            intent.putExtra("logAnswers", logAnswersSwitch.isChecked)
            intent.putExtra("showAnswers", showAnswersSwitch.isChecked)
            val resumePrefs = context.getSharedPreferences(MyApp.RESUME_DATA, 0)
            val dlNumber = resumePrefs.getString("dlNumber", "")
            val bookCategoryID = resumePrefs.getString("bookCategoryID", "")
            val bookID = resumePrefs.getString("bookID", "")
            val categoryID = resumePrefs.getString("categoryID", "")
            val subcategoryID = resumePrefs.getString("subcategoryID", "")
            intent.putExtra("callingIntent", "StudyFragment")
            intent.putExtra("resumeQuestionNumber", resumePrefs.getInt("resumeQuestionNumber", 0))
            PlaceholderFragment.questions = Queries.getQuestionIDs(context, bookCategoryID,
                    when (view.resumeStudying.text.toString()) {
                        "Resume: All Engine" -> "All Engine"
                        "Resume: All Deck" -> "All Deck"
                        else -> bookID
                    }, dlNumber, categoryID, subcategoryID)
            context.startActivity(intent)
        }
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.study_fragment, container, false)
    }
}