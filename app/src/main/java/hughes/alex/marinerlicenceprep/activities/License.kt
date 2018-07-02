package hughes.alex.marinerlicenceprep.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.uiAdapters.LicenseAdapter
import kotlinx.android.synthetic.main.activity_licence.*

class License : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_licence)
        Queries.getLicense(this).forEach { println(it.endorsement) }
        licenceRecycleView.layoutManager = LinearLayoutManager(this)
        licenceRecycleView.adapter = LicenseAdapter(Queries.getLicense(this), this)
    }
}
