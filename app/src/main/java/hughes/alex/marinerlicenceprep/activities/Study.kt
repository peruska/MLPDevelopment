package hughes.alex.marinerlicenceprep.activities

import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.os.Bundle
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.activities.Study.PlaceholderFragment.Companion.questions
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.entity.Questions
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.android.synthetic.main.fragment_study_activity.view.*

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
        autoNext = extras.getBoolean("autoNext")
        shuffleQuestions = extras.getBoolean("shuffleQuestions")
        logAnswers = extras.getBoolean("logAnswers")
        showAnswers = extras.getBoolean("showAnswers")
        dlNumber = extras.getString("dlNumber")
        bookCategoryID = extras.getString("bookCategoryID")
        bookID = extras.getString("bookID")
        categoryID = extras.getString("categoryID")
        subcategoryID = extras.getString("subcategoryID")
        PlaceholderFragment.questions = Queries.getQuestions(this, bookCategoryID!!, bookID, dlNumber!!, categoryID, subcategoryID)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        numberLabel.text = "Num " + (container.currentItem + 1) + "/" + container.adapter?.count
        closeButton.setOnClickListener { finish() }
        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                numberLabel.text = "Num " + (container.currentItem + 1) + "/" + container.adapter?.count
                attemped = 0
                if(position == 0) moveToPreviousQuestionButton.setImageResource(R.drawable.anchor)
                if(position == container.adapter!!.count) moveToPreviousQuestionButton.setImageResource(R.drawable.anchor)
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
            if (attemped == 0){ correct++
                Queries.updateQuestionStatistics(this, PlaceholderFragment.questions[container.currentItem].questionID, 1)
            }
            if (autoNext) moveToNextQuestion(view)
        } else {
            if(attemped==0)
                Queries.updateQuestionStatistics(this, PlaceholderFragment.questions[container.currentItem].questionID, 0)
            view.setBackgroundColor(resources.getColor(R.color.questionsRed))
        }
        if (attemped == 0) {
            total++
            scoreLabel.text = "Score " + correct + "/" + total
        }
        attemped = 1
    }

    fun moveToPreviousQuestion(view: View) {
        container.currentItem = container.currentItem - 1
        if (container.currentItem == 0) {
            moveToPreviousQuestionButton.setImageResource(R.drawable.anchor)
        }

        if (container.currentItem == (container.adapter?.count ?: -1) - 2) {
            moveToNextQuestionButton.setImageResource(R.mipmap.right_arrow)
        }
    }

    fun moveToNextQuestion(view: View) {
        container.currentItem = container.currentItem + 1

        if (container.currentItem == 1) {
            moveToPreviousQuestionButton.setImageResource(R.mipmap.left)
        }
        if (container.currentItem == (container.adapter?.count ?: -1) - 1) {
            moveToNextQuestionButton.setImageResource(R.drawable.anchor)
        }
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(position)
        }

        override fun getCount(): Int {
            return questions.size
        }
    }

    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_study_activity, container, false)
            val question = questions[arguments?.getInt(ARG_SECTION_NUMBER)!!]
            rootView.questionTitle.text = question.subcategory + "\n" + question.questionNumber
            rootView.questionText.text = question.question
            rootView.answer1.text = question.answerOne
            rootView.answer2.text = question.answerTwo
            rootView.answer3.text = question.answerThree
            rootView.answer4.text = question.answerFour
            if ((context as Study).showAnswers)
                setCorrect(question.correctAnswer, rootView)
            return rootView
        }

        private fun setCorrect(correctAnswer: String, rootView: View) {
            when (correctAnswer) {
                "A" -> rootView.answer1
                "B" -> rootView.answer2
                "C" -> rootView.answer3
                "D" -> rootView.answer4
                else -> rootView.answer1
            }.setBackgroundColor(resources.getColor(R.color.questionsGreen))
        }

        companion object {
            lateinit var questions: ArrayList<Questions>
            private val ARG_SECTION_NUMBER = "section_number"
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
}
