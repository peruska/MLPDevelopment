package hughes.alex.marinerlicenceprep.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.exception.AuthenticationException
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.stripe.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception


class Payment : AppCompatActivity() {

    private lateinit var stripeClass: Stripe
    var amount: Int = 0
    lateinit var name: String
    private lateinit var card: Card
    lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stripe)
        if (intent.extras.getString("duration") == "oneMonth")
            button.text = "PAY NOW $9.99" else
            button.text = "PAY NOW $24.99"
        try {
            stripeClass = Stripe(this, "pk_test_phihB2GTlTnHz5f7jkblfi1G") //TODO testing public key
        } catch (e: AuthenticationException) {
            e.printStackTrace()
        }
    }

    fun onClk(view: View) {
        with(card_input_widget) {
            try {
                if (!card!!.validateCard()) {
                    toast("Invalid card!")
                    requestFocus()
                }
            } catch (e: Exception) {
                toast("Invalid card!")
                requestFocus()
                return
            }
        }

        with(nameInput) {
            if (text.isEmpty()) {
                error = "Please enter your name!"
                requestFocus()
                return
            }
        }
        with(address1) {
            if (text.isEmpty()) {
                error = "Please enter your primary address!"
                requestFocus()
                return
            }
        }
        with(city) {
            if (text.isEmpty()) {
                error = "Please enter your city!"
                requestFocus()
                return
            }
        }
        with(zipCode) {
            if (text.isEmpty()) {
                error = "Please enter your zip code!"
                requestFocus()
                return
            }
        }
        dialog = indeterminateProgressDialog("Processing payments")
        submitCard(view)
    }

    private fun submitCard(view: View) {
        try {
            card = card_input_widget.card!!
        } catch (e: Exception) {
            return
        }
        card.currency = "usd"
        card.name = nameInput.toString()
        card.addressLine1 = address1.text.toString()
        card.addressLine2 = address2.text.toString()
        card.addressZip = zipCode.text.toString()
        card.addressCity = city.text.toString()
        amount = if (intent.extras.getString("duration") == "oneMonth") 999 else 2499
        name = MyApp.defaultUser!!.username

        stripeClass.createToken(card, "pk_test_phihB2GTlTnHz5f7jkblfi1G", object : TokenCallback { //TODO testing public key
            override fun onSuccess(token: Token?) {
                doAsync {
                    runOnUiThread {
                        try {
                            postData(name, token!!.id, amount.toString())
                            println("Thread start")
                            dialog.dismiss()
                        } catch (e: Exception) {
                            println(e.message)
                            dialog.dismiss()
                        }
                    }
                }
            }

            override fun onError(error: Exception?) {
                Log.d("Stripe", error?.localizedMessage)
            }

        })
    }

    private fun postData(description: String, token: String, amount: String) {
        try {
            println("Post method started")
            val stringRequest = object : StringRequest(Request.Method.POST, "https://marinerlicenseprep.com/api/Charge", Response.Listener { s ->
                val response = JSONObject(s)
                if (response.getString("response") == "Success")
                    alert {
                        title = "You have successfully purchased subscription!"
                        positiveButton("OK") { finish() }
                    }.show()
                else {
                    toast("Failed to purchase subscription")
                }
                println("Success POST")
            }, Response.ErrorListener { e ->
                toast("Failed to purchase subscription, check your internet connection!")
                println("Error POST")
            }) {
                override fun getParams(): Map<String, String> {
                    val email = MyApp.defaultUser?.email
                    val params = HashMap<String, String>()
                    params["method"] = "charge"
                    params["description"] = description
                    params["source"] = token
                    params["amount"] = amount
                    params["currency"] = "usd"
                    params["email"] = email.toString()
                    return params
                }
            }
            val requestQueue = Volley.newRequestQueue(this@Payment)
            requestQueue.add<String>(stringRequest)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}
