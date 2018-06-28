package hughes.alex.marinerlicenceprep

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.android.volley.NetworkResponse
import com.android.volley.Request.Method.POST
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import hughes.alex.marinerlicenceprep.MyApp.Companion.BASE_URL
import hughes.alex.marinerlicenceprep.MyApp.Companion.defaultUser
import hughes.alex.marinerlicenceprep.activities.Home
import hughes.alex.marinerlicenceprep.NetworkSingleton.Companion.getNetworkSingletonInstance
import hughes.alex.marinerlicenceprep.entity.UserEntity
import org.json.JSONObject
import uk.me.hardill.volley.multipart.MultipartRequest
import java.io.ByteArrayOutputStream
import java.util.*


class AuthService(var context: Context) {

    companion object {
        const val URL = MyApp.BASE_URL + "signin"
    }

    fun signIn(email: String, password: String) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            val signInRequest = object : StringRequest(POST, URL,
                    Response.Listener { response -> Toast.makeText(context, response, Toast.LENGTH_LONG).show()
                        val dataFromResponse = JSONObject(JSONObject( response).getString("data"))
                        defaultUser = UserEntity("peruska", "peruskatestira@gmail.com", dataFromResponse.getString("url"))
                        context.startActivity(Intent(context, Home::class.java))
                    },
                    Response.ErrorListener { error -> Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show() }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = email
                    params["password"] = password
                    return params
                }
            }
            getNetworkSingletonInstance(context).requestQueue.add(signInRequest)
        }
        else {
            //TODO Implement logic when permission is not granted
        }
    }

    fun signUp(context: Context, profilePictureBitmap: Bitmap, email: String, username: String, password: String){
        val url = BASE_URL + "signup"
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            val signInRequest = object : StringRequest(POST, url,
                    Response.Listener {
                        response -> Toast.makeText(context, response, Toast.LENGTH_LONG).show()
                        println(JSONObject(response))
                        uploadPhoto(context, profilePictureBitmap, email, username)
                    },
                    Response.ErrorListener { error -> Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show() }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["username"] = username
                    params["email"] = email
                    params["password"] = password
                    return params
                }
            }
            getNetworkSingletonInstance(context).requestQueue.add(signInRequest)
        }
        else {
            //TODO Implement logic when permission is not granted
        }
    }

    fun uploadPhoto(context: Context, profilePictureBitmap: Bitmap, email: String, username: String){
        val pictureUrl = MyApp.BASE_URL + "Upload_Avatar"
        val request = MultipartRequest(pictureUrl, null,
                Response.Listener<NetworkResponse> {
                    response ->
                    val responseInJson = JSONObject(String(response.data))
                    if(responseInJson.getBoolean("success")) {

                        val prefs = context.getSharedPreferences(MyApp.USER_ACCOUNT_PREFERENCES, 0)
                        val editor = prefs.edit()
                        editor.putString(MyApp.USER_ACCOUNT_USERNAME, username)
                        editor.putString(MyApp.USER_ACCOUNT_EMAIL, email)
                        editor.putString(MyApp.USER_ACCOUNT_PROFILE_PICTURE_URL, "")
                    }
                },
                Response.ErrorListener { error ->  println(error)})

        val stream = ByteArrayOutputStream()
        profilePictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        request.addPart(MultipartRequest.FormPart("email", email))
        request.addPart(MultipartRequest.FilePart(
                "data",
                "image/jpg",
                "${username}_${MyApp.uuid}.jpg",
                byteArray))
        getNetworkSingletonInstance(context).addToRequestQueue(request)
    }

    fun resetPassword(email : String){
        val url = BASE_URL + "Forgot_Password_API"
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            val signInRequest = object : StringRequest(POST, url,
                    Response.Listener {
                        response -> Toast.makeText(context, response, Toast.LENGTH_LONG).show()
                        println(JSONObject(response))
                    },
                    Response.ErrorListener { error -> Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show() }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = email
                    return params
                }
            }
            getNetworkSingletonInstance(context).requestQueue.add(signInRequest)
        }
        else {
            //TODO Implement logic when permission is not granted
        }
    }

    fun changeEmail(newEmail: String){
        val url = BASE_URL + "Change_Email"
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            val signInRequest = object : StringRequest(POST, url,
                    Response.Listener {
                        response -> Toast.makeText(context, response, Toast.LENGTH_LONG).show()
                        println(JSONObject(response))
                    },
                    Response.ErrorListener { error -> Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show() }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["old_email"] = "peruskatestira@gmail.com"//"defaultUser!!.email
                    params["new_email"] = newEmail
                    return params
                }
            }
            getNetworkSingletonInstance(context).requestQueue.add(signInRequest)
        }
        else {
            //TODO Implement logic when permission is not granted
        }
    }

    fun retrieveUserInfo(){
        val url = BASE_URL + "Get_User_Info"
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            val signInRequest = object : StringRequest(POST, url,
                    Response.Listener {
                        response -> Toast.makeText(context, response, Toast.LENGTH_LONG).show()
                        println(JSONObject(response))
                    },
                    Response.ErrorListener { error -> Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show() }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = "peruskatestira@gmail.com"//defaultUser!!.email
                    return params
                }
            }
            getNetworkSingletonInstance(context).requestQueue.add(signInRequest)
        }
        else {
            //TODO Implement logic when permission is not granted
        }
    }

    fun retrieveProfilePicture(){
        val url = defaultUser!!.profileImageURL
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            val signInRequest = object : StringRequest(POST, url,
                    Response.Listener {
                        response -> Toast.makeText(context, response, Toast.LENGTH_LONG).show()
                        println(JSONObject(response))
                    },
                    Response.ErrorListener { error -> Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show() }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = "peruskatestira@gmail.com"//defaultUser!!.email
                    return params
                }
            }
            getNetworkSingletonInstance(context).requestQueue.add(signInRequest)
        }
        else {
            //TODO Implement logic when permission is not granted
        }
    }
}