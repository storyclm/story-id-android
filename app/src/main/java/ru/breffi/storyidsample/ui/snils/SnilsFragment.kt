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
import kotlinx.android.synthetic.main.fragment_snils.buttonSave
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


class SnilsFragment : ImageFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: SnilsViewModel by viewModels { viewModelFactory }

    private var profile: ProfileModel? = null
    private val changeMonitor = ChangeMonitor()

    companion object {

        const val SNILS_TMP_FILE = "tmp_snils.jpg"

        fun newInstance(): SnilsFragment {
            return SnilsFragment()
        }

    }

    override fun onSelectImage(file: File) {
        changeMonitor.itemChanged(SNILS_TMP_FILE, true)
        viewModel.setSnilsImage(file)
    }

    override fun onDeleteImage(fileName: String) {
        changeMonitor.itemChanged(SNILS_TMP_FILE, true)
        viewModel.deleteImage()
    }

    override fun getTitle(context: Context, toolbarFreeWidth: Int): CharSequence {
        return getString(R.string.title_snils)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_snils
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvSnils.text.applyMask("___-___-___-__")
        tvSnils.text.inputType = InputType.TYPE_CLASS_PHONE

        changeMonitor.setChangeListener { buttonSave.setButtonEnabled(it) }

        addSnils.setOnClickListener {
            selectImage(SNILS_TMP_FILE)
        }

        buttonSave.setOnClickListener {
            profile = profile?.let {
                val snilsModel = it.snils.copy(snils = tvSnils.getText().orNull(), file = viewModel.imageSnils.value)
                it.copy(snils = snilsModel)
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
            handleProfileSnils(profileModel.snils)
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
            changeMonitor.itemChanged("number", it.toString() != snils.snils)
        }
    }
}

