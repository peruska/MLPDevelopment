package hughes.alex.marinerlicenceprep.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import hughes.alex.marinerlicenceprep.AuthService
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.activity_change_password.*
import org.jetbrains.anko.toast

class ChangePassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        cancel.setOnClickListener { finish() }
        save.setOnClickListener {
            if(newPassword.text.toString() == confirmNewPassword.text.toString()){
                AuthService(this).changePassword(oldPassword.text.toString(), newPassword.text.toString())
            }
            else toast("Passwords do not match.")
        }
    }
}
