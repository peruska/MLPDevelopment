package hughes.alex.marinerlicenceprep

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hughes.alex.marinerlicenceprep.entity.Book
import hughes.alex.marinerlicenceprep.entity.UserEntity
import hughes.alex.marinerlicenceprep.fragments.StudyFragment
import hughes.alex.marinerlicenceprep.models.BooksCategoriesSubcategories
import hughes.alex.marinerlicenceprep.models.StudyExpandableListItem
import java.util.*
import kotlin.collections.ArrayList


class MyApp : Application() {

    companion object {
        var defaultUser: UserEntity? = null
        var uuid: String = ""
        const val USER_LICENSE_DATA_VALUES = "user_license_data_values"
        const val DL_NUMBER = "dl_number"
        const val CATEGORY = "book_category"
        const val USER_ACCOUNT_PREFERENCES = "user_account"
        const val USER_ACCOUNT_USERNAME = "username"
        const val USER_ACCOUNT_EMAIL = "email"
        const val USER_ACCOUNT_PROFILE_PICTURE_URL = "profile_picture"
        const val BASE_URL = "https://marinerlicenseprep.com/api/"
    }

    override fun onCreate() {
        val prefs = this.getSharedPreferences(USER_ACCOUNT_PREFERENCES, 0)
        val username = prefs.getString(USER_ACCOUNT_USERNAME, "")
        val email = prefs.getString(USER_ACCOUNT_EMAIL, "")
        val profilePictureURL = prefs.getString(USER_ACCOUNT_PROFILE_PICTURE_URL, "")
        defaultUser = UserEntity(username, email, profilePictureURL)
        uuid = prefs.getString("uuid", "")
        if (uuid == "") {
            val editor = prefs.edit()
            uuid = UUID.randomUUID().toString()
            editor.putString("uuid", uuid)
            editor.apply()
        }
        super.onCreate()
    }

    fun getLicenseBooks(): ArrayList<String>{
        val prefs = this.getSharedPreferences(MyApp.USER_LICENSE_DATA_VALUES, 0)
        StudyFragment.dlNumber = prefs.getString(MyApp.DL_NUMBER, "")
        StudyFragment.bookCategoryID = prefs.getString(MyApp.CATEGORY, "")
        val json = prefs.getString(MyApp.USER_LICENSE_DATA_VALUES, "")
        val bookList: ArrayList<String> = ArrayList()
        if (StudyFragment.bookCategoryID == "1") {
            val list = Gson().fromJson<ArrayList<StudyExpandableListItem>>(json, object : TypeToken<ArrayList<StudyExpandableListItem>>() {}.type)
            list.forEach { bookList.add(it.groupName) }
        } else {
            val list = Gson().fromJson<ArrayList<BooksCategoriesSubcategories>>(json, object : TypeToken<ArrayList<BooksCategoriesSubcategories>>() {}.type)
            list.forEach { bookList.add(it.groupName) }
        }
        return bookList

    }
}