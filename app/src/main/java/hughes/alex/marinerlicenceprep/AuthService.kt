package hughes.alex.marinerlicenceprep

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat
import com.android.volley.NetworkResponse
import com.android.volley.Request.Method.POST
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import hughes.alex.marinerlicenceprep.MyApp.Companion.BASE_URL
import hughes.alex.marinerlicenceprep.MyApp.Companion.USER_LICENSE_DATA_VALUES
import hughes.alex.marinerlicenceprep.MyApp.Companion.checkIfUserIsSubscribed
import hughes.alex.marinerlicenceprep.MyApp.Companion.defaultUser
import hughes.alex.marinerlicenceprep.NetworkSingleton.Companion.getNetworkSingletonInstance
import hughes.alex.marinerlicenceprep.activities.EditSubscriptionProfile
import hughes.alex.marinerlicenceprep.activities.Home
import hughes.alex.marinerlicenceprep.activities.License
import hughes.alex.marinerlicenceprep.activities.LoginActivity
import hughes.alex.marinerlicenceprep.entity.UserEntity
import org.jetbrains.anko.alert
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import org.json.JSONObject
import uk.me.hardill.volley.multipart.MultipartRequest
import java.io.ByteArrayOutputStream
import java.util.*


class AuthService(var context: Context) {
    fun saveUserPrefs(username: String, email: String, url: String, subToDate: String, subType: String) {
        val prefs = context.getSharedPreferences(MyApp.USER_ACCOUNT_PREFERENCES, 0)
        val editor = prefs.edit()
        editor.putString(MyApp.USER_ACCOUNT_USERNAME, username)
        editor.putString(MyApp.USER_ACCOUNT_EMAIL, email)
        editor.putString(MyApp.USER_ACCOUNT_PROFILE_PICTURE_URL, url)
        editor.putString(MyApp.USER_ACCOUNT_SUB_TO_DATE, subToDate)
        editor.putString(MyApp.USER_ACCOUNT_SUB_TYPE, subType)
        editor.commit()
    }

