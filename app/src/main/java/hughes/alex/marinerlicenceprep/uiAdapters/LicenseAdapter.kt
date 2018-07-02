package hughes.alex.marinerlicenceprep.uiAdapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.entity.LicenseEntity
import kotlinx.android.synthetic.main.licence_item.view.*


class LicenseAdapter(val items : ArrayList<LicenseEntity>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.licence_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.endorsmentTextView.text = items[position].endorsement
        holder.tonnageTextView.text = items[position].tonnageGroup
        if(items[position].bookCategoryID == 2)
        holder.routeTextView.text = items[position].route
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val endorsmentTextView = view.endorsmentTextView
    val tonnageTextView = view.tonnageTextView
    val routeTextView = view.routeTextView
}