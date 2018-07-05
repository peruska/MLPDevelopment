package hughes.alex.marinerlicenceprep.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.activities.BookmarkList
import kotlinx.android.synthetic.main.bookmark_list_item.view.*
import kotlinx.android.synthetic.main.bookmarked_fragment.view.*


class BookmarkedFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bookmarked_fragment, container, false)
        val licenseBooks = (context!!.applicationContext as MyApp).getLicenseBooks()
        view.bookmarkListView.adapter= ArrayAdapter<String>(context ,R.layout.bookmark_list_item, licenseBooks)
        view.bookmarkListView.setOnItemClickListener { adapterView, view, i, l ->
            val intent = Intent(context!!, BookmarkList::class.java)
            intent.putExtra("bookName", view.bookNameTextView.text.toString())
            startActivity(intent)
        }
        return view
    }
}