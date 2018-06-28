package hughes.alex.marinerlicenceprep.activities


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.custom_action_bar_edit_subscription.*

class EditSubscriptionProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_subscription_profile)

        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.custom_action_bar_edit_subscription)

        custom_back_click.setOnClickListener { v-> finish()}
    }
}
