package hughes.alex.marinerlicenceprep.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.activity_illustration.*
import android.graphics.drawable.Drawable
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase


class Illustration : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_illustration)
        val illustrationName = intent.extras.getString("illustrationName")
        illustrationImageView.displayType = ImageViewTouchBase.DisplayType.FIT_TO_SCREEN
        try {
            val ims = assets.open("Illustrations/"+illustrationName+".imageset/"+illustrationName+".png")
            val d = Drawable.createFromStream(ims, null)
            illustrationImageView.setImageDrawable(d)
        }catch (e: Exception){
            val ims = assets.open("Illustrations/"+illustrationName+".imageset/"+illustrationName+".jpg")
            val d = Drawable.createFromStream(ims, null)
            illustrationImageView.setImageDrawable(d)
        }
    }
}
