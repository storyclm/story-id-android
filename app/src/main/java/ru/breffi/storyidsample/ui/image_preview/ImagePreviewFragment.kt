package ru.breffi.storyidsample.ui.image_preview

import android.net.Uri
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_image_preview.*
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.ui.common.BaseFragment


class ImagePreviewFragment : BaseFragment() {

    override fun getLayoutResId(): Int {
        return R.layout.fragment_image_preview
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasToolbar(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            val file = arguments?.getString(ARGS_IMAGE_URL)

            try {
                mBigImage.showImage(Uri.parse(file))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {

        const val ARGS_IMAGE_URL = "image_url"

        fun newInstance(file: String): ImagePreviewFragment {
            val bundle = Bundle()
            bundle.putString(ARGS_IMAGE_URL, file)
            val fragment = ImagePreviewFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}