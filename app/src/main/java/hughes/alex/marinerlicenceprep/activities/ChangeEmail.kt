package hughes.alex.marinerlicenceprep.activities

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.view.View
import hughes.alex.marinerlicenceprep.AuthService
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.MyApp.Companion.defaultUser
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.activity_change_email.*
import kotlinx.android.synthetic.main.custom_action_bar_change_email.*

class ChangeEmail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)
        cancel.setOnClickListener { finish() }
        save.setOnClickListener { saveEmailChange() }
        emailValue.hint = "peruskatestira@gmail.com"
    }

    private fun saveEmailChange(){
        AuthService(this).changeEmail(emailValue.text.toString())
    }

}
