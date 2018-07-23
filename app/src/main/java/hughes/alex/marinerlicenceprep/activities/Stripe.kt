package hughes.alex.marinerlicenceprep.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Token
import hughes.alex.marinerlicenceprep.NetworkSingleton.Companion.getNetworkSingletonInstance
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.stripe.*
import org.jetbrains.anko.toast
import java.lang.Exception
import java.util.*

class Stripe : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stripe)
    }

    fun onClk(view: View) {
        val cardToSave = card_input_widget.card
        if (cardToSave == null) {
            toast("Bad inputs")
        } else {
            val stripe = Stripe(this, "pk_test_phihB2GTlTnHz5f7jkblfi1G");
            stripe.createToken(
                    cardToSave, object :
                    TokenCallback {
                override fun onSuccess(token: Token?) {
                    println(token!!.card.toString())                //TODO Change url
                    val signInRequest = object : StringRequest(Method.POST, "https://webhook.site/da78791b-7eee-4e6f-8e16-8d749af1f181",
                            Response.Listener { response ->
                                println(response)
                            },
                            Response.ErrorListener { error -> println(error) }) {
                        override fun getParams(): Map<String, String> {
                            val params = HashMap<String, String>()
                            params["email"] = "peruskatestira@gmail.com"//defaultUser!!.email
                            //TODO FIll with appropriate params
                            return params
                        }
                    }
                    getNetworkSingletonInstance(this@Stripe).requestQueue.add(signInRequest)
                }

                override fun onError(error: Exception?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }
    }
}
