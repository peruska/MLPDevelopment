package hughes.alex.marinerlicenceprep.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.activity_privacy_policy.*

class PrivacyPolicy : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        cancel.setOnClickListener { finish() }
    }
}
