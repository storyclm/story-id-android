package ru.breffi.storyidsample.ui.image_preview

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.r0adkll.slidr.Slidr
import com.r0adkll.slidr.model.SlidrConfig
import com.r0adkll.slidr.model.SlidrListener
import com.r0adkll.slidr.model.SlidrPosition
import kotlinx.android.synthetic.main.activity_preview_pager.*
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.ui.common.BaseInjectableActivity
import ru.breffi.storyidsample.utils.dpToPx

class ImagePreviewActivity : BaseInjectableActivity() {

    private var mConfig: SlidrConfig? = null
    var filePatch: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_pager)

        val w = window
        w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)

        val primary = ContextCompat.getColor(this, R.color.red)
        val secondary = ContextCompat.getColor(this, R.color.red)

        mConfig = SlidrConfig.Builder()
            .primaryColor(primary)
            .secondaryColor(secondary)
            .position(SlidrPosition.VERTICAL)
            .velocityThreshold(2400f)
            .scrimStartAlpha(0.8f)
            .scrimEndAlpha(0f)
            .touchSize(32F.dpToPx(this).toFloat())
            .listener(object : SlidrListener {
                override fun onSlideClosed() {
                }

                override fun onSlideStateChanged(state: Int) {
                }

                override fun onSlideChange(percent: Float) {
                    main_container.alpha = percent + 0.2f
                }

                override fun onSlideOpened() {
                }

            })
            .build()

        Slidr.attach(this, mConfig!!)

        val pagerAdapter = ImagePreviewPagerAdapter(supportFragmentManager)

        if (intent != null) {
            filePatch = intent.getStringExtra(ARGS_IMAGE_URLS)

            pagerAdapter.setData(filePatch!!)
            pager.adapter = pagerAdapter

            controls.visibility = View.GONE

        } else {
            finish()
        }


        back.setOnClickListener {
            finish()
        }

        delete.setOnClickListener {
            AlertDialog.Builder(context, R.style.DialogTheme)
                .setMessage(getString(R.string.message_remove_current_image))
                .setPositiveButton(getString(R.string.button_yes).toUpperCase()) { _, _ ->
                    val intent = Intent()
                    intent.putExtra(ARGS_DELETE_IMAGE, filePatch)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                .setNegativeButton(getString(R.string.button_cancel).toUpperCase()) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    companion object {

        const val ARGS_IMAGE_URLS = "image_urls"
        const val ARGS_DELETE_IMAGE = "deleted_image"

        fun startForResult(fragment: Fragment, imageFile: String, requestCode: Int) {
            val starter = Intent(fragment.activity, ImagePreviewActivity::class.java)
            starter.putExtra(ARGS_IMAGE_URLS, imageFile)
            fragment.startActivityForResult(starter, requestCode)
        }
    }
}
