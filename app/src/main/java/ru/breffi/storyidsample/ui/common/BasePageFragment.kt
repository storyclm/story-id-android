package ru.breffi.storyidsample.ui.common

import android.content.Context
import androidx.fragment.app.Fragment

abstract class BasePageFragment : BaseFragment() {

    interface FragmentListener {

        fun onToTopFragment()

        fun onFragmentResumed(fragment: BasePageFragment)

        fun onNextFragment(fragment: BasePageFragment)

        fun onBackFragment(count: Int)

        fun onSwapFragment(fragment: Fragment)

        fun setTitle(title: CharSequence)

    }

    private var listener: FragmentListener? = null

    abstract fun getTitle(context: Context, toolbarFreeWidth: Int): CharSequence

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = activity as FragmentListener
        } catch (e: ClassCastException) {
        }
    }

    override fun onResume() {
        super.onResume()
        listener?.onFragmentResumed(this)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun toTopFragment() {
        listener?.onToTopFragment()
    }

    fun nextFragment(fragment: BasePageFragment) {
        listener?.onNextFragment(fragment)
    }

    fun swapFragment(fragment: BasePageFragment) {
        listener?.onSwapFragment(fragment)
    }

    fun backFragment() {
        backFragment(1)
    }

    private fun backFragment(count: Int) {
        listener?.onBackFragment(count)
    }

    fun setTitle(title: CharSequence) {
        listener?.setTitle(title)
    }
}