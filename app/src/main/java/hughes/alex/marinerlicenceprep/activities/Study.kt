package hughes.alex.marinerlicenceprep.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.View
import android.widget.EditText
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.fragments.PlaceholderFragment.Companion.questions
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.entity.Questions
import hughes.alex.marinerlicenceprep.fragments.PlaceholderFragment
import hughes.alex.marinerlicenceprep.fragments.StudyFragment
import kotlinx.android.synthetic.main.activity_study.*


class Study : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var autoNext: Boolean = true
    private var shuffleQuestions: Boolean = false
    private var logAnswers: Boolean = true
    var showAnswers: Boolean = false
    lateinit var currentQuestion: Questions
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
        val extras = intent.extras
        autoNext = extras.getBoolean("autoNext")
        shuffleQuestions = extras.getBoolean("shuffleQuestions")
        if (shuffleQuestions)
            PlaceholderFragment.questions.shuffle()
        logAnswers = extras.getBoolean("logAnswers")
        showAnswers = extras.getBoolean("showAnswers")
        currentQuestion = Queries.getQuestion(this, questions[0].toString())
        bookmarkQuestion.setImageResource(
                if (currentQuestion.isBookmarked == "1")
                    R.mipmap.bookmark
                else
                    R.mipmap.bookmark_empty)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        if (extras.getString("callingIntent") != "StudyFragment")
            container.currentItem = extras.getInt("bookmarkOrSearchSelection")
        else if (extras.getInt("resumeQuestionNumber") > 0)
            container.currentItem = extras.getInt("resumeQuestionNumber")
        numberLabel.text = "Num " + (container.currentItem + 1) + "/" + container.adapter?.count
        closeButton.setOnClickListener { finish() }
        val prefs = getSharedPreferences(MyApp.RESUME_DATA, 0)
        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            @SuppressLint("SetTextI18n", "ApplySharedPref")
            override fun onPageSelected(position: Int) {
                currentQuestion = Queries.getQuestion(this@Study, questions[position].toString())
                numberLabel.text = "Num " + (container.currentItem + 1) + "/" + container.adapter?.count
                attemped = 0
                moveToPreviousQuestionButton.setImageResource(if (position == 0) R.drawable.anchor else R.mipmap.left)
                moveToNextQuestionButton.setImageResource(if (position == container.adapter!!.count - 1) R.drawable.anchor else R.mipmap.right_arrow)
                bookmarkQuestion.setImageResource(
                        if (currentQuestion.isBookmarked == "1")
                            R.mipmap.bookmark
                        else
                            R.mipmap.bookmark_empty
                )
                val editor = prefs.edit()
                editor.putString("dlNumber", StudyFragment.dlNumber)
                editor.putString("bookCategoryID", StudyFragment.bookCategoryID)
                editor.putString("bookID", StudyFragment.bookID)
                editor.putString("categoryID", StudyFragment.categoryID)
                editor.putString("subcategoryID", StudyFragment.subcategoryID)
                editor.putInt("resumeQuestionNumber", container.currentItem)
                editor.commit()
            }
        })
    }

    fun showChangeQuestionDialog(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Go To Number")
        builder.setMessage("Enter the number you wish to go to.")
        val input = EditText(this)
        input.hint = "Input number..."
        input.inputType = InputType.TYPE_CLASS_NUMBER
        builder.setView(input)
        builder.setPositiveButton("Go To") { _, _ ->
            val inputValue = input.text.toString().toInt()
            if (inputValue > 0 && inputValue <= questions.size)
                container.currentItem = inputValue - 1
        }
        builder.setNegativeButton("Cancel"
        ) { dialog, _ -> dialog.cancel() }
        builder.show()
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
    @SuppressLint("SetTextI18n")
    fun answerQuestion(view: View) {

        if (compareAnswers(view, currentQuestion.correctAnswer)) {
            view.setBackgroundColor(resources.getColor(R.color.questionsGreen))
            if (attemped == 0) {
                correct++
                Queries.updateQuestionStatistics(this, currentQuestion.questionID, 1)
            }
            if (autoNext) {
                val handler = Handler()
                handler.postDelayed({
                    moveToNextQuestion(view)
                }, 700)
            }
        } else {
            if (attemped == 0)
                Queries.updateQuestionStatistics(this, currentQuestion.questionID, 0)
            view.setBackgroundColor(resources.getColor(R.color.questionsRed))
        }
        if (attemped == 0) {
            total++
            scoreLabel.text = "Score " + correct + "/" + total
        }
        attemped = 1
    }

    fun bookmarkQuestion(view: View) {
        println("")
        if (currentQuestion.isBookmarked == "1") {
            Queries.changeBookmark(this, currentQuestion.questionID, "0")
            bookmarkQuestion.setImageResource(R.mipmap.bookmark_empty)
            currentQuestion.isBookmarked = "0"
        } else {
            Queries.changeBookmark(this, currentQuestion.questionID, "1")
            bookmarkQuestion.setImageResource(R.mipmap.bookmark)
            currentQuestion.isBookmarked = "1"
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
