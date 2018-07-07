package hughes.alex.marinerlicenceprep.fragments
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.activities.Illustration
import hughes.alex.marinerlicenceprep.activities.Study
import hughes.alex.marinerlicenceprep.database.Queries
import kotlinx.android.synthetic.main.fragment_study_activity.view.*

class PlaceholderFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_study_activity, container, false)
        val questionID = questions[arguments?.getInt(ARG_SECTION_NUMBER)!!]
        val question = Queries.getQuestion(context!!, questionID.toString())
        rootView.questionTitle.text = question.subcategory + "\n" + question.questionNumber
        rootView.questionText.text = question.question
        rootView.answer1.text = question.answerOne
        rootView.answer2.text = question.answerTwo
        rootView.answer3.text = question.answerThree
        rootView.answer4.text = question.answerFour
        if ((context as Study).showAnswers)
            setCorrect(question.correctAnswer, rootView)
        if (question.illustration.isNotBlank()) {
            rootView.openIllustration.visibility = View.VISIBLE
            rootView.openIllustration.setOnClickListener {
                val intent = Intent(context, Illustration::class.java)
                intent.putExtra("illustrationName", question.illustration)
                startActivity(intent)
            }
        }
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
        lateinit var questions: ArrayList<Int>
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