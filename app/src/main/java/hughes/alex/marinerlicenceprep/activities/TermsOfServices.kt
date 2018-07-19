package hughes.alex.marinerlicenceprep.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.view.View
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.custom_action_bar_terms_of_services.*

class TermsOfServices : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_of_services)
    }

    fun backToSettings(view: View) {
        finish()
    }
}
