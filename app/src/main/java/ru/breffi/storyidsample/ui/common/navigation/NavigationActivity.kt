package ru.breffi.storyidsample.ui.common.navigation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.*
import ru.breffi.storyidsample.ui.common.BaseFragment
import ru.breffi.storyidsample.ui.common.BaseInjectableActivity
import ru.breffi.storyidsample.ui.common.BasePageFragment
import kotlinx.android.synthetic.main.include_app_bar.*
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.ui.profile.ProfileFragment
import ru.breffi.storyidsample.utils.hideKeyboard

class NavigationActivity : BaseInjectableActivity(),
    BasePageFragment.FragmentListener,
    FragmentManager.OnBackStackChangedListener {

    private val profileFragment = ProfileFragment.newInstance()

    companion object {

        fun start(context: Context, intent: Intent?, clearTop: Boolean) {
            var starter = intent

            if (starter == null) {
                starter = Intent(context, NavigationActivity::class.java)
            } else {
                starter.setClass(context, NavigationActivity::class.java)
            }

            if (clearTop) {
                starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_base)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            switchFragment(profileFragment)
        } else {
            onBackStackChanged() //show back arrow if needed
        }

        supportFragmentManager.addOnBackStackChangedListener(this)
    }

    private fun findByTag(tag: String) : BaseFragment? {
        return supportFragmentManager.findFragmentByTag(tag) as? BaseFragment
    }

    private fun switchFragment(fragment: BasePageFragment) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.commit {
            hideKeyboard()
            replace(R.id.contentFrame, fragment)
        }
    }

    override fun onToTopFragment() {
        clearBackStack()
    }

    override fun onFragmentResumed(fragment: BasePageFragment) {
        val title = fragment.getTitle(
            this,
            resources.displayMetrics.widthPixels - toolbar.contentInsetStart * 2
                    - toolbar.contentInsetEnd * 2
        )
        setTitle(title)
    }

    override fun onNextFragment(fragment: BasePageFragment) {
        supportFragmentManager.commit {
            hideKeyboard()
            replace(R.id.contentFrame, fragment)
            addToBackStack(null)
        }
    }

    override fun onBackFragment(count: Int) {
        val backStackCount = supportFragmentManager.backStackEntryCount
        val backStackId = supportFragmentManager.getBackStackEntryAt(backStackCount - count).id
        supportFragmentManager.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun onSwapFragment(fragment: Fragment) {
        supportFragmentManager.commitNow {
            hideKeyboard()
            replace(R.id.contentFrame, fragment)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }
    }

    override fun onBackStackChanged() {
        val stackEntryCount = supportFragmentManager.backStackEntryCount
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(stackEntryCount > 0)
    }

    private fun clearBackStack() {
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun onBackPressed() {
        hideKeyboard()
        super.onBackPressed()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }
}