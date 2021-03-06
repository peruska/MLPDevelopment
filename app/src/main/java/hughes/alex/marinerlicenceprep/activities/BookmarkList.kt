package hughes.alex.marinerlicenceprep.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.uiAdapters.SearchAdapter
import kotlinx.android.synthetic.main.activity_bookmark_list.*

class BookmarkList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark_list)
        bookmarkRecyclerView.layoutManager = LinearLayoutManager(this)
        val questions = Queries.getBookmarkedQuestions(this, intent.extras.getString("bookName"))
        if(questions.size>0){
        bookmarkRecyclerView.adapter = SearchAdapter(questions, this, arrayListOf("", ""))
            lines.visibility = View.GONE
        }
        textView7.text = "Bookmarked: " + intent.extras.getString("bookName")
    }

    fun back(view: View){
        finish()
    }
}
