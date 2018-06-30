package hughes.alex.marinerlicenceprep.database

import android.content.Context
import hughes.alex.marinerlicenceprep.entity.*
import hughes.alex.marinerlicenceprep.models.CategoryWithSubcategories
import hughes.alex.marinerlicenceprep.models.StudyExpandableListItem
import java.util.*
import kotlin.collections.ArrayList

object Queries {


    private const val GET_ALL_BOOKS_FROM_CERTAIN_BOOK_CATEGORY = "SELECT ${Book.COLUMN_BOOK_ID}, ${Book.COLUMN_BOOKNAME} " +
            " FROM ${Book.TABLE} WHERE ${Book.COLUMN_PARENT_CATEGORY} = ?"

    private const val GET_ALL_CATEGORIES_FOR_CERTAIN_BOOK = "SELECT ${Category.COLUMN_CATEGORY_NAME}, ${Category.COLUMN_CATEGORY_ID} FROM ${Category.TABLE} WHERE ${Category.COLUMN_BOOK_ID} = ?"

    private const val GET_ALL_SUBCATEGORIES_FOR_CERTAIN_CATEGORY = "SELECT DISTINCT(${Subcategory.TABLE}.${Subcategory.COLUMN_SUBCATEGORY_NAME}), ${Subcategory.TABLE}.${Subcategory.COLUMN_SUBCATEGORY_ID}" +
            " FROM ((${Subcategory.TABLE} INNER JOIN ${CategorySaubcategoryInnerEntity.TABLE}" +
            " ON ${Subcategory.TABLE}.${Subcategory.COLUMN_SUBCATEGORY_ID} = ${CategorySaubcategoryInnerEntity.TABLE}.${CategorySaubcategoryInnerEntity.COLUMN_SUBCATEGORY_ID}) INNER JOIN ${Category.TABLE}" +
            " ON ${CategorySaubcategoryInnerEntity.TABLE}.${CategorySaubcategoryInnerEntity.COLUMN_CATEGORY_ID} = ${Category.TABLE}.${Category.COLUMN_CATEGORY_ID}) WHERE " +
            " ${Category.TABLE}.${Category.COLUMN_BOOK_ID} = ? AND ${Category.TABLE}.${Category.COLUMN_CATEGORY_ID} = ? "

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
            if(bookName == "All Engine") continue
            val cursor2 =databaseAccess.executeRawQuery(GET_ALL_CATEGORIES_FOR_CERTAIN_BOOK, arrayOf(bookID))
            val categoriesWithSubcategories = ArrayList<CategoryWithSubcategories>()
            while (cursor2.moveToNext()){
                val categoryOfBook = cursor2.getString(0)
                val bookCategoryID = cursor2.getString(1)
                val cursor3 = databaseAccess.executeRawQuery(GET_ALL_SUBCATEGORIES_FOR_CERTAIN_CATEGORY, arrayOf(bookID, bookCategoryID))
                val subcategoriesOfCategory = ArrayList<String>()
                while (cursor3.moveToNext()) {
                    val subcategory = cursor3.getString(0)
                    subcategoriesOfCategory.add(subcategory)
                }
                Collections.sort(subcategoriesOfCategory, String.CASE_INSENSITIVE_ORDER)
                categoriesWithSubcategories.add(CategoryWithSubcategories(categoryOfBook, subcategoriesOfCategory))
            }
            list.add(StudyExpandableListItem(bookName, categoriesWithSubcategories))
        }
        databaseAccess.close()
        return list.sortedWith(compareBy({ it.groupName }))
    }


    fun loadQuestions(context: Context, parentSection: String, shufle: Boolean, subCategory : Int, dlNumber: Int): ArrayList<Questions>{
        val databaseAccess = DatabaseAccess.getInstance(context)
        val LOAD_THE_QUESTIONS = "SELECT ${Questions.COLUMN_QUESTION}, ${Questions.COLUMN_ANSWER_ONE}, ${Questions.COLUMN_ANSWER_TWO}, ${Questions.COLUMN_ANSWER_THREE}, ${Questions.COLUMN_ANSWER_FOUR}, ${Questions.COLUMN_ANSWER}, ${Questions.COLUMN_NUMBER}, " +
                " ${Questions.COLUMN_SUBCATEGORY_NAME} FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_BOOK_CATEGORY_ID} = ? AND ZDL" + dlNumber + " = 0 "
        databaseAccess.open()
        val loadedQuestions = ArrayList<Questions>()
        if(parentSection.contains("All")){
            val cursor = databaseAccess.executeRawQuery(LOAD_THE_QUESTIONS, arrayOf(1.toString()))
            while (cursor.moveToNext()){
                val question = cursor.getString(0)
                val answerOne = cursor.getString(1)
                val answerTwo = cursor.getString(2)
                val answerThree = cursor.getString(3)
                val answerFour = cursor.getString(4)
                val rightAnswer = cursor.getString(5)
                val questionNumber = cursor.getString(6)
                val subcategoryName = cursor.getString(7)
                loadedQuestions.add(Questions(question, answerOne, answerTwo, answerThree, answerFour, rightAnswer, questionNumber, subcategoryName))
            }
        }else{
            //TODO Deck deo
        }
        databaseAccess.close()
        return loadedQuestions
    }
}