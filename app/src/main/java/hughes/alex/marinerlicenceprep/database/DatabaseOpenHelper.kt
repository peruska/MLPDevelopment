package hughes.alex.marinerlicenceprep.database

import android.content.Context
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

class DatabaseOpenHelper (context: Context) : SQLiteAssetHelper (context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "DataModel_v2.sqlite"
        const val DATABASE_VERSION = 1
    }

}