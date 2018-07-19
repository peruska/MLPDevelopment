package hughes.alex.marinerlicenceprep.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.activity_illustration.*
import android.graphics.drawable.Drawable
import android.view.View
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase


class Illustration : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_illustration)

        //Fix passed image name
        val illustrationName = fixImageName(intent.extras.getString("illustrationName"))
        illustrationLabel.text = illustrationName

        //Adapt image view to match parent
        illustrationImageView.baselineAlignBottom = false
        illustrationImageView.displayType = ImageViewTouchBase.DisplayType.FIT_TO_SCREEN

        //Try to open png with fixed image name
        try {
            val ims = assets.open("Illustrations/$illustrationName.imageset/$illustrationName.png")
            val d = Drawable.createFromStream(ims, null)
            illustrationImageView.setImageDrawable(d)
        }

        //If try fails try to open jpg with image name
        catch (e: Exception){
            val ims = assets.open("Illustrations/$illustrationName.imageset/$illustrationName.jpg")
            val d = Drawable.createFromStream(ims, null)
            illustrationImageView.setImageDrawable(d)
        }

    }

    private fun fixImageName(diagramRef: String) : String {
        val trimmedString = diagramRef.replace("\\s".toRegex(), "")
        val substringSep = trimmedString.split("-")
        val size = substringSep.size
        return when {
            trimmedString.contains("and") -> {
                val separator = trimmedString.split("-")
                val lastSubstring = separator[size - 1].toInt()
                val sep1 = separator[1].split("and")
                val sep2 = sep1[0]+" and "+sep1[1]
                "${separator[0]}-$sep2-$lastSubstring"
            }
            size > 2 -> {
                val lastSubstring = substringSep[size - 1].toInt()
                "${substringSep[0]}-${substringSep[1]}-$lastSubstring"
            }
            size > 1 -> {
                val lastSubstring = substringSep[size - 1].toInt()
                "${substringSep[0]}-$lastSubstring"
            }
            else -> diagramRef
        }
    }

    fun backToQuestion(view: View){
        finish()
    }
}
