package hughes.alex.marinerlicenceprep.uiAdapters

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.entity.LicenseEntity
import kotlinx.android.synthetic.main.licence_item.view.*
import com.google.gson.Gson
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import hughes.alex.marinerlicenceprep.activities.EditLicenceRating
import hughes.alex.marinerlicenceprep.activities.Home
import hughes.alex.marinerlicenceprep.database.Queries
import org.jetbrains.anko.AlertDialogBuilder
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class LicenseAdapter(val items: ArrayList<LicenseEntity>, val context: Context,
                     var activeDL: Int? = null, var activeBookCategoryID: Int? = null)
    : RecyclerView.Adapter<LicenseAdapter.ViewHolder>() {


    lateinit var visibleCheckMark: ImageView
    lateinit var activeView: View
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.licence_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val view = holder.itemView
        view.setOnClickListener {
            if (context is EditLicenceRating) {
                if (::visibleCheckMark.isInitialized)
                    visibleCheckMark.visibility = View.INVISIBLE
                visibleCheckMark = view.imageView
                view.imageView.visibility = View.VISIBLE

                if (::activeView.isInitialized)
                    activeView.setBackgroundColor(Color.WHITE)
                activeView = view
                activeView.setBackgroundColor(context.resources.getColor(R.color.lightGray))
                val dialog = ProgressDialog(context)
                dialog.setTitle("Saving Changes")
                dialog.setMessage("Please Wait")
                dialog.setCancelable(false)
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                dialog.show()
                doAsync {
                    val prefs = context.getSharedPreferences(MyApp.USER_LICENSE_DATA_VALUES, 0)
                    val prefsEditor = prefs.edit()
                    val itemClicked = items[position]
                    val json = Gson().toJson(
                            if (itemClicked.bookCategoryID == 1)
                                Queries.getBooksWithSubcategories(context, itemClicked.bookCategoryID, itemClicked.dlNumber)
                            else
                                Queries.getBooksCategoriesSubcategories(context, itemClicked.bookCategoryID, itemClicked.dlNumber)
                    )
                    prefsEditor.putString(MyApp.USER_LICENSE_DATA_VALUES, json)
                    prefsEditor.putString(MyApp.DL_NUMBER, itemClicked.dlNumber.toString())
                    prefsEditor.putString(MyApp.CATEGORY, itemClicked.bookCategoryID.toString())
                    activeDL = itemClicked.dlNumber
                    activeBookCategoryID = itemClicked.bookCategoryID
                    prefsEditor.commit()

                    uiThread {
                        dialog.dismiss()
                        (context.application as MyApp).fetchLicenseBooksAsListItem()
                    }
                }
            } else {
                val builder = AlertDialogBuilder(context)
                builder.title("Save Rating?")
                builder.message("You have selected "
                        + view.endorsmentTextView.text.toString()
                        + ".\nThis can be changed from the settings page."
                )
                builder.positiveButton("Save") {
                    val prefs = context.getSharedPreferences(MyApp.USER_LICENSE_DATA_VALUES, 0)
                    val prefsEditor = prefs.edit()
                    val itemClicked = items[position]
                    val json = Gson().toJson(
                            if (itemClicked.bookCategoryID == 1)
                                Queries.getBooksWithSubcategories(context, itemClicked.bookCategoryID, itemClicked.dlNumber)
                            else
                                Queries.getBooksCategoriesSubcategories(context, itemClicked.bookCategoryID, itemClicked.dlNumber)
                    )
                    prefsEditor.putString(MyApp.USER_LICENSE_DATA_VALUES, json)
                    prefsEditor.putString(MyApp.DL_NUMBER, itemClicked.dlNumber.toString())
                    prefsEditor.putString(MyApp.CATEGORY, itemClicked.bookCategoryID.toString())
                    prefsEditor.commit()
                    context.startActivity(Intent(context, Home::class.java))
                    (context as Activity).finish()
                }
                builder.negativeButton("Cancel") { }
                builder.show()
            }
        }
        if (items[position].dlNumber == activeDL && items[position].bookCategoryID == activeBookCategoryID) {
            if (::visibleCheckMark.isInitialized)
                visibleCheckMark.visibility = View.INVISIBLE
            holder.checkMarkImageView.visibility = View.VISIBLE
            visibleCheckMark = holder.checkMarkImageView
            if (::activeView.isInitialized)
                activeView.setBackgroundColor(Color.WHITE)
            activeView = holder.itemView
            activeView.setBackgroundColor(context.resources.getColor(R.color.lightGray))
        }
        holder.endorsmentTextView.text = items[position].endorsement
        holder.tonnageTextView.text = items[position].tonnageGroup
        if (items[position].bookCategoryID == 2) {
            holder.routeTextView.text = items[position].route
            holder.licenseImageView.setImageResource(R.drawable.compass_64)
        } else
            holder.licenseImageView.setImageResource(R.mipmap.gear_wheel)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val endorsmentTextView: TextView = view.endorsmentTextView
        val tonnageTextView: TextView = view.tonnageTextView
        val routeTextView: TextView = view.routeTextView
        val licenseImageView: ImageView = view.licenseImageView
        val checkMarkImageView: ImageView = view.imageView
    }
}
