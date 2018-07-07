package hughes.alex.marinerlicenceprep.entity

class Questions(var question: String, var answerOne: String, var answerTwo: String,
                var answerThree: String, var answerFour: String, var correctAnswer: String,
                var questionNumber: String, var subcategory: String, var questionID: String,
                var isBookmarked: String, var illustration: String, var numberOfTimesWrong: String, var numberOfTimesCorrect: String)  {
    companion object {
        const val TABLE = "ZQUESTIONS"

        const val COLUMN_QUESTIONS_ID = "Z_PK"
        const val COLUMN_BOOKMARKED = "ZBOOKMARKED"
        const val COLUMN_HAS_BEEN_ANSWERED = "ZHASBEENANSWERED"
        const val COLUMN_BOOK_ID = "ZINBOOK"
        const val COLUMN_BOOK_CATEGORY_ID = "ZINBOOKCATEGORY"
        const val COLUMN_CATEGORY_ID = "ZINCATEGORY"
        const val COLUMN_SUBCATEGORY_ID = "ZINSUBCATEGORY"
        const val COLUMN_NUMBER_OF_TIMES_ANSWERED = "ZNUMBEROFTIMESANSWERED"
        const val COLUMN_NUMBER_OF_TIMES_CORRECT = "ZNUMBEROFTIMESCORRECT"
        const val COLUMN_NUMBER_OF_TIMES_WRONG = "ZNUMBEROFTIMESWRONG"
        const val COLUMN_ANSWER = "ZANSWER"
        const val COLUMN_ANSWER_ONE = "ZANSWERONE"
        const val COLUMN_ANSWER_TWO = "ZANSWERTWO"
        const val COLUMN_ANSWER_THREE = "ZANSWERTHREE"
        const val COLUMN_ANSWER_FOUR = "ZANSWERFOUR"
        const val COLUMN_BOOK_NAME = "ZBOOKNAME"
        const val COLUMN_CATEGORY_NAME = "ZCATEGORYNAME"
        const val COLUMN_ILLUSTRATION = "ZILLUSTRATION"
        const val COLUMN_NUMBER = "ZNUMBER"
        const val COLUMN_QUESTION = "ZQUESTION"
        const val COLUMN_REFERENCE = "ZREFERENCE"
        const val COLUMN_REFERENCE_NUMBER = "ZREFERENCENUM"
        const val COLUMN_SOLUTION = "ZSOLUTION"
        const val COLUMN_SUBCATEGORY_NAME = "ZSUBCATEGORYNAME"
    }

}