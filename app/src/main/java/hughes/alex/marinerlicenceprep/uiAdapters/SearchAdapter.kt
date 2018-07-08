package hughes.alex.marinerlicenceprep.uiAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.activities.Study
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.fragments.PlaceholderFragment
import kotlinx.android.synthetic.main.search_item.view.*

class SearchAdapter(val items: ArrayList<Int>, val context: Context, val querySearchWord: ArrayList<String>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false)
        view.answer1.isClickable = false
        view.answer2.isClickable = false
        view.answer3.isClickable = false
        view.answer4.isClickable = false
        view.questionText.isClickable = false
        view.questionTitle.isClickable = false
        view.setOnClickListener {
            val intent = Intent(context, Study::class.java)
            intent.putExtra("autoNext", true)
            intent.putExtra("shuffleQuestions", false)
            intent.putExtra("logAnswers", false)
            intent.putExtra("showAnswers", false)
            intent.putExtra("callingIntent", "StudyFragment")
            PlaceholderFragment.questions = items
                    context.startActivity(intent)
        }
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = Queries.getQuestion(context, items[position].toString())
        if(querySearchWord[0] == "") {
            holder.questionText.text = question.question
            holder.questionTitle.text = question.subcategory
            holder.answer4.text = question.answerFour
            holder.answer3.text = question.answerThree
            holder.answer2.text = question.answerTwo
            holder.answer1.text = question.answerOne
            if(querySearchWord[1] == "weakest!")
            {
                holder.countOfAnswers.text = "Wrong: ${question.numberOfTimesWrong}\nCorrect: ${question.numberOfTimesCorrect}"
            }
        }else {
            val value = querySearchWord[0].toLowerCase()
            fun makeSpannable(text: String, query: String): Spannable {
                val mSpannable: Spannable = SpannableString(text)
                val startIndex = text.toLowerCase().indexOf(query)
                val endIndex = query.length + startIndex
                mSpannable.setSpan(ForegroundColorSpan(Color.RED), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                return mSpannable
            }
            if (question.subcategory.toLowerCase().contains(value)) {
                holder.questionTitle.setText(makeSpannable(question.subcategory, value), TextView.BufferType.SPANNABLE)
            } else {
                holder.questionTitle.text = question.subcategory
            }
            if (question.question.toLowerCase().contains(value)) {
                holder.questionText.setText(makeSpannable(question.question, value), TextView.BufferType.SPANNABLE)
            } else {
                holder.questionText.text = question.question
            }
            if (question.answerOne.toLowerCase().contains(value)) {
                holder.answer1.setText(makeSpannable(question.answerOne, value), TextView.BufferType.SPANNABLE)
            } else {
                holder.answer1.text = question.answerOne
            }
            if (question.answerTwo.toLowerCase().contains(value)) {
                holder.answer2.setText(makeSpannable(question.answerTwo, value), TextView.BufferType.SPANNABLE)
            } else {
                holder.answer2.text = question.answerTwo
            }
            if (question.answerThree.toLowerCase().contains(value)) {
                holder.answer3.setText(makeSpannable(question.answerThree, value), TextView.BufferType.SPANNABLE)
            } else {
                holder.answer3.text = question.answerThree
            }
            if (question.answerFour.toLowerCase().contains(value)) {
                holder.answer4.setText(makeSpannable(question.answerFour, value), TextView.BufferType.SPANNABLE)
            } else {
                holder.answer4.text = question.answerFour
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val answer1 = view.answer1
        val answer2 = view.answer2
        val answer3 = view.answer3
        val answer4 = view.answer4
        val questionTitle = view.questionTitle
        val countOfAnswers = view.countOfAnswers
        val questionText = view.questionText
    }
}
