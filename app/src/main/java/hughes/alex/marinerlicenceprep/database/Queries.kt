package hughes.alex.marinerlicenceprep.database

import android.content.Context
import hughes.alex.marinerlicenceprep.entity.*
import hughes.alex.marinerlicenceprep.models.StudyExpandableListItem
import java.util.*
import kotlin.collections.ArrayList

object Queries {


    private const val GET_ALL_BOOKS_FROM_CERTAIN_BOOK_CATEGORY = "SELECT ${Book.COLUMN_BOOK_ID}, ${Book.COLUMN_BOOKNAME} " +
            " FROM ${Book.TABLE} WHERE ${Book.COLUMN_PARENT_CATEGORY} = ?"

    private const val GET_ALL_SUBCATEGORIES_FOR_CERTAIN_BOOK = "SELECT DISTINCT(${Subcategory.TABLE}.${Subcategory.COLUMN_SUBCATEGORY_NAME}), ${Subcategory.TABLE}.${Subcategory.COLUMN_SUBCATEGORY_ID}" +
            " FROM ((${Subcategory.TABLE} INNER JOIN ${CategorySaubcategoryInnerEntity.TABLE}" +
            " ON ${Subcategory.TABLE}.${Subcategory.COLUMN_SUBCATEGORY_ID} = ${CategorySaubcategoryInnerEntity.TABLE}.${CategorySaubcategoryInnerEntity.COLUMN_SUBCATEGORY_ID}) INNER JOIN ${Category.TABLE}" +
            " ON ${CategorySaubcategoryInnerEntity.TABLE}.${CategorySaubcategoryInnerEntity.COLUMN_CATEGORY_ID} = ${Category.TABLE}.${Category.COLUMN_CATEGORY_ID}) WHERE " +
            " ${Category.TABLE}.${Category.COLUMN_BOOK_ID} = ? "

    //FUNCTION TO LOAD THE RATINGS
    private const val LOAD_RATINGS_DECK = "SELECT ${License.COLUMN_DISPLAY_ORDER_NUMBER} from ${License.TABLE} INNER JOIN ${BookCategory.TABLE} ON ${License.TABLE}.${License.COLUMN_BOOK_CATEGORY_ID} = ${BookCategory.TABLE}.${BookCategory.COLUMN_BOOK_CATEGORY_ID}" +
            " WHERE ${BookCategory.TABLE}.${BookCategory.COLUMN_NAME} = 'Deck' ORDER BY ${License.COLUMN_DISPLAY_ORDER_NUMBER} "
    private const val LOAD_RATINGS_ENGINE = "SELECT ${License.COLUMN_DISPLAY_ORDER_NUMBER} from ${License.TABLE} INNER JOIN ${BookCategory.TABLE} ON ${License.TABLE}.${License.COLUMN_BOOK_CATEGORY_ID} = ${BookCategory.TABLE}.${BookCategory.COLUMN_BOOK_CATEGORY_ID}" +
            " WHERE ${BookCategory.TABLE}.${BookCategory.COLUMN_NAME} = 'Engine' ORDER BY ${License.COLUMN_DISPLAY_ORDER_NUMBER} "

    //FUNCTION TO LOAD THE TESTS
    private const val LOAD_THE_TESTS = "SELECT "


    fun getBooksWithSubcategories(context: Context, bookCategory: Int): List<StudyExpandableListItem> {
        val list: ArrayList<StudyExpandableListItem> = ArrayList()
        val databaseAccess = DatabaseAccess.getInstance(context)

        databaseAccess.open()
        val cursor = databaseAccess.executeRawQuery(GET_ALL_BOOKS_FROM_CERTAIN_BOOK_CATEGORY, arrayOf(bookCategory.toString()))

        while (cursor.moveToNext()){
            val bookID = cursor.getString(0)
            val bookName = cursor.getString(1)
            val cursor2 =databaseAccess.executeRawQuery(GET_ALL_SUBCATEGORIES_FOR_CERTAIN_BOOK, arrayOf(bookID))
            val subcategories = ArrayList<String>()
            while (cursor2.moveToNext()){
                subcategories.add(cursor2.getString(0))
            }
            Collections.sort(subcategories, String.CASE_INSENSITIVE_ORDER)
            list.add(StudyExpandableListItem(bookName, subcategories))
        }
        databaseAccess.close()
        return list.sortedWith(compareBy({ it.groupName }))
    }
}