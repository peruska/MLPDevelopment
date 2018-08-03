package hughes.alex.marinerlicenceprep.activities

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.exception.AuthenticationException
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.stripe.*
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

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
        card = card_input_widget.card!!
        card.currency = "usd"
        card.name = "Djura Karikatura"
        amount = 11

        stripeClass.createToken(card, "pk_test_phihB2GTlTnHz5f7jkblfi1G", object : TokenCallback {
            override fun onSuccess(token: Token?) {
                Toast.makeText(applicationContext, "Token created: " + token!!.id, Toast.LENGTH_LONG).show()
                StripeCharge(token.id, "Djura Karikatura", amount).execute()
            }

            override fun onError(error: Exception?) {
                Log.d("Stripe", error?.localizedMessage)
                println("Greska 1")
            }

        })
    }

    class StripeCharge(private val token: String, val name: String, private val amount: Int) : AsyncTask<String, Void, String>() {
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
            try {
                val url = URL("https://marinerlicenseprep.com/api/Charge")
                val conn = url.openConnection() as HttpURLConnection
                conn.readTimeout = 10000
                conn.connectTimeout = 15000
                conn.requestMethod = "POST"
                conn.doInput = true
                conn.doOutput = true

                val params = ArrayList<Pair<String, String>>()
                params.add(Pair("method", "charge"))
                params.add(Pair("description", description))
                params.add(Pair("source", token))
                params.add(Pair("amount", amount))

                val os: OutputStream

                os = conn.outputStream
                val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
                writer.write(params.toString()) //TODO Check this line
                writer.flush()
                writer.close()
                os.close()

            }catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}
