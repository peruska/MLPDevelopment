package hughes.alex.marinerlicenceprep.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.custom_dialog.*

class CustomDialog(a: Activity) : Dialog(a), android.view.View.OnClickListener {


    override fun onClick(v: View?) {
        dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog)
        btn_ok.setOnClickListener(this)
    }
}