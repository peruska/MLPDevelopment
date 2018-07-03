package hughes.alex.marinerlicenceprep.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.uiAdapters.LicenseAdapter
import kotlinx.android.synthetic.main.activity_edit_licence_rating.*

class EditLicenceRating : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_licence_rating)
        val prefs = this.getSharedPreferences(MyApp.USER_LICENSE_DATA_VALUES, 0)
        val dlNumber = prefs.getString(MyApp.DL_NUMBER, "")
        val bookCategoryID = prefs.getString(MyApp.CATEGORY, "")
        editLicenseRecyclerView.layoutManager = LinearLayoutManager(this)
        editLicenseRecyclerView.adapter = LicenseAdapter(
                Queries.getLicense(this),
                this,
                dlNumber.toInt(),
                bookCategoryID.toInt())
    }

    fun backToSettings(view: View) {
        finish()
    }
}
