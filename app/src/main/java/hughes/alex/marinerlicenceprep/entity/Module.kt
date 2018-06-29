package hughes.alex.marinerlicenceprep.entity

class Module {
    companion object {
        const val TABLE = "ZMODULE"

        const val COLUMN_MODULE_ID = "Z_PK"
        const val COLUMN_ENT = "Z_ENT"
        const val COLUMN_OPT = "Z_OPT"
        const val COLUMN_BOOK_CATEGORY_ID = "ZBOOKCAT"
        const val COLUMN_LICENSE_ID = "ZINLICENSE"
        const val COLUMN_MIN_SCORE = "ZMINSCORE"
        const val COLUMN_NUMBER_OF_QUESTIONS = "ZNUMBEROFQUESTIONS"
        const val COLUMN_QUESTION_POOL = "ZQUESTIONPOOL"
        const val COLUMN_NAME = "ZNAME"
    }
}