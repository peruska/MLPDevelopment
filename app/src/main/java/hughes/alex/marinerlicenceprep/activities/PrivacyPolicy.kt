package hughes.alex.marinerlicenceprep.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.custom_action_bar_privacy_policy.*

class PrivacyPolicy : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.custom_action_bar_privacy_policy)

        custom_back_click_privacy_policy.setOnClickListener { v -> finish() }
        settings_privacy_policy.setOnClickListener { v -> finish() }
    }
}
