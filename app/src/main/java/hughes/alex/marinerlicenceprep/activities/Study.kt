package hughes.alex.marinerlicenceprep.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.widget.TextViewCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import com.google.gson.Gson
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.entity.Questions
import hughes.alex.marinerlicenceprep.fragments.PlaceholderFragment
import hughes.alex.marinerlicenceprep.fragments.PlaceholderFragment.Companion.questions
import hughes.alex.marinerlicenceprep.fragments.StudyFragment
import kotlinx.android.synthetic.main.activity_study.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton


class Study : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var autoNext: Boolean = true
    private var shuffleQuestions: Boolean = false
    private var logAnswers: Boolean = true
    var showAnswers: Boolean = false
    lateinit var currentQuestion: Questions
    lateinit var answeredQuestions: Array<Boolean>
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
        TextViewCompat.setAutoSizeTextTypeWithDefaults(upgradeAccount, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        val extras = intent.extras
        if (MyApp.checkIfUserIsSubscribed())
            upgradeAccount.visibility = View.GONE
        //Fetch values from switches
        autoNext = extras.getBoolean("autoNext")
        shuffleQuestions = extras.getBoolean("shuffleQuestions")
        logAnswers = extras.getBoolean("logAnswers")
        showAnswers = extras.getBoolean("showAnswers")
        currentQuestion = Queries.getQuestion(this, questions[0].toString())

        //Set corresponding values on activity
        if (shuffleQuestions) PlaceholderFragment.questions.shuffle()
        if (!logAnswers) scoreLabel.text = "Study Mode"
        bookmarkQuestion.setImageResource(if (currentQuestion.isBookmarked == "1") R.mipmap.bookmark else R.mipmap.bookmark_empty)

        answeredQuestions = Array(questions.size) { false }

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        val prefs = getSharedPreferences(MyApp.RESUME_DATA, 0)
        if (extras.getString("callingIntent") != "StudyFragment")
            container.currentItem = extras.getInt("bookmarkOrSearchSelection")
        else if (extras.getInt("resumeQuestionNumber") > 0) {
            container.currentItem = extras.getInt("resumeQuestionNumber")
            correct = prefs.getInt("correct", 0)
            total = prefs.getInt("total", 0)
            scoreLabel.text = "Score " + correct + "/" + total
            val jsonText = prefs.getString("answeredQuestions", "")
            if (jsonText.isNotBlank())
                answeredQuestions = Gson().fromJson<Array<Boolean>>(jsonText, Array<Boolean>::class.java)
        }
        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                currentQuestion = Queries.getQuestion(this@Study, questions[position].toString())
                numberLabel.text = "Num " + (container.currentItem + 1) + "/" + container.adapter?.count
                attemped = 0
                moveToPreviousQuestionButton.setImageResource(if (position == 0) R.mipmap.left_disabled else R.mipmap.left)
                moveToNextQuestionButton.setImageResource(if (position == container.adapter!!.count - 1) R.mipmap.right_disabled else R.mipmap.right)
                bookmarkQuestion.setImageResource(
                        if (currentQuestion.isBookmarked == "1") R.mipmap.bookmark else
                            R.mipmap.bookmark_empty
                )
                val editor = prefs.edit()
                editor.putString("dlNumber", StudyFragment.dlNumber)
                editor.putString("bookCategoryID", StudyFragment.bookCategoryID)
                editor.putString("bookID", StudyFragment.bookID)
                editor.putString("categoryID", StudyFragment.categoryID)
                editor.putString("subcategoryID", StudyFragment.subcategoryID)
                editor.putInt("resumeQuestionNumber", container.currentItem)
                editor.putString("answeredQuestions", Gson().toJson(answeredQuestions))
                editor.commit()
            }
        })
        numberLabel.text = "Num " + (container.currentItem + 1) + "/" + container.adapter?.count
        closeButton.setOnClickListener { finish() }
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
                if (logAnswers && !answeredQuestions[container.currentItem]) {
                    correct++
                    Queries.updateQuestionStatistics(this, currentQuestion.questionID, 1)
                    answeredQuestions[container.currentItem] = true
                    total++
                }
            }
            if (autoNext) {
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                val handler = Handler()
                handler.postDelayed({
                    moveToNextQuestion(view)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }, 400)
            }
        } else {
            view.setBackgroundColor(resources.getColor(R.color.questionsRed))
            if (attemped == 0 && logAnswers && !answeredQuestions[container.currentItem]) {
                Queries.updateQuestionStatistics(this, currentQuestion.questionID, 0)
                answeredQuestions[container.currentItem] = true
                total++
            }
        }
        if (attemped == 0) {
            if (logAnswers) {
                scoreLabel.text = "Score " + correct + "/" + total
                val prefsEditor = getSharedPreferences(MyApp.RESUME_DATA, 0).edit()
                prefsEditor.putInt("correct", correct)
                prefsEditor.putInt("total", total)
                prefsEditor.apply()
            }
        }
        attemped = 1
    }

    private fun showRestartDialog() {
        alert {
            title = "FINISHED"
            message = "You've finished all the questions, do you want to start over?"
            positiveButton("Restart") { container.currentItem = 0 }
            noButton { }
        }.show()
    }

    fun changeLogingAnswers(view: View) {
        if (logAnswers)
            alert {
                yesButton {
                    logAnswers = false
                    scoreLabel.text = "Study Mode"
                }
                noButton { }
                title = "Turn OFF Logging Answers"
                message = "Turn OFF tracking for correct or incorrect answers"
            }.show() else {
            alert {
                yesButton {
                    logAnswers = true
                    scoreLabel.text = "Score " + correct + "/" + total
                }
                noButton { }
                title = "Turn ON Logging Answers"
                message = "Turn ON tracking for correct or incorrect answers"
            }.show()
        }
    }

    fun bookmarkQuestion(view: View) {
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
        if (container.currentItem == container.adapter?.count!! - 1)
            showRestartDialog()
        else
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
