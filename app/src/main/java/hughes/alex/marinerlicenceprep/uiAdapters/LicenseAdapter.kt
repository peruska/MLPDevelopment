package hughes.alex.marinerlicenceprep.uiAdapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.entity.LicenseEntity
import kotlinx.android.synthetic.main.licence_item.view.*
import org.jetbrains.anko.AlertDialogBuilder
import com.google.gson.Gson
import android.R.id.edit
import hughes.alex.marinerlicenceprep.database.Queries


class LicenseAdapter(val items: ArrayList<LicenseEntity>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.licence_item, parent, false)
        view.setOnClickListener {
            AlertDialogBuilder(context).builder
                    .setTitle("Save Rating?")
                    .setMessage("You have selected "
                            + view.endorsmentTextView.text.toString()
                            + ".\nThis can be changed from the settings page."
                    )
                    .setPositiveButton("Save") { _, _ ->
                        val prefs = context.getSharedPreferences(MyApp.USER_LICENSE_DATA, 0)
                        val prefsEditor = prefs.edit()
                        val itemClicked = items.find { it.endorsement == view.endorsmentTextView.text.toString() }
                        if (itemClicked != null) {
                            val json = Gson().toJson(
                                    if (itemClicked.bookCategoryID == 1)
                                        Queries.getBooksWithSubcategories(context, itemClicked.bookCategoryID, itemClicked.dlNumber)
                                    else
                                        Queries.getBooksCategoriesSubcategories(context, itemClicked.bookCategoryID, itemClicked.dlNumber)
                            )
                            prefsEditor.putString(MyApp.USER_LICENSE_DATA_VALUES, json)
                            prefsEditor.putString(MyApp.DL_NUMBER, itemClicked.dlNumber.toString())
                            prefsEditor.putString(MyApp.CATEGORY, itemClicked.bookCategoryID.toString())
                            println(json)
                            prefsEditor.apply()
                        }
                    }
                    .setNegativeButton("Cancel") { _, _ -> }
                    .show()
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.endorsmentTextView.text = items[position].endorsement
        holder.tonnageTextView.text = items[position].tonnageGroup
        if (items[position].bookCategoryID == 2) {
            holder.routeTextView.text = items[position].route
            //holder.licenseImageView.setImageResource(R.drawable.compass_64)
        } else
            holder.licenseImageView.setImageResource(R.mipmap.gear_wheel)
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val endorsmentTextView = view.endorsmentTextView
    val tonnageTextView = view.tonnageTextView
    val routeTextView = view.routeTextView
    val licenseImageView = view.licenseImageView
}