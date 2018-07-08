package hughes.alex.marinerlicenceprep.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.database.Queries
import hughes.alex.marinerlicenceprep.fragments.SettingsFragment
import kotlinx.android.synthetic.main.custom_decision_dialog.*
import org.jetbrains.anko.indeterminateProgressDialog

class CustomDecisionDialog(a: Activity, private val dialogTitle: String, private val dialogText: String, private val decisionType: Int) : Dialog(a){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_decision_dialog)
        btn_cancel.setOnClickListener{ cancelDialog()}
        btn_ok.setOnClickListener{ confirm()}
        txt_Title.text = dialogTitle
        txt_dialog.text = dialogText
    }

    private fun confirm(){
        this.dismiss()
        val waitDialog = context.indeterminateProgressDialog("Please wait", "Resetting")
        when (decisionType){
            SettingsFragment.resetBookmarkType ->{
                Queries.resetBookmarks(context)
            }
            SettingsFragment.resetScoresType ->{
                Queries.resetScores(context)
            }
        }
        waitDialog.dismiss()
    }

    private fun cancelDialog(){
        this.dismiss()
    }
}