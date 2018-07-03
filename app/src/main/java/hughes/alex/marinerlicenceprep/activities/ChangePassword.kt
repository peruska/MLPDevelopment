package hughes.alex.marinerlicenceprep.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        cancel.setOnClickListener { finish() }
        save.setOnClickListener {
            savePasswordChange()
            finish()
        }
    }


    private fun savePasswordChange() {
        //TODO
    }
}
