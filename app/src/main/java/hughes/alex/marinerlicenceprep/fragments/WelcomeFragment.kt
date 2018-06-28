package hughes.alex.marinerlicenceprep.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import hughes.alex.marinerlicenceprep.R


class WelcomeFragment : Fragment() {
    companion object {
        fun newInstance(): WelcomeFragment {
            return WelcomeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.welcome_fragment, container, false)
        return view
    }
}