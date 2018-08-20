package hughes.alex.marinerlicenceprep.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.transition.Fade
import android.transition.Scene
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import hughes.alex.marinerlicenceprep.AuthService
import hughes.alex.marinerlicenceprep.MyApp
import hughes.alex.marinerlicenceprep.R
import hughes.alex.marinerlicenceprep.entity.UserEntity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.login_scene.*
import kotlinx.android.synthetic.main.sign_in_scene.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.customView
import org.jetbrains.anko.editText
import org.jetbrains.anko.toast
import java.io.File
import java.util.*


class LoginActivity : AppCompatActivity() {
    lateinit var photo: File
    lateinit var mImageUri: Uri
    private lateinit var profilePictureBitmap: Bitmap
    private lateinit var loginScene: Scene
    private lateinit var signUpScene: Scene
    private var transition = Fade()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchUserPreferences()
        if (alreadyLoggedIn())
            loadHomeActivity()
        setContentView(R.layout.activity_login)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET), 1)
        checkIfUUIDExists()
        initLoginActivity()
    }

    private fun loadHomeActivity() {
        startActivity(Intent(this, Home::class.java))
        finish()
    }

    private fun initLoginActivity() {
        window.statusBarColor = resources.getColor(R.color.colorPrimary)
        loginScene = Scene.getSceneForLayout(scene_root as ViewGroup, R.layout.login_scene, this)
        signUpScene = Scene.getSceneForLayout(scene_root as ViewGroup, R.layout.sign_in_scene, this)
        transition.duration = 300
        passwordLogin.onSubmit {
            submitLogInRequest(passwordLogin)

            //Hide soft keyboard
            val view = this.currentFocus
            if (view != null) {
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }


    }

    private fun checkIfUUIDExists() {
        val prefs = this.getSharedPreferences(MyApp.USER_ACCOUNT_PREFERENCES, 0)
        MyApp.uuid = prefs.getString("uuid", "")
        if (MyApp.uuid == "") {
            val editor = prefs.edit()
            MyApp.uuid = UUID.randomUUID().toString()
            editor.putString("uuid", MyApp.uuid)
            editor.commit()
        }
    }

    private fun fetchUserPreferences() {
        val prefs = this.getSharedPreferences(MyApp.USER_ACCOUNT_PREFERENCES, 0)
        val username = prefs.getString(MyApp.USER_ACCOUNT_USERNAME, "")
        val email = prefs.getString(MyApp.USER_ACCOUNT_EMAIL, "")
        val profilePictureURL = prefs.getString(MyApp.USER_ACCOUNT_PROFILE_PICTURE_URL, "")
        val subToDate = prefs.getString(MyApp.USER_ACCOUNT_SUB_TO_DATE, "")
        val subType = prefs.getString(MyApp.USER_ACCOUNT_SUB_TYPE, "")
        MyApp.defaultUser = UserEntity(username, email, profilePictureURL, subToDate, subType)
    }

    private fun alreadyLoggedIn(): Boolean {
        return MyApp.defaultUser?.email!!.isNotBlank()
    }


    fun transitionToSignUp(view: View) {
        TransitionManager.go(signUpScene, transition)
        confirmPasswordSignUp.onSubmit {
            submitSignUpRequest(confirmPasswordSignUp)
            //Hide soft keyboard
            val view = this.currentFocus
            if (view != null) {
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    fun transitionToLogIn(view: View) {
        TransitionManager.go(loginScene, transition)
    }

    fun showPhotoDialog(view: View) {
        photo = createTemporaryFile("picture", ".jpg")
        photo.delete()
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setItems(items) { dialog, item ->
            when {
                items[item] == "Take Photo" -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    //mImageUri = Uri.fromFile(photo)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            1 -> if (resultCode == Activity.RESULT_OK) {
                grabImage(profilePictureSignUp)
            }
            2 -> if (resultCode == Activity.RESULT_OK) {
                onSelectFromGalleryResult(imageReturnedIntent!!)
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
        profilePictureBitmap = bm
    }


    fun submitSignUpRequest(view: View) {
        val usernameInput = findViewById<EditText>(R.id.usernameSignUp)
        val emailInput = findViewById<EditText>(R.id.emailSignUp)
        val passwordInput = findViewById<EditText>(R.id.paswordSignUp)
        val value = inputsAreInvalid()
        if (value != 0)
            showErrorInInputs(value)
        else
            AuthService(this).signUp(
                    this,
                    profilePictureBitmap,
                    emailInput.text.toString(),
                    usernameInput.text.toString(),
                    passwordInput.text.toString())
    }

    fun submitLogInRequest(view: View) {
        val passwordInput = findViewById<EditText>(R.id.passwordLogin)
        val emailInput = findViewById<EditText>(R.id.emailLogin)
        if (emailInput.text.toString().isBlank()) {
            toast("You didn't enter email.")
            return
        }
        if (passwordInput.text.toString().isBlank()) {
            toast("You didn't enter password." + passwordLogin.text.toString())
            return
        }
        if (!isEmailValid(emailInput.text.toString())) {
            toast("Wrong email form.")
            return
        }
        AuthService(this).signIn(
                emailInput.text.toString().trim(),
                passwordInput.text.toString()
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
            8 -> toast("Password must be at least 8 characters long.")
        }
    }

    private fun inputsAreInvalid(): Int {
        val usernameInput = findViewById<EditText>(R.id.usernameSignUp)
        val emailInput = findViewById<EditText>(R.id.emailSignUp)
        val passwordInput = findViewById<EditText>(R.id.paswordSignUp)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirmPasswordSignUp)
        if (usernameInput.text.isBlank()) return 1
        if (emailInput.text.isBlank()) return 2
        if (!isEmailValid(emailInput.text.toString())) return 3
        if (passwordInput.text.isBlank()) return 4
        if (confirmPasswordInput.text.isBlank()) return 5
        if (confirmPasswordInput.text.toString() != passwordInput.text.toString()) return 6
        if (!::profilePictureBitmap.isInitialized) return 7
        if (passwordInput.text.length < 8) return 7
        return 0
    }

    private fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun forgotPassword(view: View) {
        alert {
            title = "Reset password"
            message = "Please enter your email. An email will be sent with a link to reset your password."
            customView {
                val emailEditText = editText()
                emailEditText.hint = "Enter email here..."
                positiveButton("Reset") {
                    val email = emailEditText.text.toString()
                    if (email.isNotBlank() && isEmailValid(email))
                        AuthService(this@LoginActivity).resetPassword(email)
                    else toast("You didn't enter valid email")
                }
                negativeButton("Cancel") {}
            }
        }.show()
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

    //Submit login request when user clicks ok button after password input
    private fun EditText.onSubmit(func: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                func()
            }
            true
        }
    }
}
