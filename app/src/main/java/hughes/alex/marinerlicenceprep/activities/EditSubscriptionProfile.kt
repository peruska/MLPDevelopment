package hughes.alex.marinerlicenceprep.activities

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso
import hughes.alex.marinerlicenceprep.AuthService
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.MyApp.Companion.defaultUser
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.entity.UserEntity
import kotlinx.android.synthetic.main.activity_edit_subscription_profile.*
import java.io.File
import java.text.SimpleDateFormat

class EditSubscriptionProfile : AppCompatActivity() {

    private lateinit var photo: File
    private lateinit var mImageUri: Uri
    private lateinit var profilePictureBitmap: Bitmap
    private var alreadySubscribed: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_subscription_profile)
        if (!MyApp.checkIfUserIsSubscribed()) {
            subType.text = "Subscription: NONE"
            alreadySubscribed = false
        } else {
            subType.text = "Expiration: " +
                    SimpleDateFormat("MM/dd/yyyy").format(SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(defaultUser?.subscriptionEndDate))
            if (!defaultUser?.subscriptionName!!.contains("Trial")) {
                alreadySubscribed = true
                oneMonth.background.setColorFilter(resources.getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC)
                threeMonths.background.setColorFilter(resources.getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC)
                setBlur()
            }
        }
        if (defaultUser?.subscriptionName!!.contains("1"))
            oneMonthCheck.visibility = View.VISIBLE
        else if (defaultUser?.subscriptionName!!.contains("3"))
            threeMonthCheck.visibility = View.VISIBLE
        Picasso.get().load("https://marinerlicenseprep.com/" + defaultUser?.profileImageURL).into(profilePictureImageView)
        editProfileUsername.text = (defaultUser as UserEntity).username

        cancel.setOnClickListener { finish() }
    }

    private fun setBlur() {
        val radius = textView.textSize / 10
        val filter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
        textView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        textView.paint.maskFilter = filter
        textView2.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        textView2.paint.maskFilter = filter
        textView3.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        textView3.paint.maskFilter = filter
        textView4.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        textView4.paint.maskFilter = filter
        textView5.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        textView5.paint.maskFilter = filter
        textView6.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        textView6.paint.maskFilter = filter
    }

    fun moveToStripe(view: View) {
        if (alreadySubscribed) return
        val paymentIntent = Intent(this, Payment::class.java)
        paymentIntent.putExtra("duration", if (view.id == R.id.oneMonth) "oneMonth" else "threeMonths")
        startActivity(paymentIntent)
        finish()
    }

    fun changeProfilePicture(view: View) {
        photo = createTemporaryFile("picture", ".jpg")
        photo.delete()
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setItems(items) { dialog, item ->
            when {
                items[item] == "Take Photo" -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    mImageUri = FileProvider.getUriForFile(this, this.applicationContext.packageName + ".my.package.name.provider", photo)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
                    startActivityForResult(intent, 1)
                }
                items[item] == "Choose from Library" -> {
                    val intent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, 2)
                }
                items[item] == "Cancel" -> dialog.dismiss()
            }
        }
        val dialog = builder.create()
        val listView = dialog.listView
        listView.divider = ColorDrawable(resources.getColor(R.color.colorPrimary))
        listView.dividerHeight = 2
        listView.overscrollFooter = ColorDrawable(Color.TRANSPARENT)
        dialog.show()
    }

    private fun createTemporaryFile(part: String, ext: String): File {
        var tempDir = Environment.getExternalStorageDirectory()
        tempDir = File(tempDir.absolutePath + "/.temp/")
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        return File.createTempFile(part, ext, tempDir)
    }

    private fun grabImage(imageView: ImageView) {
        this.contentResolver.notifyChange(mImageUri, null)
        val contentResolver: ContentResolver = this.contentResolver
        try {
            val bitmap = android.provider.MediaStore.Images.Media.getBitmap(contentResolver, mImageUri)
            imageView.setImageBitmap(bitmap)
            profilePictureBitmap = bitmap
        } catch (ex: Exception) {
            Log.d("Failed to load", ex.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            1 -> if (resultCode == Activity.RESULT_OK) {
                grabImage(profilePictureImageView)
            }
            2 -> if (resultCode == Activity.RESULT_OK) {
                onSelectFromGalleryResult(imageReturnedIntent!!)
            }
        }
        AuthService(this).uploadPhoto(this, profilePictureBitmap, MyApp.USER_ACCOUNT_EMAIL, defaultUser!!.username)
    }

    private fun onSelectFromGalleryResult(data: Intent) {
        val selectedImageUri = data.data
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = managedQuery(selectedImageUri, projection, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        cursor.moveToFirst()
        val selectedImagePath = cursor.getString(column_index)
        val bm: Bitmap
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(selectedImagePath, options)
        val REQUIRED_SIZE = 200
        var scale = 1
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2
        options.inSampleSize = scale
        options.inJustDecodeBounds = false
        bm = BitmapFactory.decodeFile(selectedImagePath, options)

        profilePictureImageView.setImageBitmap(bm)
        profilePictureBitmap = bm
    }

}
