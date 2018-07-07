package hughes.alex.marinerlicenceprep.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.graph_fragment.view.*
import com.google.gson.reflect.TypeToken
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.entity.Book
import hughes.alex.marinerlicenceprep.models.BooksCategoriesSubcategories
import hughes.alex.marinerlicenceprep.models.StudyExpandableListItem
import hughes.alex.marinerlicenceprep.uiAdapters.SearchAdapter
import kotlinx.android.synthetic.main.weakest_question_fragment.view.*
import kotlinx.android.synthetic.main.welcome_fragment.view.*

class WeakestQuestionsFragment : Fragment() {
    private var title: String? = null
    private var page: Int = 0

    companion object {
        fun newInstance(page: Int, title: String): WeakestQuestionsFragment {
            val fragmentFirst = WeakestQuestionsFragment()
            val args = Bundle()
            args.putInt("someInt", page)
            args.putString("someTitle", title)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        page = arguments!!.getInt("someInt", 0)
        title = arguments!!.getString("someTitle")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.weakest_question_fragment, container, false)
        view.weakestQuestionsRecycler.layoutManager = LinearLayoutManager(context)
        view.weakestQuestionsRecycler.adapter = SearchAdapter(Queries.getQuestionIDs(context!!, "1", "All Engine","1", "1", "1"), context!!, arrayListOf(""))
        return view
    }
}