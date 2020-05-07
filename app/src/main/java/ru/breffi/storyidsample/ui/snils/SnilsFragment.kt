package ru.breffi.storyidsample.ui.snils

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
import kotlinx.android.synthetic.main.fragment_snils.*
import kotlinx.android.synthetic.main.layout_doc_image.view.*
import kotlinx.android.synthetic.main.static_hint_edittext.view.*
import ru.breffi.storyid.profile.model.*
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.ui.common.glide.GlideApp
import ru.breffi.storyidsample.utils.ImageFragment
import ru.breffi.storyidsample.utils.applyMask
import java.io.File
import javax.inject.Inject


class SnilsFragment : ImageFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: SnilsViewModel by viewModels { viewModelFactory }

    var profile: ProfileModel? = null

    companion object {

        const val SNILS_TMP_FILE = "tmp_snils.jpg"

        fun newInstance(): SnilsFragment {
            return SnilsFragment()
        }

    }

    override fun onSelectImage(file: File) {
        viewModel.setSnilsImage(file)
    }

    override fun onDeleteImage(fileName: String) {
        viewModel.deleteImage()
    }

    override fun getTitle(context: Context, toolbarFreeWidth: Int): CharSequence {
        return getString(R.string.title_snils)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_snils
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvSnils.text.applyMask("___-___-___-__")
        tvSnils.text.inputType = InputType.TYPE_CLASS_PHONE

        addSnils.setOnClickListener {
            selectImage(SNILS_TMP_FILE)
        }

        buttonSave.setOnClickListener {
            profile = profile?.let {
                val snilsModel = it.snils.copy(snils = tvSnils.getText(), file = viewModel.imageSnils.value)
                it.copy(snils = snilsModel)
            }

            tvSnils.wasChanged = false

            viewModel.saveProfile(profile)
            setButtonEnabled(false)

            activity?.setResult(Activity.RESULT_OK)
        }

        setButtonEnabled(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.profile.observe(viewLifecycleOwner) { profileModel ->
            profile = profileModel
            profileModel?.let { handleProfileSnils(it.snils) }
        }

        viewModel.imageSnils.observe(viewLifecycleOwner) { file ->
            if (file != null && file.exists()) {
                setSnilsImage(file)
            } else {
                if (snilsImgLayout.childCount > 1) {
                    addSnils.visibility = View.VISIBLE
                    snilsImgLayout.removeViewAt(0)
                }
            }
        }

        viewModel.start()
    }

    private fun setSnilsImage(image: File) {
        context?.let {
            if (snilsImgLayout.childCount > 1) {
                snilsImgLayout.removeViewAt(0)
            }

            val snilsView = LayoutInflater.from(context).inflate(R.layout.layout_doc_image, snilsImgLayout, false)

            snilsView.setOnClickListener {
                deleteImage(image)
            }

            GlideApp.with(it)
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(snilsView.image)

            snilsImgLayout.addView(snilsView, 0)

            if (snilsImgLayout.childCount >= 2) {
                addSnils.visibility = View.GONE
            }
        }
    }

    private fun handleProfileSnils(snils: SnilsModel) {
        tvSnils.setText(snils.snils)

        snils.file?.let {
            viewModel.setSnilsImage(it)
        }

        tvSnils.text.addTextChangedListener {
            setButtonEnabled(true)
        }
    }

    private fun setButtonEnabled(enabled: Boolean) {
        if (enabled) {
            buttonSave.alpha = 1f
        } else {
            buttonSave.alpha = 0.4f
        }
        buttonSave.isEnabled = enabled
    }
}

