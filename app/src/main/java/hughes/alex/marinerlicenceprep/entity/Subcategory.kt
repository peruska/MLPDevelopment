package hughes.alex.marinerlicenceprep.entity

class Subcategory(var subcategoryName: String, var subcategroyID: String) {
    companion object {
        const val TABLE = "ZSUBCATEGORY"

        const val COLUMN_SUBCATEGORY_ID = "Z_PK"
        const val  COLUMN_ENT = "Z_ENT"
        const val  COLUMN_OPT = "Z_OPT"
        const val  COLUMN_NUMBER = "ZNUMBER"
        const val  COLUMN_SUBCATEGORY_NAME = "ZSUBCATEGORYNAME"
    }
}