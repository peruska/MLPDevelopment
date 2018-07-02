package hughes.alex.marinerlicenceprep.database

import android.content.ContentValues
import android.content.Context
import hughes.alex.marinerlicenceprep.entity.*
import hughes.alex.marinerlicenceprep.models.BooksCategoriesSubcategories
import hughes.alex.marinerlicenceprep.models.CategoryWithSubcategories
import hughes.alex.marinerlicenceprep.models.StudyExpandableListItem
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

    private const val GET_ALL_SUBCATEGORIES_FOR_CERTAIN_BOOK = "SELECT DISTINCT(${Subcategory.TABLE}.${Subcategory.COLUMN_SUBCATEGORY_NAME}), ${Subcategory.TABLE}.${Subcategory.COLUMN_SUBCATEGORY_ID}" +
            " FROM ((${Subcategory.TABLE} INNER JOIN ${CategorySaubcategoryInnerEntity.TABLE}" +
            " ON ${Subcategory.TABLE}.${Subcategory.COLUMN_SUBCATEGORY_ID} = ${CategorySaubcategoryInnerEntity.TABLE}.${CategorySaubcategoryInnerEntity.COLUMN_SUBCATEGORY_ID}) INNER JOIN ${Category.TABLE}" +
            " ON ${CategorySaubcategoryInnerEntity.TABLE}.${CategorySaubcategoryInnerEntity.COLUMN_CATEGORY_ID} = ${Category.TABLE}.${Category.COLUMN_CATEGORY_ID}) WHERE " +
            " ${Category.TABLE}.${Category.COLUMN_BOOK_ID} = ? "


    private const val GET_BOOK_CATEGORY_ID = "SELECT ${BookCategory.COLUMN_BOOK_CATEGORY_ID} FROM ${BookCategory.TABLE} WHERE ${BookCategory.COLUMN_NAME} = ? "

    private const val UPDATE_QUESTION_STATISTICS_FIELDS = "UPDATE ${Questions.TABLE} SET ${Questions.COLUMN_NUMBER_OF_TIMES_ANSWERED} = ?, ${Questions.COLUMN_NUMBER_OF_TIMES_CORRECT} = ?," +
            " ${Questions.COLUMN_NUMBER_OF_TIMES_WRONG} = ?,  WHERE ${Questions.COLUMN_QUESTIONS_ID} = ?"

    private const val GET_QUESTION_STATISTICS_FIELDS = "SELECT ${Questions.COLUMN_NUMBER_OF_TIMES_ANSWERED}, ${Questions.COLUMN_NUMBER_OF_TIMES_CORRECT}, ${Questions.COLUMN_NUMBER_OF_TIMES_WRONG} " +
            " FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_QUESTIONS_ID} = ?"

    private const val GET_QUESTIONS_STATISTICS_FIELDS_FOR_CERTAIN_BOOK = "SELECT ${Questions.COLUMN_NUMBER_OF_TIMES_ANSWERED}, ${Questions.COLUMN_NUMBER_OF_TIMES_CORRECT}, ${Questions.COLUMN_NUMBER_OF_TIMES_WRONG} " +
            " FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_BOOK_ID} = ?"


    //TODO ove dve funkcije se pozivaju u zavisnosti od licence koju je korisnik izabrao
    fun getBooksCategoriesSubcategories(context: Context, bookCategory: Int, licenceNumber: Int): ArrayList<BooksCategoriesSubcategories> {
        val getQuestionsForSubcategoryAndLicence = "SELECT ${Questions.COLUMN_QUESTIONS_ID} FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_SUBCATEGORY_ID} = ? AND ZDL" + licenceNumber + " = 0"

        val list: ArrayList<BooksCategoriesSubcategories> = ArrayList()
        val databaseAccess = DatabaseAccess.getInstance(context)

        databaseAccess.open()
        val cursor = databaseAccess.executeRawQuery(GET_ALL_BOOKS_FROM_CERTAIN_BOOK_CATEGORY, arrayOf(bookCategory.toString()))

        while (cursor.moveToNext()) {

            val bookID = cursor.getString(0)
            val bookName = cursor.getString(1)
            if (bookName == "All Deck") continue
            val cursor2 = databaseAccess.executeRawQuery(GET_ALL_CATEGORIES_FOR_CERTAIN_BOOK, arrayOf(bookID))
            val categoriesWithSubcategories = ArrayList<CategoryWithSubcategories>()
            while (cursor2.moveToNext()) {
                val categoryOfBook = cursor2.getString(0)
                val bookCategoryID = cursor2.getString(1)
                val cursor3 = databaseAccess.executeRawQuery(GET_ALL_SUBCATEGORIES_FOR_CERTAIN_CATEGORY, arrayOf(bookID, bookCategoryID))
                val subcategoriesOfCategory = ArrayList<Subcategory>()
                while (cursor3.moveToNext()) {
                    val subcategoryName = cursor3.getString(0)
                    val subcategoryID = cursor3.getString(1)
                    val cursorCheck = databaseAccess.executeRawQuery(getQuestionsForSubcategoryAndLicence, arrayOf(subcategoryID))
                    if(cursorCheck.count > 0) {
                        subcategoriesOfCategory.add(Subcategory(subcategoryName, subcategoryID))
                    }
                    cursorCheck.close()

                }
                cursor3.close()
                if(subcategoriesOfCategory.size > 0) {
                    categoriesWithSubcategories.add(CategoryWithSubcategories(categoryOfBook, bookCategoryID, subcategoriesOfCategory))
                }
            }
            cursor2.close()
            if(categoriesWithSubcategories.size > 0) {
                list.add(BooksCategoriesSubcategories(bookName, bookID, categoriesWithSubcategories))
            }
        }
        cursor.close()
        databaseAccess.close()
        return list
    }

    fun getBooksWithSubcategories(context: Context, bookCategory: Int, licenceNumber: Int): List<StudyExpandableListItem> {
        val getQuestionsForSubcategoryAndLicence = "SELECT ${Questions.COLUMN_QUESTIONS_ID} FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_SUBCATEGORY_ID} = ? AND ZDL" + licenceNumber + " = 0"

        val listOfGroups = ArrayList<StudyExpandableListItem>()
        val databaseAccess = DatabaseAccess.getInstance(context)

        databaseAccess.open()
        val cursor = databaseAccess.executeRawQuery(GET_ALL_BOOKS_FROM_CERTAIN_BOOK_CATEGORY, arrayOf(bookCategory.toString()))
        while (cursor.moveToNext()) {
            val bookName = cursor.getString(1)
            val bookID = cursor.getString(0)
            if(bookName == "All Engine") continue
            val cursor2 = databaseAccess.executeRawQuery(GET_ALL_SUBCATEGORIES_FOR_CERTAIN_BOOK, arrayOf(bookID))
            val subcategories = ArrayList<Subcategory>()
            while (cursor2.moveToNext()) {
                val subcategoryName = cursor2.getString(0)
                val subcategoryID = cursor2.getString(1)
                val cursorCheck = databaseAccess.executeRawQuery(getQuestionsForSubcategoryAndLicence, arrayOf(subcategoryID))
                if(cursorCheck.count > 0) {
                    subcategories.add(Subcategory(subcategoryName, subcategoryID))
                }
                cursorCheck.close()
            }
            cursor2.close()
            if(subcategories.size > 0) {
                listOfGroups.add(StudyExpandableListItem(bookName, bookID, subcategories))
            }
        }
        cursor.close()
        databaseAccess.close()
        return listOfGroups
    }


    fun loadQuestions(context: Context, bookCategory: String, shuffle: Boolean, subCategoryID: String, dlNumber: Int, bookID: String): ArrayList<Questions> {
        val databaseAccess = DatabaseAccess.getInstance(context)
        databaseAccess.open()
        val loadedQuestions = ArrayList<Questions>()
        val columnBookCategoryID: String
        val cursor1 = if (bookCategory.contains("Engine")) {
            databaseAccess.executeRawQuery(GET_BOOK_CATEGORY_ID, arrayOf("Engine"))
        } else {
            databaseAccess.executeRawQuery(GET_BOOK_CATEGORY_ID, arrayOf("Deck"))
        }
        cursor1.moveToNext()
        columnBookCategoryID = cursor1.getString(0)
        cursor1.close()
        val loadTheQuestions = "SELECT ${Questions.COLUMN_QUESTION}, ${Questions.COLUMN_ANSWER_ONE}, ${Questions.COLUMN_ANSWER_TWO}, ${Questions.COLUMN_ANSWER_THREE}, " +
                " ${Questions.COLUMN_ANSWER_FOUR}, ${Questions.COLUMN_ANSWER}, ${Questions.COLUMN_NUMBER}, " +
                " ${Questions.COLUMN_SUBCATEGORY_NAME}, ${Questions.COLUMN_QUESTIONS_ID} FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_BOOK_CATEGORY_ID} = ? AND ZDL" + dlNumber + " = 0 " +
                " AND ${Questions.COLUMN_BOOK_ID} = ? AND ${Questions.COLUMN_SUBCATEGORY_ID} = ?"
        val cursor = databaseAccess.executeRawQuery(loadTheQuestions, arrayOf(columnBookCategoryID, bookID, subCategoryID))
        while (cursor.moveToNext()) {
            val question = cursor.getString(0)
            val answerOne = cursor.getString(1)
            val answerTwo = cursor.getString(2)
            val answerThree = cursor.getString(3)
            val answerFour = cursor.getString(4)
            val rightAnswer = cursor.getString(5)
            val questionNumber = cursor.getString(6)
            val subcategoryName = cursor.getString(7)
            val questionID = cursor.getString(8)
            loadedQuestions.add(Questions(question, answerOne, answerTwo, answerThree, answerFour, rightAnswer, questionNumber, subcategoryName, questionID))
        }
        cursor.close()
        databaseAccess.close()
        if(shuffle){
            loadedQuestions.shuffle()
        }
        return loadedQuestions
    }

    fun loadQuestions(context: Context, bookCategory: String, shuffle: Boolean, subCategoryID: String, dlNumber: Int, bookID: String, categoryID: String): ArrayList<Questions> {
        val databaseAccess = DatabaseAccess.getInstance(context)
        databaseAccess.open()
        val loadedQuestions = ArrayList<Questions>()
        val columnBookCategoryID: String
        val cursor1 = if (bookCategory.contains("Engine")) {
            databaseAccess.executeRawQuery(GET_BOOK_CATEGORY_ID, arrayOf("Engine"))
        } else {
            databaseAccess.executeRawQuery(GET_BOOK_CATEGORY_ID, arrayOf("Deck"))
        }
        cursor1.moveToNext()
        columnBookCategoryID = cursor1.getString(0)
        cursor1.close()
        val loadTheQuestions = "SELECT ${Questions.COLUMN_QUESTION}, ${Questions.COLUMN_ANSWER_ONE}, ${Questions.COLUMN_ANSWER_TWO}, ${Questions.COLUMN_ANSWER_THREE}, " +
                " ${Questions.COLUMN_ANSWER_FOUR}, ${Questions.COLUMN_ANSWER}, ${Questions.COLUMN_NUMBER}, " +
                " ${Questions.COLUMN_SUBCATEGORY_NAME} FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_BOOK_CATEGORY_ID} = ? AND ZDL" + dlNumber + " = 0 " +
                " AND ${Questions.COLUMN_BOOK_ID} = ? AND ${Questions.COLUMN_SUBCATEGORY_ID} = ? ${Questions.COLUMN_CATEGORY_ID} = ?"
        val cursor = databaseAccess.executeRawQuery(loadTheQuestions, arrayOf(columnBookCategoryID, bookID, subCategoryID, categoryID))
        while (cursor.moveToNext()) {
            val question = cursor.getString(0)
            val answerOne = cursor.getString(1)
            val answerTwo = cursor.getString(2)
            val answerThree = cursor.getString(3)
            val answerFour = cursor.getString(4)
            val rightAnswer = cursor.getString(5)
            val questionNumber = cursor.getString(6)
            val subcategoryName = cursor.getString(7)
            val questionID = cursor.getString(8)
            loadedQuestions.add(Questions(question, answerOne, answerTwo, answerThree, answerFour, rightAnswer, questionNumber, subcategoryName, questionID))
        }
        cursor.close()
        databaseAccess.close()
        if(shuffle){
            loadedQuestions.shuffle()
        }
        return loadedQuestions
    }

    fun updateQuestionStatistics(context: Context, questionID: String, correctOrWrongAnswered: Int){
        val databaseAccess = DatabaseAccess.getInstance(context)
        databaseAccess.open()
        val cursor = databaseAccess.executeRawQuery(GET_QUESTION_STATISTICS_FIELDS, arrayOf(questionID))
        cursor.moveToNext()
        var numberOfTimesAnswered = cursor.getFloat(0)
        var numberOfTimesAnsweredCorrect = cursor.getFloat(1)
        var numberOfTimesAnsweredWrong = cursor.getFloat(2)
        if(correctOrWrongAnswered == 1){
            numberOfTimesAnsweredCorrect += 1
        }else{
            numberOfTimesAnsweredWrong += 1
        }
        numberOfTimesAnswered += 1
        val fieldContainer = ContentValues()
        fieldContainer.put(Questions.COLUMN_NUMBER_OF_TIMES_ANSWERED, numberOfTimesAnswered)
        fieldContainer.put(Questions.COLUMN_NUMBER_OF_TIMES_CORRECT, numberOfTimesAnsweredCorrect)
        fieldContainer.put(Questions.COLUMN_NUMBER_OF_TIMES_WRONG, numberOfTimesAnsweredWrong)
        val whereClause = Questions.COLUMN_QUESTIONS_ID + " = " + questionID
        databaseAccess.updateTable(Questions.TABLE, fieldContainer, whereClause)
        databaseAccess.close()
    }

    fun getStatisticsForBook(context: Context, bookId: String): Float{
        val databaseAccess = DatabaseAccess.getInstance(context)
        databaseAccess.open()
        val cursor = databaseAccess.executeRawQuery(GET_QUESTIONS_STATISTICS_FIELDS_FOR_CERTAIN_BOOK, arrayOf(bookId))
        var numberOfAnsweres : Float = 0.toFloat()
        var numberOfCorrectAnswers: Float = 0.toFloat()
        while (cursor.moveToNext()){
            numberOfAnsweres += cursor.getFloat(0)
            numberOfCorrectAnswers += cursor.getFloat(1)
        }
        return numberOfCorrectAnswers/numberOfAnsweres*100
    }

}