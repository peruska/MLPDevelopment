package hughes.alex.marinerlicenceprep.activities

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.custom_action_bar_change_password.*

class ChangePassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.custom_action_bar_change_password)

        change_pass_cancel.setOnClickListener { v -> goBack() }
    }

    private fun goBack(){
        change_pass_cancel.setTextColor(Color.RED)
        finish()
    }
}
