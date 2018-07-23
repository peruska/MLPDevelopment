package hughes.alex.marinerlicenceprep.database

import hughes.alex.marinerlicenceprep.entity.Subcategory
import java.util.Comparator
import java.util.regex.Pattern

class AlphanumericSorter : Comparator<Subcategory> {
    override fun compare(o1: Subcategory?, o2: Subcategory?): Int {
        val firstString = o1!!.subcategoryName
        val secondString = o2!!.subcategoryName

        if (secondString == "" || firstString == "") {
            return 0
        }

        if(firstString.matches(".*\\d+.*".toRegex()) && secondString.matches(".*\\d+.*".toRegex())){
            val p = Pattern.compile("\\d+")
            val m = p.matcher(firstString)
            val m2 = p.matcher(secondString)
            m.find()
            m2.find()
            if(firstString.substring(0, firstString.indexOf(m.group())) == secondString.substring(0, secondString.indexOf(m2.group()))){
                when {
                    m.group().toInt() < m2.group().toInt() -> return -1
                    m2.group().toInt() < m.group().toInt() -> return 1
                    m.group().toInt() == m2.group().toInt() -> return 0
                }
            }

        }
        return when {
            firstString > secondString -> 1
            firstString < secondString -> -1
            else -> 0
        }
    }
}