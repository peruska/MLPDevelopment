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
import kotlinx.android.synthetic.main.weakest_question_fragment.view.*

class SearchAdapter(private val items: ArrayList<Int>,
                    val context: Context,
                    private val querySearchWord: ArrayList<String>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.search_item, parent, false))
    }

    private fun makeSpannable(text: String, query: String): Spannable {
        val mSpannable: Spannable = SpannableString(text)
        val startIndex = text.toLowerCase().indexOf(query)
        val endIndex = query.length + startIndex
        mSpannable.setSpan(ForegroundColorSpan(Color.RED), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return mSpannable
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position != 0){
            holder.num.visibility = View.GONE
        }else{
            when(querySearchWord[1]) {
                "weakest!" ->{
                    holder.num.text = "Logged: " + items.size + " Question(s)"
            }
                "bookmarked!" ->{
                    holder.num.text ="" + items.size + " Question(s)"
                }
                else -> {
                    holder.num.text = "Search Results: " + items.size + " Question(s)"
                }
            }
        }
        val question = Queries.getQuestion(context, items[position].toString())
        holder.mainView.setOnClickListener {
            val intent = Intent(context, Study::class.java)
            intent.putExtra("autoNext", true)
            intent.putExtra("shuffleQuestions", false)
            intent.putExtra("logAnswers", false)
            intent.putExtra("showAnswers", false)
            intent.putExtra("callingIntent", "StudyFragment")
            intent.putExtra("resumeQuestionNumber", position)
            PlaceholderFragment.questions = items
            context.startActivity(intent)
        }
        if (querySearchWord[0] == "") {
            holder.questionText.text = question.question
            holder.questionTitle.text = question.subcategory
            holder.answer4.text = question.answerFour
            holder.answer3.text = question.answerThree
            holder.answer2.text = question.answerTwo
            holder.answer1.text = question.answerOne
            if (querySearchWord[1] == "weakest!") {
                holder.countOfAnswers.text = "Wrong: ${question.numberOfTimesWrong}\nCorrect: ${question.numberOfTimesCorrect}"
            }
        } else {
            val value = querySearchWord[0].toLowerCase()

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
        val mainView: View = view
        val answer1: TextView = view.answer1
        val answer2: TextView = view.answer2
        val answer3: TextView = view.answer3
        val answer4: TextView = view.answer4
        val questionTitle: TextView = view.questionTitle
        val countOfAnswers: TextView = view.countOfAnswers
        val questionText: TextView = view.questionText
        val num: TextView = view.num
    }
}
