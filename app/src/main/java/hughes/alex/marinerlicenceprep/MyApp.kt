package hughes.alex.marinerlicenceprep

import android.app.Application
import hughes.alex.marinerlicenceprep.entity.UserEntity


class MyApp : Application() {

    companion object {
        var defaultUser: UserEntity? = null
        const val USER_ACCOUNT_PREFERENCES = "user_account"
        const val USER_ACCOUNT_USERNAME = "email"
        const val USER_ACCOUNT_EMAIL = "email"
        const val USER_ACCOUNT_PROFILE_PICTURE_URL = "email"
        const val BASE_URL = "https://marinerlicenseprep.com/api/"

    }

    override fun onCreate() {
        val prefs = this.getSharedPreferences(USER_ACCOUNT_PREFERENCES, 0)
        val username = prefs.getString(USER_ACCOUNT_USERNAME, "")
        val email = prefs.getString(USER_ACCOUNT_EMAIL, "")
        val profilePictureURL = prefs.getString(USER_ACCOUNT_PROFILE_PICTURE_URL, "")
        if (!(username.isNullOrEmpty() || email.isNullOrEmpty() || profilePictureURL.isNullOrEmpty()))
            defaultUser = UserEntity(username, email, profilePictureURL)
        super.onCreate()
    }


}