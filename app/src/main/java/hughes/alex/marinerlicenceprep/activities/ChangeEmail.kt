package hughes.alex.marinerlicenceprep.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import hughes.alex.marinerlicenceprep.AuthService
import hughes.alex.marinerlicenceprep.MyApp.Companion.defaultUser
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.activity_change_email.*

class ChangeEmail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)
        cancel.setOnClickListener { finish() }
        save.setOnClickListener { saveEmailChange() }
        emailValue.hint = defaultUser?.email
    }

    private fun saveEmailChange(){
        AuthService(this).changeEmail(emailValue.text.toString())
    }

}
