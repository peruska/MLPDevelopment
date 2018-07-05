package hughes.alex.marinerlicenceprep

import android.Manifest
import android.app.Application
import android.support.v4.app.ActivityCompat
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
        const val RESUME_DATA = "resume_data"
        const val DL_NUMBER = "dl_number"
        const val CATEGORY = "book_category"
        const val USER_ACCOUNT_PREFERENCES = "user_account"
        const val USER_ACCOUNT_USERNAME = "username"
        const val USER_ACCOUNT_EMAIL = "email"
        const val USER_ACCOUNT_PROFILE_PICTURE_URL = "profile_picture"
        const val BASE_URL = "https://marinerlicenseprep.com/api/"
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