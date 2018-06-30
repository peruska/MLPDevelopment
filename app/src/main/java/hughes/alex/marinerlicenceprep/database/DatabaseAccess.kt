package hughes.alex.marinerlicenceprep.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseAccess {
    private var openHelper: SQLiteOpenHelper
    private var database : SQLiteDatabase? = null
    companion object {
        private var instance: DatabaseAccess? = null

        fun getInstance(context: Context): DatabaseAccess {
            if(instance==null){
                instance = DatabaseAccess(context)
            }
            return instance as DatabaseAccess
        }
    }

    private constructor(context: Context){
        this.openHelper = DatabaseOpenHelper(context)
    }

    fun open(){
        database = openHelper.writableDatabase
    }

    fun close(){
        if(database!=null){
            database!!.close()
        }
    }

    fun executeRawQuery(queryString: String, parameters: Array<String>): Cursor{
        return database!!.rawQuery(queryString, parameters)
    }

}