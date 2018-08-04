package hughes.alex.marinerlicenceprep

import android.app.Application
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hughes.alex.marinerlicenceprep.entity.UserEntity
import hughes.alex.marinerlicenceprep.fragments.StudyFragment
import hughes.alex.marinerlicenceprep.models.BooksCategoriesSubcategories
import hughes.alex.marinerlicenceprep.models.StudyExpandableListItem
import java.text.SimpleDateFormat
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
        const val USER_ACCOUNT_SUB_TO_DATE = "sub_to_date"
        const val BASE_URL = "https://marinerlicenseprep.com/api/"
        lateinit var dataForTwoLevelList: ArrayList<StudyExpandableListItem>
        lateinit var dataForThreeLevelList: ArrayList<BooksCategoriesSubcategories>

        fun checkIfUserIsSubscribed(): Boolean {
            return SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(defaultUser!!.subscriptionEndDate).after(Date())
        }
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

    fun fetchLicenseBooksAsListItem(){
        val startTime = System.currentTimeMillis()
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
        if (StudyFragment.bookCategoryID == "1")
            dataForTwoLevelList =
                    Gson().fromJson<java.util.ArrayList<StudyExpandableListItem>>(json, object : TypeToken<java.util.ArrayList<StudyExpandableListItem>>() {}.type)
                else
            dataForThreeLevelList = Gson().fromJson<java.util.ArrayList<BooksCategoriesSubcategories>>(json, object : TypeToken<java.util.ArrayList<BooksCategoriesSubcategories>>() {}.type)
        val difference = System.currentTimeMillis() - startTime
        println(difference)
    }
}