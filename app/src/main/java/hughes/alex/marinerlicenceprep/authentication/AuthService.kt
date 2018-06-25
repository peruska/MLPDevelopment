package hughes.alex.marinerlicenceprep.authentication

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.android.volley.Request.Method.POST
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.entity.NetworkSingleton.Companion.getNetworkSingletonInstance
import org.json.JSONObject
import java.net.URLEncoder

class AuthService(var context: Context) {

    companion object {
        const val URL = MyApp.BASE_URL + "signin"
    }

    fun signIn(email: String, password: String) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            val params = JSONObject()
            params.put("email", email)
            params.put("password", password)
            val stringRequest = JsonObjectRequest(POST, URLEncoder.encode(URL, "UTF-8"), params, Response.Listener<JSONObject> { response -> println(response) }, Response.ErrorListener { error -> println(error) })
            getNetworkSingletonInstance(context).requestQueue.add(stringRequest)
        }
        else {
            //TODO Implement logic when permission is not granted
        }
    }
}