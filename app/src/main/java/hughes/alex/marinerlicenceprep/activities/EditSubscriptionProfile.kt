package hughes.alex.marinerlicenceprep.activities


import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
import hughes.alex.marinerlicenceprep.MyApp.Companion.BASE_URL
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.entity.UserEntity
import kotlinx.android.synthetic.main.activity_edit_subscription_profile.*
import java.io.File

class EditSubscriptionProfile : AppCompatActivity() {

    private lateinit var photo: File
    private lateinit var mImageUri: Uri
    private lateinit var profilePictureBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_subscription_profile)

        Picasso.get().load(BASE_URL+MyApp.defaultUser!!.profileImageURL).into(profilePictureImageView)
        editProfileUsername.text =(MyApp.defaultUser as UserEntity).username

        cancel.setOnClickListener { finish() }
    }
    fun moveToStripe(view: View){
        startActivity(Intent(this, Payment::class.java))
    }
    fun changeProfilePicture(view: View){
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
        AuthService(this).uploadPhoto(this, profilePictureBitmap, MyApp.USER_ACCOUNT_EMAIL, MyApp.defaultUser!!.username)
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
