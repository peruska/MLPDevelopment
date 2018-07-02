package hughes.alex.marinerlicenceprep

import android.app.Application
import hughes.alex.marinerlicenceprep.entity.UserEntity
import java.util.*


class MyApp : Application() {

    companion object {
        var defaultUser: UserEntity? = null
            get() = field
        var uuid: String = ""
        const val USER_LICENSE_DATA = "user_license_data"
        const val USER_LICENSE_DATA_VALUES = "user_license_data_values"
        const val DL_NUMBER = "dl_number"
        const val CATEGORY = "book_category"
        const val USER_ACCOUNT_PREFERENCES = "user_account"
        const val USER_ACCOUNT_USERNAME = "username"
        const val USER_ACCOUNT_EMAIL = "email"
        const val USER_ACCOUNT_PROFILE_PICTURE_URL = "profile_picture"
        const val BASE_URL = "https://marinerlicenseprep.com/api/"
        const val ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%"
    }

    override fun onCreate() {
        val prefs = this.getSharedPreferences(USER_ACCOUNT_PREFERENCES, 0)
        val username = prefs.getString(USER_ACCOUNT_USERNAME, "")
        val email = prefs.getString(USER_ACCOUNT_EMAIL, "")
        val profilePictureURL = prefs.getString(USER_ACCOUNT_PROFILE_PICTURE_URL, "")
        if (!(username.isNullOrEmpty() || email.isNullOrEmpty() || profilePictureURL.isNullOrEmpty()))
            defaultUser = UserEntity(username, email, profilePictureURL)
        uuid = prefs.getString("uuid", "")
        if(uuid==""){
            val editor = prefs.edit()
            uuid = UUID.randomUUID().toString()
            editor.putString("uuid", uuid)
            editor.apply()
        }
        super.onCreate()
    }

}