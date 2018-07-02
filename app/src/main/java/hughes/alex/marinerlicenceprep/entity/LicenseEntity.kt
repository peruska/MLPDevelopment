package hughes.alex.marinerlicenceprep.entity

class LicenseEntity (var licenseID: Int, var dlNumner: Int, var bookCategoryID: Int, var endorsement: String,
                     var route: String, var tonnageGroup: String) {
    companion object {
        const val TABLE = "ZLICENSE"

        const val COLUMN_LICENSE_ID = "Z_PK"
        const val COLUMN_ENT = "Z_ENT"
        const val COLUMN_OPT = "Z_OPT"
        const val COLUMN_DL_NUMBER = "ZDL_NUMBER"
        const val COLUMN_BOOK_CATEGORY_ID = "ZINBOOKCATEGORY"
        const val COLUMN_DISPLAY_ORDER_NUMBER = "ZDISPLAYORDERNUMBER"
        const val COLUMN_REF_NUM = "ZREFNUM"
        const val COLUMN_ENDORSEMENT = "ZENDORSEMENT"
        const val COLUMN_ROUTE = "ZROUTE"
        const val COLUMN_TONNAGE_GROUP = "ZTONNAGEGROUP"
    }
}