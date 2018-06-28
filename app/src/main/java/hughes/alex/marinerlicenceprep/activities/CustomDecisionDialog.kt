package hughes.alex.marinerlicenceprep.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.custom_decision_dialog.*

class CustomDecisionDialog(a: Activity, private val dialogTitle: String, private val dialogText: String) : Dialog(a){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_decision_dialog)
        btn_cancel.setOnClickListener{ v -> cancelDialog()}
        btn_ok.setOnClickListener{ v -> confirm()}
        txt_Title.text = dialogTitle
        txt_dialog.text = dialogText
    }

    private fun confirm(){
        //TODO ovde se menja bookmark
    }

    private fun cancelDialog(){
        this.dismiss()
    }
}