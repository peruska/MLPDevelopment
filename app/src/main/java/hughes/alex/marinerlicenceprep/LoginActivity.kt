package hughes.alex.marinerlicenceprep

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.transition.ArcMotion
import android.transition.ChangeBounds
import android.transition.Scene
import android.transition.TransitionManager
import android.view.ViewGroup
import hughes.alex.marinerlicenceprep.authentication.AuthService
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    lateinit var mAScene: Scene
    lateinit var mAnotherScene: Scene

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_login)
        /*
        var mSceneRoot = scene_root as ViewGroup
        mAScene = Scene.getSceneForLayout(mSceneRoot, R.layout.login_scene, this)
        mAnotherScene = Scene.getSceneForLayout(mSceneRoot, R.layout.sign_in_scene, this)
        val mFadeTransition = ChangeBounds()
        mFadeTransition.pathMotion = ArcMotion()
        mFadeTransition.duration = 500
        TransitionManager.beginDelayedTransition(mSceneRoot, mFadeTransition)
*/
        AuthService(this).signIn("peruskatestira@gmail.com", "Snajper123")
    }


}
