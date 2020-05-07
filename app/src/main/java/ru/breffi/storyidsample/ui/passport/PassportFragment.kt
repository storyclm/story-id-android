package ru.breffi.storyidsample.ui.passport

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.HorizontalScrollView
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_itn.*
import kotlinx.android.synthetic.main.fragment_itn.buttonSave
import kotlinx.android.synthetic.main.fragment_passport.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.passport
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.layout_doc_image.view.*
import kotlinx.android.synthetic.main.static_hint_edittext.view.*
import ru.breffi.storyid.profile.model.*
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.ui.common.glide.GlideApp
import ru.breffi.storyidsample.ui.image_preview.ImagePreviewActivity
import ru.breffi.storyidsample.ui.passport.model.PassportPage
import ru.breffi.storyidsample.utils.ImageFragment
import ru.breffi.storyidsample.utils.applyMask
import java.io.File
import javax.inject.Inject


class PassportFragment : ImageFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: PassportViewModel by viewModels { viewModelFactory }

    var profile: ProfileModel? = null

    companion object {

        fun newInstance(): PassportFragment {
            return PassportFragment()
        }

    }

    override fun onSelectImage(file: File) {
        viewModel.setImage(file)
    }

    override fun onDeleteImage(fileName: String) {
        viewModel.deleteImage(fileName)
    }

    override fun getTitle(context: Context, toolbarFreeWidth: Int): CharSequence {
        return getString(R.string.title_passport)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_passport
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPassport.text.applyMask("____ ______")
        tvPassport.text.inputType = InputType.TYPE_CLASS_PHONE

        addPassport.setOnClickListener {
            selectImage(viewModel.getPassportFileName())
        }

        buttonSave.setOnClickListener {
            profile = profile?.let {
                val passportModel = it.passport.copy(sn = tvPassport.getText())
                val map = viewModel.passportPageImages.value ?: mutableMapOf()
                val pages = passportModel.pages.map { pageModel -> pageModel.copy(file = map[pageModel.page]?.imageFile) }
                it.copy(passport = passportModel.copy(pages = pages))
            }

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
            profileModel?.let { handleProfilePassport(it.passport) }
        }

        viewModel.passportPageImages.observe(viewLifecycleOwner) { files ->
            if (files.isNotEmpty()) {
                setPassportFiles(files)
            } else {
                if (pasImgLayout.childCount > 1) {
                    pasImgLayout.removeViewAt(0)
                }
            }
        }

        viewModel.start()
    }

    private fun setPassportFiles(passportPages: MutableMap<Int, PassportPage>) {
        context?.let { context ->
            val isAdd = pasImgLayout.childCount - 1 < passportPages.size

            updatePassportPages(passportPages)

            for (passportPage in passportPages.values) {

                when (passportPage.action) {
                    PassportPage.ADD -> {
                        if (pasImgLayout.children.findLast { it.tag == passportPage.page } == null) {
                            val passportView =
                                LayoutInflater.from(context).inflate(R.layout.layout_doc_image, pasImgLayout, false)
                            passportView.tag = passportPage.page
                            passportPage.imageFile?.let { file ->
                                passportView.setOnClickListener {
                                    deleteImage(file)
                                }
                                GlideApp.with(context)
                                    .load(file.path)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(passportView.image)
                            } ?: run {
                                GlideApp.with(context)
                                    .load(R.drawable.ic_launcher_background)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(passportView.image)
                            }
                            pasImgLayout.addView(passportView, 0)
                        }
                    }
                    PassportPage.CHANGE -> {
                        pasImgLayout.children.findLast { it.tag == passportPage.page }?.let {
                            GlideApp.with(context)
                                .load(passportPage.imageFile)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(it.image)

                            it.setOnClickListener {
                                passportPage.imageFile?.let {
                                    deleteImage(it)
                                }
                            }
                        }
                    }
                    PassportPage.REMOVE -> {
                        pasImgLayout.children.findLast { it.tag == passportPage.page }?.let {
                            pasImgLayout.removeView(it)
                        }
                    }
                }

            }

            if (isAdd) {
                pasImgLayout.post {
                    pasScrollLayout.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
                }
            }

            if (pasImgLayout.childCount >= 3) {
                addPassport.visibility = View.GONE
            } else {
                addPassport.visibility = View.VISIBLE
            }
        }
    }

    private fun updatePassportPages(pages: Map<Int, PassportPage>) {
        profile = profile?.let {
            val pageModels = pages.map { pageEntry ->
                PassportPageModel(page = pageEntry.value.page, file = pageEntry.value.imageFile)
            }
            val passportModel = it.passport.copy(pages = pageModels)
            it.copy(passport = passportModel)
        }
    }

    private fun handleProfilePassport(passport: PassportModel) {
        val passportNumber = "${passport.sn ?: ""} ${passport.code ?: ""}".trim()
        tvPassport.setText(passportNumber)

        viewModel.setPassportImages(passport.pages)
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