    fun signIn(email: String, password: String) {
        val waitDialog = context.indeterminateProgressDialog("Please wait", "Logging in")
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            val url = MyApp.BASE_URL + "signin"
            val signInRequest = object : StringRequest(POST, url,
                    Response.Listener { response ->

                        val responseAsJson = JSONObject(response)

                        if (responseAsJson.getBoolean("success")) {
                            val dataFromResponse = JSONObject(responseAsJson.getString("data"))
                            val user = dataFromResponse.getString("username")
                            val pictureUrl = dataFromResponse.getString("url")
                            val subToDate =
                                    try {
                                        dataFromResponse.getString("sub_to_date")
                                    } catch (e: Exception) {
                                        Date().time.toString()
                                    }
                            val subType =
                                    try {
                                        dataFromResponse.getString("subscription_package")
                                    } catch (e: Exception) {
                                        "NONE"
                                    }
                            defaultUser = UserEntity(user, email, pictureUrl, subToDate, subType)
                            saveUserPrefs(user, email, pictureUrl, subToDate, subType)
                            val prefs = context.getSharedPreferences(MyApp.USER_LICENSE_DATA_VALUES, 0)
                            if (prefs.getString(USER_LICENSE_DATA_VALUES, "").isBlank())
                                context.startActivity(Intent(context, License::class.java))
                            else
                                context.startActivity(Intent(context, Home::class.java))
                            (context as LoginActivity).finish()
                        } else context.toast(responseAsJson.getString("msg"))

                        waitDialog.dismiss()
                    },
                    Response.ErrorListener {
                        context.toast("Unsuccessful login attempt. Please check your internet connection.")
                        waitDialog.dismiss()
                    }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = email
                    params["password"] = password
                    return params
                }
            }
            getNetworkSingletonInstance(context).requestQueue.add(signInRequest)
        } else {
            //TODO Implement logic when permission is not granted
        }
    }

    fun signUp(context: Context, profilePictureBitmap: Bitmap, email: String, username: String, password: String) {
        val url = BASE_URL + "signup"
        val waitDialog = context.indeterminateProgressDialog("Please wait", "Creating account")
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            val signInRequest = object : StringRequest(POST, url,
                    Response.Listener { response ->
                        val responseAsJson = JSONObject(response)
                        waitDialog.dismiss()
                        if (responseAsJson.getBoolean("success"))
                            uploadPhoto(context, profilePictureBitmap, email, username)
                        else context.toast(responseAsJson.getString("msg"))
                    },
                    Response.ErrorListener {
                        waitDialog.dismiss()
                        context.toast("Unsuccessful sign up attempt. Please check your internet connection.")
                    }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["username"] = username
                    params["email"] = email
                    params["password"] = password
                    return params
                }
            }
            getNetworkSingletonInstance(context).requestQueue.add(signInRequest)
        } else {
            //TODO Implement logic when permission is not granted
        }
    }

    fun uploadPhoto(context: Context, profilePictureBitmap: Bitmap, email: String, username: String) {
        val url = MyApp.BASE_URL + "Upload_Avatar"
        val pictureUrl = "${username}_${MyApp.uuid}.jpg"
        val request = MultipartRequest(url, null,
                Response.Listener<NetworkResponse> { response ->
                    val responseAsJson = JSONObject(String(response.data))
                    if (responseAsJson.getBoolean("success")) {
                        val subToDate =
                                try {
                                    responseAsJson.getString("sub_to_date")
                                } catch (e: Exception) {
                                    Date().time.toString()
                                }
                        val subType =
                                try {
                                    responseAsJson.getString("subscription_package")
                                } catch (e: Exception) {
                                    "NONE"
                                }
                        defaultUser = UserEntity(username, email, pictureUrl, subToDate, subType)
                        saveUserPrefs(username, email, pictureUrl, subToDate, subType)
                        //If the upload occurred on login activity show select rating screen
                        (context as? LoginActivity)?.startActivity(Intent(context, License::class.java))
                        (context as? LoginActivity)?.finish()
                        (context as? EditSubscriptionProfile)?.toast("Profile picture successfully changed")
                    } else context.toast(responseAsJson.getString("msg"))
                },
                Response.ErrorListener { error -> println(error) })

        val stream = ByteArrayOutputStream()
        profilePictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        request.addPart(MultipartRequest.FormPart("email", email))
        request.addPart(MultipartRequest.FilePart(
                "data",
                "image/jpg",
                pictureUrl,
                byteArray))
        getNetworkSingletonInstance(context).addToRequestQueue(request)
    }

    fun resetPassword(email: String) {
        val url = BASE_URL + "Forgot_Password_API"
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            val signInRequest = object : StringRequest(POST, url,
                    Response.Listener { response ->
                        val responseAsJson = JSONObject(response)
                        if (responseAsJson.getBoolean("success"))
                            context.alert {
                                title = "Successful password reset, please check your email."
                                okButton { }
                            }.show()
                        else
                            context.alert {
                                title = responseAsJson.getString("msg")
                                okButton { }
                            }.show()

                    },
                    Response.ErrorListener { error ->
                        context.toast("Unsuccessful password reset attempt. Please check your internet connection.")
                    }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = email
                    return params
                }
            }
            getNetworkSingletonInstance(context).requestQueue.add(signInRequest)
        } else {
            //TODO Implement logic when permission is not granted
        }
    }

    fun changeEmail(newEmail: String) {
        val url = BASE_URL + "Change_Email"
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            val signInRequest = object : StringRequest(POST, url,
                    Response.Listener { response ->
                        val responseAsJson = JSONObject(response)
                        if (responseAsJson.getBoolean("success")) {
                            val editor = context.getSharedPreferences(MyApp.USER_ACCOUNT_PREFERENCES, 0).edit()
                            editor.putString(MyApp.USER_ACCOUNT_EMAIL, newEmail)
                            editor.apply()
                            defaultUser?.email = newEmail
                            context.alert {
                                title = "You have successfully changed your email!"
                                okButton { }
                            }.show()
                        } else
                            context.alert {
                                title = responseAsJson.getString("msg")
                                okButton { }
                            }.show()
                    },
                    Response.ErrorListener { error ->
                        context.toast("Unsuccessful email change attempt. Please check your internet connection.")
                    }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["old_email"] = defaultUser!!.email
                    params["new_email"] = newEmail
                    return params
                }
            }
            getNetworkSingletonInstance(context).requestQueue.add(signInRequest)
        } else {
            //TODO Implement logic when permission is not granted
        }
    }


    fun changePassword(oldPassword: String, newPassword: String) {
        val url = BASE_URL + "Change_Password"
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            val signInRequest = object : StringRequest(POST, url,
                    Response.Listener { response ->
                        val responseAsJson = JSONObject(response)
                        if (responseAsJson.getBoolean("success")) {
                            context.alert {
                                title = "Password successfully changed"
                                okButton { }
                            }.show()
                        } else
                            context.alert {
                                title = responseAsJson.getString("msg")
                                okButton { }
                            }.show()
                    },
                    Response.ErrorListener { error ->
                        context.toast("Unsuccessful password change attempt. Please check your internet connection.")
                    }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = defaultUser!!.email
                    params["old_password"] = oldPassword
                    params["new_password"] = newPassword
                    return params
                }
            }
            getNetworkSingletonInstance(context).requestQueue.add(signInRequest)
        } else {
            //TODO Implement logic when permission is not granted
        }
    }

    fun retrieveUserInfo() {
        val url = BASE_URL + "Get_User_Info"
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            val signInRequest = object : StringRequest(POST, url,
                    Response.Listener { response ->
                        println(response)
                        val responseAsJson = JSONObject(response)

                        if (responseAsJson.getBoolean("success")) {
                            val dataFromResponse = JSONObject(responseAsJson.getString("data"))
                            val user = dataFromResponse.getString("username")
                            val pictureUrl = dataFromResponse.getString("url")
                            val subToDate =
                                    try {
                                        dataFromResponse.getString("sub_to_date")
                                    } catch (e: Exception) {
                                        Date().time.toString()
                                    }
                            val subType =
                                    try {
                                        dataFromResponse.getString("subscription_package")
                                    } catch (e: Exception) {
                                        "None"
                                    }
                            defaultUser = UserEntity(user, defaultUser!!.email, pictureUrl, subToDate, subType)
                            saveUserPrefs(user, defaultUser!!.email, pictureUrl, subToDate, subType)
                            if (!checkIfUserIsSubscribed())
                                context.alert {
                                    positiveButton("Upgrage Account") {
                                        title = "Upgrade Account"
                                        context.startActivity(Intent(context, EditSubscriptionProfile::class.java))
                                    }
                                    negativeButton("Not Now") {}
                                    title = "Upgrade Account?"
                                    message = "You are not a premium user! The functionality of this app will be heavily limited" +
                                            " - only twenty questions per category. Upgrade your account to gain full functionality" +
                                            " of this App and MarinerLicensePrep.com"
                                }.show()
                        }
                        println(JSONObject(response))
                    },
                    Response.ErrorListener { error -> }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = defaultUser!!.email
                    return params
                }
            }
            getNetworkSingletonInstance(context).requestQueue.add(signInRequest)
        } else {
            //TODO Implement logic when permission is not granted
        }
    }

    fun changeRating(book: String, rating: String) {
        val url = BASE_URL + "Change_Rating"
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            val signInRequest = object : StringRequest(POST, url,
                    Response.Listener { response ->
                        val responseAsJson = JSONObject(response)
                        if (responseAsJson.getBoolean("success"))
                            context.toast("Rating saved!")
                    },
                    Response.ErrorListener { error ->
                        context.toast("Failed to save rating, no internet connectivity")
                    }) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["email"] = defaultUser!!.email
                    params["book"] = book
                    params["rating"] = rating
                    return params
                }
            }
            getNetworkSingletonInstance(context).requestQueue.add(signInRequest)
        } else {
            //TODO Implement logic when permission is not granted
        }
    }

    fun logOut() {
        saveUserPrefs("", "", "", "", "")
        defaultUser = UserEntity("", "", "", "", "NONE")
    }
}