package hughes.alex.marinerlicenceprep.activities


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.activity_edit_subscription_profile.*

class EditSubscriptionProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_subscription_profile)
        cancel.setOnClickListener { finish() }
    }
}
