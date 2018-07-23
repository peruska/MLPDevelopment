package hughes.alex.marinerlicenceprep.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hughes.alex.marinerlicenceprep.AuthService
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.activities.*
import hughes.alex.marinerlicenceprep.database.Queries
import kotlinx.android.synthetic.main.settings_fragment.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.noButton
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.yesButton


class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.settings_fragment, container, false)
        view.allEngine.setOnClickListener { startActivity(Intent(context, EditSubscriptionProfile::class.java)) }
        view.editLicenceRating.setOnClickListener { startActivity(Intent(context, EditLicenceRating::class.java)) }
        view.changeEmail.setOnClickListener { startActivity(Intent(context, ChangeEmail::class.java)) }
        view.changePassword.setOnClickListener { startActivity(Intent(context, ChangePassword::class.java)) }
        view.submitFeedback.setOnClickListener {
            alert {
                title = "Feedback"
                messageResource = R.string.feedback_text
                yesButton {}
            }.show()
        }
        view.reset_bookmark.setOnClickListener {
            alert {
                title = "Reset Bookmarks?"
                message = "Are you sure you want to reset your Bookmarks?"
                yesButton {
                    val waitDialog = context?.indeterminateProgressDialog("Please wait", "Resetting")
                    waitDialog?.show()
                    Queries.resetBookmarks(context!!)
                    waitDialog?.dismiss()
                }
                noButton { }
            }.show()
        }
        view.reset_scores.setOnClickListener {
            alert {
                title = "Reset Bookmarks?"
                message = "Are you sure you want to reset your scores?"
                yesButton {
                    val waitDialog = context?.indeterminateProgressDialog("Please wait", "Resetting")
                    waitDialog?.show()
                    Queries.resetScores(context!!)
                    waitDialog?.dismiss()
                }
                noButton { }
            }.show()
        }
        view.terms_of_service.setOnClickListener { startActivity(Intent(context as Activity, TermsOfServices::class.java)) }
        view.privacy_policy.setOnClickListener { startActivity(Intent(context as Activity, PrivacyPolicy::class.java)) }
        view.logout.setOnClickListener {
            context!!.alert {
                title = "Logout"
                message = "Are you sure you want to log out?"
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
}