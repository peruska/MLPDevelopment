package hughes.alex.marinerlicenceprep.uiAdapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.entity.Questions
import kotlinx.android.synthetic.main.fragment_study_activity.view.*


class SearchAdapter(val items: ArrayList<Int>, val context: Context) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = Queries.getQuestion(context, items[position].toString())
        holder.answer1.text = question.answerOne
        holder.answer2.text = question.answerTwo
        holder.answer3.text = question.answerThree
        holder.answer4.text = question.answerFour
        holder.questionText.text = question.question
        holder.questionTitle.text = question.subcategory
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val answer1 = view.answer1
        val answer2 = view.answer2
        val answer3 = view.answer3
        val answer4 = view.answer4
        val questionTitle = view.questionTitle
        val questionText = view.questionText
    }
}
