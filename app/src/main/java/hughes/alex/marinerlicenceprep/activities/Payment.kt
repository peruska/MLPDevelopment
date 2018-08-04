package hughes.alex.marinerlicenceprep.activities

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.exception.AuthenticationException
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.stripe.*
import java.io.IOException
import java.lang.Exception


class Payment : AppCompatActivity() {

    private lateinit var stripeClass: Stripe
    var amount: Int = 0
    lateinit var name: String
    private lateinit var card: Card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stripe)
        try{
            stripeClass = Stripe(this, "pk_test_phihB2GTlTnHz5f7jkblfi1G") //TODO testing public key
        } catch ( e: AuthenticationException ){
            e.printStackTrace()
        }
    }

    fun onClk(view: View) {
        submitCard(view)
    }

    private fun submitCard(view: View ){
        try {
            card = card_input_widget.card!!
        }
        catch (e: Exception){
            return
        }
        card.currency = "usd"
        amount = 150    //TODO this amount is in cents. This is actually 1.5 dollars

        stripeClass.createToken(card, "pk_test_phihB2GTlTnHz5f7jkblfi1G", object : TokenCallback { //TODO testing public key
            override fun onSuccess(token: Token?) {
                Toast.makeText(applicationContext, "Token created: " + token!!.id, Toast.LENGTH_LONG).show()
                StripeCharge(token.id, "Demo Description", amount, this@Payment).execute()
            }

            override fun onError(error: Exception?) {
                Log.d("Stripe", error?.localizedMessage)
            }

        })
    }

    class StripeCharge(private val token: String, val name: String, private val amount: Int, val context: Context) : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            Thread{
                postData(name, token, amount.toString())
                println("Thread start")
            }.start()
            return "Done"
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.e("Result",result)
            println("Post execute $result")
        }

        private fun postData(description: String, token: String, amount: String){
            try{

                val stringRequest = object : StringRequest(Request.Method.POST, "https://marinerlicenseprep.com/api/Charge", Response.Listener { s ->
                    // Your success code here
                    println("Success POST")
                }, Response.ErrorListener { e ->
                    // Your error code here
                    println("Error POST")
                }) {
                    override fun getParams(): Map<String, String> {
                        val params = HashMap<String, String>()
                        params["method"] = "charge"
                        params["description"] = description
                        params["source"] = token
                        params["amount"] = amount
                        params["currency"] = "usd"

                        return params
                    }
                }
                val  requestQueue = Volley.newRequestQueue(context)
                requestQueue.add<String>(stringRequest)
            }catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}
