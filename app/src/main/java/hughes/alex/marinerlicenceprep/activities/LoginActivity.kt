package hughes.alex.marinerlicenceprep.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.transition.Fade
import android.transition.Scene
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import hughes.alex.marinerlicenceprep.AuthService
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.sign_in_scene.*
import org.jetbrains.anko.toast
import android.widget.EditText
import hughes.alex.marinerlicenceprep.R
import kotlinx.android.synthetic.main.login_scene.*
import android.graphics.BitmapFactory
import android.provider.MediaStore.MediaColumns




class LoginActivity : AppCompatActivity() {

    private lateinit var profilePictureBitmap: Bitmap
    private lateinit var loginScene: Scene
    private lateinit var signUpScene: Scene
    private var transition = Fade()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.statusBarColor=resources.getColor(R.color.colorPrimary)
        loginScene = Scene.getSceneForLayout(scene_root as ViewGroup, R.layout.login_scene, this)
        signUpScene = Scene.getSceneForLayout(scene_root as ViewGroup, R.layout.sign_in_scene,this)
        transition.duration = 300
    }

    fun transitionToSignUp(view: View) {
        TransitionManager.go(signUpScene, transition)
    }

    fun transitionToLogIn(view: View) {
        TransitionManager.go(loginScene, transition)
    }

    fun showPhotoDialog(view: View) {
        //TODO ADD PERMISSION CHECK
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setItems(items) { dialog, item ->
            when {
                items[item] == "Take Photo" -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            1 -> if (resultCode == Activity.RESULT_OK) {
                val extras = imageReturnedIntent.extras
                profilePictureBitmap = extras.get("data") as Bitmap
                profilePictureSignUp.setImageBitmap(profilePictureBitmap)
            }
            2 -> if (resultCode == Activity.RESULT_OK) {
                onSelectFromGalleryResult(imageReturnedIntent)
            }
        }
    }

    private fun onSelectFromGalleryResult(data: Intent) {
        val selectedImageUri = data.data
        val projection = arrayOf(MediaColumns.DATA)
        val cursor = managedQuery(selectedImageUri, projection, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA)
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

        profilePictureSignUp.setImageBitmap(bm)
    }


    fun submitSignUpRequest(view: View) {
        val value = inputsAreInvalid()
        if (value != 0)
            showErrorInInputs(value)
        else
            AuthService(this).signUp(
                    this,
                    profilePictureBitmap,
                    emailSignUp.text.toString(),
                    usernameSignUp.text.toString(),
                    paswordSignUp.text.toString())
    }

    fun submitLogInRequest(view: View) {
        if (emailLogin.text.toString().isEmpty()) {
            toast("You didn't enter email.")
            return
        }
        if (passwordLogin.text.toString().isEmpty()) {
            toast("You didn't enter password.")
            return
        }
        if (!isEmailValid(emailLogin.text.toString())) {
            toast("Wrong email form.")
            return
        }
        AuthService(this).signIn(
                emailLogin.text.toString(),
                passwordLogin.text.toString()
        )
    }


    private fun showErrorInInputs(value: Int) {
        when (value) {
            1 -> toast("You didn't enter username.")
            2 -> toast("You didn't enter email.")
            3 -> toast("Invalid email.")
            4 -> toast("You didn't enter password.")
            5 -> toast("You didn't confirm your password.")
            6 -> toast("Passwords don't match.")
            7 -> toast("Please upload photo.")
        }
    }

    private fun inputsAreInvalid(): Int {
        if (usernameSignUp.text.isNullOrEmpty()) return 1
        if (emailSignUp.text.isNullOrEmpty()) return 2
        if (!isEmailValid(emailSignUp.text.toString())) return 3
        if (paswordSignUp.text.isNullOrEmpty()) return 4
        if (confirmPasswordSignUp.text.isNullOrEmpty()) return 5
        if (confirmPasswordSignUp.text.toString() != paswordSignUp.text.toString()) return 6
        if (!::profilePictureBitmap.isInitialized) return 7
        return 0
    }

    private fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun forgotPassword(view: View) {
        val alert = AlertDialog.Builder(this)
        val emailEditText = EditText(this)
        alert.setMessage("Please enter your email. An email will be sent with a link to reset your password.")
        alert.setTitle("Reset password")
        emailEditText.hint = "Enter email here..."
        alert.setView(emailEditText)
        alert.setPositiveButton("Reset") { _, _ -> AuthService(this).resetPassword(emailEditText.text.toString()) }
        alert.setNegativeButton("Cancel") { _, _ -> }

        alert.show()
    }
}
