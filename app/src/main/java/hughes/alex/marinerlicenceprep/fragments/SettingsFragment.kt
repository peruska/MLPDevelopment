package hughes.alex.marinerlicenceprep.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hughes.alex.marinerlicenceprep.AuthService
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.activities.*
import kotlinx.android.synthetic.main.settings_fragment.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar


class SettingsFragment : Fragment() {

    private val changeBookmarkTitleText = "Reset Bookmarks?"
    private val changeBookmarkDialogText = "Are you sure you want to reset your Bookmarks?"
    private val resetScoresTitle = "Reset Scores?"
    private val resetScoreDialog = "Are you sure you want to reset your scores?"
    private val logoutTitle = "Logout"
    private val logoutDialog = "Are you sure you want to log out?"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.settings_fragment, container, false)
        view.allEngine.setOnClickListener { startActivity(Intent(context, EditSubscriptionProfile::class.java)) }
        view.editLicenceRating.setOnClickListener { startActivity(Intent(context, EditLicenceRating::class.java)) }
        view.changeEmail.setOnClickListener { startActivity(Intent(context, ChangeEmail::class.java)) }
        view.changePassword.setOnClickListener { startActivity(Intent(context, ChangePassword::class.java)) }
        view.submitFeedback.setOnClickListener { showDialog() }
        view.reset_bookmark.setOnClickListener { showDecisionDialog(context as Activity, changeBookmarkTitleText, changeBookmarkDialogText) }
        view.reset_scores.setOnClickListener { showDecisionDialog(context as Activity, resetScoresTitle, resetScoreDialog) }
        view.terms_of_service.setOnClickListener { startActivity(Intent(context as Activity, TermsOfServices::class.java)) }
        view.privacy_policy.setOnClickListener { startActivity(Intent(context as Activity, PrivacyPolicy::class.java)) }
        view.logout.setOnClickListener {
            context!!.alert(logoutDialog, logoutTitle) {
                yesButton {
                    AuthService(context!!).logOut()
                    startActivity(Intent(context, LoginActivity::class.java))
                    (context as Activity).finish()
                }
                noButton {}
            }.show()
        }
        return view
    }


    private fun showDialog() {
        val customDialog = CustomDialog(context as Activity)
        customDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.show()
    }

    private fun showDecisionDialog(activity: Activity, title: String, dialogText: String) {
        val customDecisionDialog = CustomDecisionDialog(activity, title, dialogText)
        customDecisionDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDecisionDialog.show()
    }
}