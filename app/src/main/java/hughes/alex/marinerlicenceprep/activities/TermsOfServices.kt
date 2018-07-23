package hughes.alex.marinerlicenceprep.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import hughes.alex.marinerlicenceprep.R

class TermsOfServices : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_of_services)
    }

    fun backToSettings(view: View) {
        finish()
    }
}
