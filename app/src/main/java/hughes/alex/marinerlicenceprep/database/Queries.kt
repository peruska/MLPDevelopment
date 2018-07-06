package hughes.alex.marinerlicenceprep.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.entity.*
import hughes.alex.marinerlicenceprep.models.BooksCategoriesSubcategories
import hughes.alex.marinerlicenceprep.models.BooksWithScores
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

    private const val GET_ALL_LICENCE = "SELECT ${LicenseEntity.COLUMN_LICENSE_ID}, ${LicenseEntity.COLUMN_DL_NUMBER}, ${LicenseEntity.COLUMN_BOOK_CATEGORY_ID}, ${LicenseEntity.COLUMN_ENDORSEMENT}, ${LicenseEntity.COLUMN_ROUTE}," +
            " ${LicenseEntity.COLUMN_TONNAGE_GROUP} FROM ${LicenseEntity.TABLE}"
    private const val GET_QUESTION_STATISTICS_FIELDS = "SELECT ${Questions.COLUMN_NUMBER_OF_TIMES_ANSWERED}, ${Questions.COLUMN_NUMBER_OF_TIMES_CORRECT}, ${Questions.COLUMN_NUMBER_OF_TIMES_WRONG} " +
            " FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_QUESTIONS_ID} = ?"

    private const val GET_QUESTIONS_STATISTICS_FIELDS_FOR_CERTAIN_BOOK = "SELECT ${Questions.COLUMN_NUMBER_OF_TIMES_ANSWERED}, ${Questions.COLUMN_NUMBER_OF_TIMES_CORRECT}, ${Questions.COLUMN_NUMBER_OF_TIMES_WRONG} " +
            " FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_BOOK_ID} = ?"


    fun getBooksCategoriesSubcategories(context: Context, bookCategory: Int, licenceNumber: Int): ArrayList<BooksCategoriesSubcategories> {
        val getQuestionsForSubcategoryAndLicence = "SELECT ${Questions.COLUMN_QUESTIONS_ID} FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_SUBCATEGORY_ID} = ? AND ZDL" + licenceNumber + " = 1"

        val list: ArrayList<BooksCategoriesSubcategories> = ArrayList()
        val databaseAccess = DatabaseAccess.getInstance(context)

        databaseAccess.open()
        val cursor = databaseAccess.executeRawQuery(GET_ALL_BOOKS_FROM_CERTAIN_BOOK_CATEGORY, arrayOf(bookCategory.toString()))

        while (cursor.moveToNext()) {

            val bookID = cursor.getString(0)
            val bookName = cursor.getString(1)
            if (bookName == "All Deck") {
                list.add(BooksCategoriesSubcategories(bookName, bookID, ArrayList<CategoryWithSubcategories>()))
                continue
            }
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
                    if (cursorCheck.count > 0) {
                        subcategoriesOfCategory.add(Subcategory(subcategoryName, subcategoryID))
                    }
                    cursorCheck.close()

                }
                cursor3.close()
                if (subcategoriesOfCategory.size > 0) {
                    categoriesWithSubcategories.add(CategoryWithSubcategories(categoryOfBook, bookCategoryID, subcategoriesOfCategory))
                }
            }
            cursor2.close()
            if (categoriesWithSubcategories.size > 0) {
                list.add(BooksCategoriesSubcategories(bookName, bookID, categoriesWithSubcategories))
            }
        }
        cursor.close()
        databaseAccess.close()
        return list
    }

    fun getBooksWithSubcategories(context: Context, bookCategory: Int, licenceNumber: Int): List<StudyExpandableListItem> {
        val getQuestionsForSubcategoryAndLicence = "SELECT ${Questions.COLUMN_QUESTIONS_ID} FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_SUBCATEGORY_ID} = ? AND ZDL" + licenceNumber + " = 1"

        val listOfGroups = ArrayList<StudyExpandableListItem>()
        val databaseAccess = DatabaseAccess.getInstance(context)

        databaseAccess.open()
        val cursor = databaseAccess.executeRawQuery(GET_ALL_BOOKS_FROM_CERTAIN_BOOK_CATEGORY, arrayOf(bookCategory.toString()))
        while (cursor.moveToNext()) {
            val bookName = cursor.getString(1)
            val bookID = cursor.getString(0)
            if (bookName == "All Engine") {
                listOfGroups.add(StudyExpandableListItem(bookName, bookID, ArrayList()))
                continue
            }
            val cursor2 = databaseAccess.executeRawQuery(GET_ALL_SUBCATEGORIES_FOR_CERTAIN_BOOK, arrayOf(bookID))
            val subcategories = ArrayList<Subcategory>()
            while (cursor2.moveToNext()) {
                val subcategoryName = cursor2.getString(0)
                val subcategoryID = cursor2.getString(1)
                val cursorCheck = databaseAccess.executeRawQuery(getQuestionsForSubcategoryAndLicence, arrayOf(subcategoryID))
                if (cursorCheck.count > 0) {
                    subcategories.add(Subcategory(subcategoryName, subcategoryID))
                }
                cursorCheck.close()
            }
            cursor2.close()
            if (subcategories.size > 0) {
                listOfGroups.add(StudyExpandableListItem(bookName, bookID, subcategories))
            }
        }
        cursor.close()
        databaseAccess.close()
        return listOfGroups
    }

    fun updateQuestionStatistics(context: Context, questionID: String, correctOrWrongAnswered: Int) {
        val databaseAccess = DatabaseAccess.getInstance(context)
        databaseAccess.open()
        val cursor = databaseAccess.executeRawQuery(GET_QUESTION_STATISTICS_FIELDS, arrayOf(questionID))
        cursor.moveToNext()
        var numberOfTimesAnswered = cursor.getFloat(0)
        var numberOfTimesAnsweredCorrect = cursor.getFloat(1)
        var numberOfTimesAnsweredWrong = cursor.getFloat(2)
        if (correctOrWrongAnswered == 1) {
            numberOfTimesAnsweredCorrect += 1
        } else {
            numberOfTimesAnsweredWrong += 1
        }
        numberOfTimesAnswered += 1
        val fieldContainer = ContentValues()
        fieldContainer.put(Questions.COLUMN_NUMBER_OF_TIMES_ANSWERED, numberOfTimesAnswered)
        fieldContainer.put(Questions.COLUMN_NUMBER_OF_TIMES_CORRECT, numberOfTimesAnsweredCorrect)
        fieldContainer.put(Questions.COLUMN_NUMBER_OF_TIMES_WRONG, numberOfTimesAnsweredWrong)
        fieldContainer.put(Questions.COLUMN_HAS_BEEN_ANSWERED, "1")
        val whereClause = Questions.COLUMN_QUESTIONS_ID + " = " + questionID
        databaseAccess.updateTable(Questions.TABLE, fieldContainer, whereClause)
        databaseAccess.close()
    }

    fun getStatisticsForBook(context: Context, listOfBooks: ArrayList<Book>): ArrayList<BooksWithScores> {
        val databaseAccess = DatabaseAccess.getInstance(context)
        databaseAccess.open()
        val booksWithScores = ArrayList<BooksWithScores>()
        listOfBooks.forEach {
            val cursor = databaseAccess.executeRawQuery(GET_QUESTIONS_STATISTICS_FIELDS_FOR_CERTAIN_BOOK, arrayOf(it.bookID.toString()))
            var numberOfAnsweres: Float = 0.toFloat()
            var numberOfCorrectAnswers: Float = 0.toFloat()
            while (cursor.moveToNext()) {
                numberOfAnsweres += cursor.getFloat(0)
                numberOfCorrectAnswers += cursor.getFloat(1)
            }
            val scoreOfBook = numberOfCorrectAnswers / numberOfAnsweres * 100
            booksWithScores.add(BooksWithScores(it.bookName, it.bookID, if(scoreOfBook == Float.NaN) 0f else scoreOfBook))
        }
        return booksWithScores
    }


    fun getLicense(context: Context): ArrayList<LicenseEntity> {
        val databaseAccess = DatabaseAccess.getInstance(context)
        databaseAccess.open()
        val cursor = databaseAccess.executeRawQuery(GET_ALL_LICENCE, arrayOf())
        val listOfLicenses = ArrayList<LicenseEntity>()
        while (cursor.moveToNext()) {
            val licenseID = cursor.getInt(0)
            val dlNumber = cursor.getInt(1)
            val bookCategoryID = cursor.getInt(2)
            val endorsement = cursor.getString(3)
            val route = cursor.getString(4)
            val tonnageGroup = cursor.getString(5)
            listOfLicenses.add(LicenseEntity(licenseID, dlNumber, bookCategoryID, endorsement, route, tonnageGroup))
        }
        cursor.close()
        databaseAccess.close()
        return listOfLicenses
    }

    fun getQuestion(context: Context, questionID: String): Questions {
        val getQuestionWithSpecificID = "SELECT ${Questions.COLUMN_QUESTION}, ${Questions.COLUMN_ANSWER_ONE}, ${Questions.COLUMN_ANSWER_TWO}, ${Questions.COLUMN_ANSWER_THREE}, " +
                " ${Questions.COLUMN_ANSWER_FOUR}, ${Questions.COLUMN_ANSWER}, ${Questions.COLUMN_NUMBER}, " +
                " ${Questions.COLUMN_SUBCATEGORY_NAME}, ${Questions.COLUMN_QUESTIONS_ID}, ${Questions.COLUMN_BOOKMARKED}, ${Questions.COLUMN_ILLUSTRATION}  FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_QUESTIONS_ID} = ? "
        val databaseAccess = DatabaseAccess.getInstance(context)
        databaseAccess.open()
        val cursor = databaseAccess.executeRawQuery(getQuestionWithSpecificID, arrayOf(questionID))
        val question = dataFetchingForOneQuestion(cursor)
        cursor.close()
        databaseAccess.close()
        return question

    }

    fun getQuestionIDs(context: Context, bookCategoryID: String, bookID: String?, dlNumber: String, categoryID: String?, subCategoryID: String?): ArrayList<Int> {
        val getAllQuestionsForWholeBookCategory = "SELECT ${Questions.COLUMN_QUESTIONS_ID}  FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_BOOK_CATEGORY_ID} = ? "
        val getAllQuestionsFromBookCategory = "SELECT ${Questions.COLUMN_QUESTIONS_ID} FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_BOOK_CATEGORY_ID} = ? AND ZDL" + dlNumber + " = 1 "
        val getAllQuestionsFromBook = "SELECT ${Questions.COLUMN_QUESTIONS_ID}  FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_BOOK_CATEGORY_ID} = ? AND ZDL" + dlNumber + " = 1 " +
                " AND ${Questions.COLUMN_BOOK_ID} = ?"
        val getAllQuestionsFromCategory = "SELECT ${Questions.COLUMN_QUESTIONS_ID}  FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_BOOK_CATEGORY_ID} = ? AND ZDL" + dlNumber + " = 1 " +
                " AND ${Questions.COLUMN_BOOK_ID} = ? AND ${Questions.COLUMN_CATEGORY_ID} = ?"
        val getAllQuestionsFromSubcategoryWithoutCategory = "SELECT ${Questions.COLUMN_QUESTIONS_ID}  FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_BOOK_CATEGORY_ID} = ? AND ZDL" + dlNumber + " = 1 " +
                " AND ${Questions.COLUMN_BOOK_ID} = ? AND ${Questions.COLUMN_SUBCATEGORY_ID} = ?"
        val getAllQuestionsFromSubcategory = "SELECT ${Questions.COLUMN_QUESTIONS_ID}  FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_BOOK_CATEGORY_ID} = ? AND ZDL" + dlNumber + " = 1 " +
                " AND ${Questions.COLUMN_BOOK_ID} = ? AND ${Questions.COLUMN_CATEGORY_ID} = ? AND ${Questions.COLUMN_SUBCATEGORY_ID} = ?"

        val listOfQuestions: ArrayList<Int>
        val databaseAccess = DatabaseAccess.getInstance(context)
        databaseAccess.open()
        when {
            bookID!!.contains("All Engine") -> {
                val cursor = databaseAccess.executeRawQuery(getAllQuestionsForWholeBookCategory, arrayOf("1"))
                listOfQuestions = idFetching(cursor)
                cursor.close()
            }
            bookID.contains("All Deck") -> {
                val cursor = databaseAccess.executeRawQuery(getAllQuestionsForWholeBookCategory, arrayOf("2"))
                listOfQuestions = idFetching(cursor)
                cursor.close()
            }
            bookID == "-1" -> {
                val cursor = databaseAccess.executeRawQuery(getAllQuestionsFromBookCategory, arrayOf(bookCategoryID))
                listOfQuestions = idFetching(cursor)
                cursor.close()
            }

            subCategoryID != "-1" && categoryID == "-1" -> {
                val cursor = databaseAccess.executeRawQuery(getAllQuestionsFromSubcategoryWithoutCategory, arrayOf(bookCategoryID, bookID, subCategoryID!!))
                listOfQuestions = idFetching(cursor)
                cursor.close()
            }
            categoryID == "-1" -> {
                val cursor = databaseAccess.executeRawQuery(getAllQuestionsFromBook, arrayOf(bookCategoryID, bookID))
                listOfQuestions = idFetching(cursor)
                cursor.close()
            }
            subCategoryID == "-1" -> {
                val cursor = databaseAccess.executeRawQuery(getAllQuestionsFromCategory, arrayOf(bookCategoryID, bookID, categoryID!!))
                listOfQuestions = idFetching(cursor)
                cursor.close()
            }
            else -> {
                val cursor = databaseAccess.executeRawQuery(getAllQuestionsFromSubcategory, arrayOf(bookCategoryID, bookID, categoryID!!, subCategoryID!!))
                listOfQuestions = idFetching(cursor)
                cursor.close()
            }
        }

        databaseAccess.close()
        return listOfQuestions
    }

    private fun dataFetchingForOneQuestion(cursor: Cursor): Questions {
        val questionIndex = cursor.getColumnIndex(Questions.COLUMN_QUESTION)
        val answerOneIndex = cursor.getColumnIndex(Questions.COLUMN_ANSWER_ONE)
        val answerTwoIndex = cursor.getColumnIndex(Questions.COLUMN_ANSWER_TWO)
        val answerThreeIndex = cursor.getColumnIndex(Questions.COLUMN_ANSWER_THREE)
        val answerFourIndex = cursor.getColumnIndex(Questions.COLUMN_ANSWER_FOUR)
        val correctAnswerIndex = cursor.getColumnIndex(Questions.COLUMN_ANSWER)
        val numberIndex = cursor.getColumnIndex(Questions.COLUMN_NUMBER)
        val subcategoryNameIndex = cursor.getColumnIndex(Questions.COLUMN_SUBCATEGORY_NAME)
        val questionIDIndex = cursor.getColumnIndex(Questions.COLUMN_QUESTIONS_ID)
        val isBookmarkedIndex = cursor.getColumnIndex(Questions.COLUMN_BOOKMARKED)
        val illustrationIndex = cursor.getColumnIndex(Questions.COLUMN_ILLUSTRATION)
        cursor.moveToNext()
        val question = cursor.getString(questionIndex)
        val answerOne = cursor.getString(answerOneIndex)
        val answerTwo = cursor.getString(answerTwoIndex)
        val answerThree = cursor.getString(answerThreeIndex)
        val answerFour = cursor.getString(answerFourIndex)
        val correctAnswer = cursor.getString(correctAnswerIndex)
        val number = cursor.getString(numberIndex)
        val subcategoryName = cursor.getString(subcategoryNameIndex)
        val questionID = cursor.getString(questionIDIndex)
        val isBookmarked = cursor.getString(isBookmarkedIndex)
        val illustration = cursor.getString(illustrationIndex)
        return Questions(question, answerOne, answerTwo, answerThree, answerFour, correctAnswer, number, subcategoryName, questionID, isBookmarked, illustration)
    }

    private fun dataFetching(cursor: Cursor): ArrayList<Questions> {
        val listOfQuestions = ArrayList<Questions>()
        val questionIndex = cursor.getColumnIndex(Questions.COLUMN_QUESTION)
        val answerOneIndex = cursor.getColumnIndex(Questions.COLUMN_ANSWER_ONE)
        val answerTwoIndex = cursor.getColumnIndex(Questions.COLUMN_ANSWER_TWO)
        val answerThreeIndex = cursor.getColumnIndex(Questions.COLUMN_ANSWER_THREE)
        val answerFourIndex = cursor.getColumnIndex(Questions.COLUMN_ANSWER_FOUR)
        val correctAnswerIndex = cursor.getColumnIndex(Questions.COLUMN_ANSWER)
        val numberIndex = cursor.getColumnIndex(Questions.COLUMN_NUMBER)
        val subcategoryNameIndex = cursor.getColumnIndex(Questions.COLUMN_SUBCATEGORY_NAME)
        val questionIDIndex = cursor.getColumnIndex(Questions.COLUMN_QUESTIONS_ID)
        val isBookmarkedIndex = cursor.getColumnIndex(Questions.COLUMN_BOOKMARKED)
        val illustrationIndex = cursor.getColumnIndex(Questions.COLUMN_ILLUSTRATION)
        while(cursor.moveToNext()) {
            val question = cursor.getString(questionIndex)
            val answerOne = cursor.getString(answerOneIndex)
            val answerTwo = cursor.getString(answerTwoIndex)
            val answerThree = cursor.getString(answerThreeIndex)
            val answerFour = cursor.getString(answerFourIndex)
            val correctAnswer = cursor.getString(correctAnswerIndex)
            val number = cursor.getString(numberIndex)
            val subcategoryName = cursor.getString(subcategoryNameIndex)
            val questionID = cursor.getString(questionIDIndex)
            val isBookmarked = cursor.getString(isBookmarkedIndex)
            val illustration = cursor.getString(illustrationIndex)
            listOfQuestions.add(Questions(question, answerOne, answerTwo, answerThree, answerFour, correctAnswer, number, subcategoryName, questionID, isBookmarked, illustration))
        }
        return listOfQuestions
    }

    private fun idFetching(cursor: Cursor): ArrayList<Int>{
        val listOfIDs = ArrayList<Int>()
        while (cursor.moveToNext()){
            listOfIDs.add(cursor.getInt(0))
        }
        return listOfIDs
    }

    fun getBookmarkedQuestions(context: Context, bookName: String): ArrayList<Int> {
        val prefs = context.getSharedPreferences(MyApp.USER_LICENSE_DATA_VALUES, 0)
        val dlNumber = prefs.getString(MyApp.DL_NUMBER, "")
        val databaseAccess = DatabaseAccess.getInstance(context)
        databaseAccess.open()
        val listOfQuestions: ArrayList<Int>

        val getAllBookmarkedQuestions = "SELECT ${Questions.COLUMN_QUESTIONS_ID}  FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_BOOKMARKED} = 1 AND " +
                " ZDL" + dlNumber + " = 1 AND ${Questions.COLUMN_BOOK_NAME} = ?"

        val cursor = databaseAccess.executeRawQuery(getAllBookmarkedQuestions, arrayOf(bookName))
        listOfQuestions = idFetching(cursor)
        cursor.close()
        databaseAccess.close()
        return listOfQuestions
    }

    fun changeBookmark(context: Context, questionID: String, value: String){
        val fieldContainer = ContentValues()
        fieldContainer.put(Questions.COLUMN_BOOKMARKED, value)
        val whereClause = Questions.COLUMN_QUESTIONS_ID + " = " + questionID
        val databaseAccess = DatabaseAccess.getInstance(context)
        databaseAccess.open()
        databaseAccess.updateTable(Questions.TABLE, fieldContainer, whereClause)
        databaseAccess.close()
    }

    fun getSearchedQuestionIDs(context: Context, searchWord: String, bookCategoryID: String, bookID: String?): ArrayList<Int>{
        val getQuestionMatchingIDForWholeCategory = "SELECT ${Questions.COLUMN_QUESTIONS_ID} FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_BOOK_CATEGORY_ID} = $bookCategoryID "
        val getQuestionMatchingID = "SELECT ${Questions.COLUMN_QUESTIONS_ID} FROM ${Questions.TABLE} WHERE ${Questions.COLUMN_BOOK_CATEGORY_ID} = $bookCategoryID AND ${Questions.COLUMN_BOOK_ID} = $bookID "
        val whereClause = arrayOf(" AND " + Questions.COLUMN_QUESTION + " LIKE '%" + searchWord + "%'",
                " AND " + Questions.COLUMN_ANSWER_ONE + " LIKE '%" + searchWord + "%'",
                " AND " + Questions.COLUMN_ANSWER_TWO + " LIKE '%" + searchWord + "%'",
                " AND " + Questions.COLUMN_ANSWER_THREE + " LIKE '%" + searchWord + "%'",
                " AND " + Questions.COLUMN_ANSWER_FOUR + " LIKE '%" + searchWord + "%'",
                " AND " + Questions.COLUMN_NUMBER + " LIKE '%" + searchWord + "%'",
                " AND " + Questions.COLUMN_SUBCATEGORY_NAME + " LIKE '%" + searchWord + "%'")
        val databaseAccess = DatabaseAccess.getInstance(context)
        databaseAccess.open()
        val listOfQuestionIDs = ArrayList<Int>()
        if(bookID != null){
            for(i in 0..6) {
                val cursor = databaseAccess.executeRawQuery(getQuestionMatchingID + whereClause[i], arrayOf())
                while (cursor.moveToNext()){
                    listOfQuestionIDs.add(cursor.getInt(0))
                }
                cursor.close()
            }
        }else{
            for(i in 0..6) {
                val query = getQuestionMatchingIDForWholeCategory + whereClause[i]
                val cursor = databaseAccess.executeRawQuery(query, arrayOf())
                while (cursor.moveToNext()){
                    listOfQuestionIDs.add(cursor.getInt(0))
                }
                cursor.close()
            }
        }
        databaseAccess.close()
        return listOfQuestionIDs
    }
}