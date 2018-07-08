package hughes.alex.marinerlicenceprep.fragments
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.TextViewCompat
import android.util.TypedValue
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

        //Query question based on passed argument
        val questionID = questions[arguments?.getInt("question_number")!!]
        val question = Queries.getQuestion(context!!, questionID.toString())

        //Set question title and text
        rootView.questionTitle.text = question.subcategory + "\n" + question.questionNumber
        rootView.questionText.text = question.question

        //Set text values and enable auto-sizing
        rootView.answer1.text = question.answerOne
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(rootView.answer1, 10, 24, 1, TypedValue.COMPLEX_UNIT_SP)
        rootView.answer2.text = question.answerTwo
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(rootView.answer2, 10, 24, 1, TypedValue.COMPLEX_UNIT_SP)
        rootView.answer3.text = question.answerThree
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(rootView.answer3, 10, 24, 1, TypedValue.COMPLEX_UNIT_SP)
        rootView.answer4.text = question.answerFour
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(rootView.answer4, 10, 24, 1, TypedValue.COMPLEX_UNIT_SP)

        //If show answers is enabled, mark correct answer
        if ((context as Study).showAnswers)
            setCorrect(question.correctAnswer, rootView)

        //If illustration is available, enable button for opening it, and handle on click
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
        //Compare answers and mark correct
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
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            val fragment = PlaceholderFragment()
            val args = Bundle()
            args.putInt("question_number", sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}