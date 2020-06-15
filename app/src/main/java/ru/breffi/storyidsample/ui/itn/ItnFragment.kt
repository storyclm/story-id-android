package ru.breffi.storyidsample.ui.itn

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_itn.*
import kotlinx.android.synthetic.main.layout_doc_image.view.*
import kotlinx.android.synthetic.main.static_hint_edittext.view.*
import ru.breffi.storyid.profile.model.*
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.ui.common.glide.GlideApp
import ru.breffi.storyidsample.ui.common.model.ChangeMonitor
import ru.breffi.storyidsample.ui.common.ImageFragment
import ru.breffi.storyidsample.utils.applyMask
import ru.breffi.storyidsample.utils.orNull
import ru.breffi.storyidsample.utils.setButtonEnabled
import java.io.File
import javax.inject.Inject


class ItnFragment : ImageFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ItnViewModel by viewModels { viewModelFactory }

    private var profile: ProfileModel? = null
    private val changeMonitor = ChangeMonitor()

    companion object {

        const val ITN_TMP_FILE = "tmp_itn.jpg"

        fun newInstance(): ItnFragment {
            return ItnFragment()
        }

    }

    override fun onSelectImage(file: File) {
        changeMonitor.itemChanged(ITN_TMP_FILE, true)
        viewModel.setItnImage(file)
    }

    override fun onDeleteImage(fileName: String) {
        changeMonitor.itemChanged(ITN_TMP_FILE, true)
        viewModel.deleteImage()
    }

    override fun getTitle(context: Context, toolbarFreeWidth: Int): CharSequence {
        return getString(R.string.title_itn)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_itn
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvItn.text.applyMask("____________")
        tvItn.text.inputType = InputType.TYPE_CLASS_PHONE

        changeMonitor.setChangeListener { buttonSave.setButtonEnabled(it) }

        addItn.setOnClickListener {
            selectImage(ITN_TMP_FILE)
        }

        buttonSave.setOnClickListener {
            profile = profile?.let {
                val itnModel = it.itn.copy(itn = tvItn.getText().orNull(), file = viewModel.imageItn.value)
                it.copy(itn = itnModel)
            }

            viewModel.saveProfile(profile)
            changeMonitor.reset()
            buttonSave.setButtonEnabled(false)

            activity?.setResult(Activity.RESULT_OK)
        }
        buttonSave.setButtonEnabled(false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.profile.observeFirstNonNull(viewLifecycleOwner) { profileModel ->
            profile = profileModel
            handleProfileItn(profileModel.itn)
        }

        viewModel.imageItn.observe(viewLifecycleOwner) { file ->
            if (file != null && file.exists()) {
                setItnImage(file)
            } else {
                if (itnImgLayout.childCount > 1) {
                    addItn.visibility = View.VISIBLE
                    itnImgLayout.removeViewAt(0)
                }
            }
        }
    }

    private fun setItnImage(image: File) {
        context?.let {
            if (itnImgLayout.childCount > 1) {
                itnImgLayout.removeViewAt(0)
            }

            val itnView = LayoutInflater.from(context).inflate(R.layout.layout_doc_image, itnImgLayout, false)

            itnView.setOnClickListener {
                deleteImage(image)
            }

            GlideApp.with(it)
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(itnView.image)

            itnImgLayout.addView(itnView, 0)

            if (itnImgLayout.childCount >= 2) {
                addItn.visibility = View.GONE
            }
        }
    }

    private fun handleProfileItn(itn: ItnModel) {
        tvItn.setText(itn.itn)

        itn.file?.let {
            viewModel.setItnImage(it)
        }

        tvItn.text.addTextChangedListener {
            changeMonitor.itemChanged("number", it.toString() != itn.itn)
        }
    }
}

