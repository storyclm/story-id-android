package ru.breffi.storyidsample.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.include_app_bar.*
import ru.breffi.storyidsample.utils.hideKeyboard

abstract class BaseFragment : Fragment() {

    var fragmentTag: String? = null

    private var isReattached: Boolean? = null

    private var hasToolbar = true
    private var hasShadowAppBar = true

    protected abstract fun getLayoutResId(): Int

    protected fun setHasShadowAppBar(hasShadowAppBar: Boolean) {
        this.hasShadowAppBar = hasShadowAppBar
    }

    protected fun setHasToolbar(hasToolbar: Boolean) {
        this.hasToolbar = hasToolbar
    }

    protected open fun addTabs(tabLayout: TabLayout) {}

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isReattached = isReattached != null

        val root = inflater.inflate(getLayoutResId(), container, false)

        if (activity?.toolbar != null) {
            activity?.toolbar!!.visibility = if (hasToolbar) View.VISIBLE else View.GONE
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val activity = activity
        if (activity != null) {
            view?.let { activity.hideKeyboard(it) }
        }
    }

    fun isReattached() : Boolean {
        return isReattached == true
    }
}