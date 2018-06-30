package hughes.alex.marinerlicenceprep.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.os.Bundle
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.entity.Questions
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.android.synthetic.main.fragment_study_activity.view.*

class Study : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
        PlaceholderFragment.questions = Queries.loadQuestions(this, "All Engine", true, 1, 1)
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter
        closeButton.setOnClickListener { finish() }


    }

    fun answerQuestion(view: View) {

        if ((view.background as ColorDrawable).color == resources.getColor(R.color.questionsRed))
            view.setBackgroundColor(resources.getColor(R.color.questionsGreen))
        else
            view.setBackgroundColor(resources.getColor(R.color.questionsRed))
    }

    fun moveToNextQuestion(view: View) {
        container.currentItem = container.currentItem + 1
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        var questions = Queries.loadQuestions(this@Study, "All Engine", false, 1, 1)
        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(position + 1)
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
            rootView.questionTitle.text = question.subcategory + "\n"+ question.questionNumber
            rootView.questionText.text = question.question
            rootView.answer1.text = question.answerOne
            rootView.answer2.text = question.answerTwo
            rootView.answer3.text = question.answerThree
            rootView.answer4.text = question.answerFour
            return rootView
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
