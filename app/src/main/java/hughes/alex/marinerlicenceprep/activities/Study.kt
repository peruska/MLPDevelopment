package hughes.alex.marinerlicenceprep.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.activities.PlaceholderFragment.Companion.questions
import hughes.alex.marinerlicenceprep.database.Queries
import kotlinx.android.synthetic.main.activity_study.*

class Study : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var autoNext: Boolean = true
    private var shuffleQuestions: Boolean = false
    private var logAnswers: Boolean = true
    private var dlNumber: String? = null
    private var bookCategoryID: String? = null
    private var bookID: String? = null
    private var categoryID: String? = null
    private var subcategoryID: String? = null
    var showAnswers: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
        val extras = intent.extras
        if (extras.getString("callingIntent") == "Normal") {
            autoNext = extras.getBoolean("autoNext")
            shuffleQuestions = extras.getBoolean("shuffleQuestions")
            logAnswers = extras.getBoolean("logAnswers")
            showAnswers = extras.getBoolean("showAnswers")
            dlNumber = extras.getString("dlNumber")
            bookCategoryID = extras.getString("bookCategoryID")
            bookID = extras.getString("bookID")
            categoryID = extras.getString("categoryID")
            subcategoryID = extras.getString("subcategoryID")
            PlaceholderFragment.questions = Queries.getQuestions(this, bookCategoryID!!,
                    when(bookCategoryID){
                        "1"->"All Engine"
                        "2"->"All Deck"
                        else -> bookID
                    }
                    , dlNumber!!, categoryID, subcategoryID)

        }

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        if(extras.getString("callingIntent") != "Normal")
            container.currentItem = extras.getInt("bookmarkOrSearchSelection")
        if(extras.getInt("resumeQuestionNumber")>0)
            container.currentItem = extras.getInt("resumeQuestionNumber")
        numberLabel.text = "Num " + (container.currentItem + 1) + "/" + container.adapter?.count
        closeButton.setOnClickListener { finish() }
        val prefs = getSharedPreferences(MyApp.RESUME_DATA, 0)
        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                numberLabel.text = "Num " + (container.currentItem + 1) + "/" + container.adapter?.count
                attemped = 0
                moveToPreviousQuestionButton.setImageResource(if (position == 0) R.drawable.anchor else R.mipmap.left)
                moveToNextQuestionButton.setImageResource(if (position == container.adapter!!.count - 1) R.drawable.anchor else R.mipmap.right_arrow)
                bookmarkQuestion.setImageResource(if (questions[position].isBookmarked == "1") R.mipmap.bookmark_empty else R.mipmap.bookmark)
                val editor = prefs.edit()
                editor.putString("dlNumber", dlNumber)
                editor.putString("bookCategoryID", bookID)
                editor.putString("bookID", bookID)
                editor.putString("categoryID", bookID)
                editor.putString("subcategoryID", bookID)
                editor.putInt("resumeQuestionNumber", container.currentItem)
                editor.commit()
            }
        })
    }

    private fun compareAnswers(view: View, answer: String): Boolean {
        val selectedAnswer =
                when (view.id) {
                    R.id.answer1 -> "A"
                    R.id.answer2 -> "B"
                    R.id.answer3 -> "C"
                    R.id.answer4 -> "D"
                    else -> ""
                }
        return selectedAnswer == answer
    }

    var attemped = 0
    var correct = 0
    var total = 0
    fun answerQuestion(view: View) {

        if (compareAnswers(view, PlaceholderFragment.questions[container.currentItem].correctAnswer)) {
            view.setBackgroundColor(resources.getColor(R.color.questionsGreen))
            if (attemped == 0) {
                correct++
                Queries.updateQuestionStatistics(this, PlaceholderFragment.questions[container.currentItem].questionID, 1)
            }
            if (autoNext) moveToNextQuestion(view)
        } else {
            if (attemped == 0)
                Queries.updateQuestionStatistics(this, PlaceholderFragment.questions[container.currentItem].questionID, 0)
            view.setBackgroundColor(resources.getColor(R.color.questionsRed))
        }
        if (attemped == 0) {
            total++
            scoreLabel.text = "Score " + correct + "/" + total
        }
        attemped = 1
    }

    fun bookmarkQuestion(view: View) {
        val question = questions[container.currentItem]
        if (question.isBookmarked == "1") {
            Queries.changeBookmark(this, question.questionID, "0")
            bookmarkQuestion.setImageResource(R.mipmap.bookmark_empty)
            questions[container.currentItem].isBookmarked = "0"
        } else {
            Queries.changeBookmark(this, question.questionID, "1")
            bookmarkQuestion.setImageResource(R.mipmap.bookmark)
            questions[container.currentItem].isBookmarked = "1"
        }
    }

    fun moveToPreviousQuestion(view: View) {
        container.currentItem = container.currentItem - 1
    }

    fun moveToNextQuestion(view: View) {
        container.currentItem = container.currentItem + 1
    }

    class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(position)
        }

        override fun getCount(): Int {
            return questions.size
        }
    }
}
