package ru.breffi.storyidsample.ui.common

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.include_app_bar.*
import ru.breffi.storyidsample.R
import javax.inject.Inject

abstract class BaseInjectableActivity : BaseActivity(), HasSupportFragmentInjector {

    lateinit var context: Activity

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        context = this
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return supportFragmentInjector
    }

    override fun setTitle(title: CharSequence) {
        val actionBar = supportActionBar
        if (toolbar != null) {
            val titleView = toolbar.findViewById<TextView>(R.id.toolbar_title)
            if (titleView != null) {
                titleView.text = title
            }
        }

        actionBar?.setDisplayShowTitleEnabled(false)
    }

}
