package hughes.alex.marinerlicenceprep

import android.content.Context
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper


public class MyDatabase(context: Context) : SQLiteAssetHelper(context, DATABASE_NAME, null,  DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "yogalite.db";
        const val DATABASE_VERSION = 1;
        const val ID="id";
        const val NAME="name";
        const val DESCRIPTION="description";
        const val IMAGE="image";
        const val BENEFITS="benefits";
        const val POSES_TABLE="poses";
    }
}