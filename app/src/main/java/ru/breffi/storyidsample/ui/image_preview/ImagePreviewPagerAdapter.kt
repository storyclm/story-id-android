package ru.breffi.storyidsample.ui.image_preview

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ru.breffi.storyidsample.ui.image_preview.ImagePreviewFragment

class ImagePreviewPagerAdapter internal constructor(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var file: String? = null

    fun setData(file: String) {
        this.file = file
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return ImagePreviewFragment.newInstance(file!!)
    }

    override fun getCount(): Int {
        return 1
    }


}
