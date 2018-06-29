package hughes.alex.marinerlicenceprep.entity

class Book(var bookID: Int, var ent: Int, var opt: Int, var parentCategory: Int, var refNum: Float, var bookName: String ) {
    companion object {
        const val TABLE = "ZBOOK"

        const val COLUMN_BOOK_ID = "Z_PK"
        const val COLUMN_ENT = "Z_ENT"
        const val COLUMN_OPT = "Z_OPT"
        const val COLUMN_PARENT_CATEGORY = "ZPARENTCATEGORY"
        const val COLUMN_REFNUM = "ZREFNUM"
        const val COLUMN_BOOKNAME = "ZBOOKNAME"
    }

}