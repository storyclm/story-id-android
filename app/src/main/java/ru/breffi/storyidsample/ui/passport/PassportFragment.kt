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
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_itn.buttonSave
import kotlinx.android.synthetic.main.fragment_passport.*
import kotlinx.android.synthetic.main.layout_doc_image.view.*
import kotlinx.android.synthetic.main.static_hint_edittext.view.*
import ru.breffi.storyid.profile.model.*
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.ui.common.glide.GlideApp
import ru.breffi.storyidsample.ui.common.model.ChangeState
import ru.breffi.storyidsample.ui.passport.model.PassportPage
import ru.breffi.storyidsample.ui.common.ImageFragment
import ru.breffi.storyidsample.ui.passport.adapter.PageAdapter
import ru.breffi.storyidsample.ui.passport.mapper.PassportMapper
import ru.breffi.storyidsample.ui.passport.model.PassportPageUiModel
import ru.breffi.storyidsample.utils.applyMask
import ru.breffi.storyidsample.utils.orNull
import ru.breffi.storyidsample.utils.setButtonEnabled
import java.io.File
import javax.inject.Inject


class PassportFragment : ImageFragment(), PageAdapter.Listener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: PassportViewModel by viewModels { viewModelFactory }

    private var profile: ProfileModel? = null
    private val changeState = ChangeState()

    private val pageAdapter = PageAdapter(this)
    private lateinit var passportMapper: PassportMapper

    companion object {

        const val IMAGE_CHANGE_TAG = "image"

        fun newInstance(): PassportFragment {
            return PassportFragment()
        }

    }

    override fun onSelectImage(file: File) {
        changeState.itemChanged(IMAGE_CHANGE_TAG, true)
        pageAdapter.setData(passportMapper.mapWithPageAdded(file))
    }

    override fun onDeleteImage(fileName: String) {
        changeState.itemChanged(IMAGE_CHANGE_TAG, true)
        pageAdapter.setData(passportMapper.mapWithPageDeleted(fileName))
    }

    override fun getTitle(context: Context, toolbarFreeWidth: Int): CharSequence {
        return getString(R.string.title_passport)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_passport
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvPassport.text.applyMask("____ ______")
        tvPassport.text.inputType = InputType.TYPE_CLASS_PHONE

        changeState.setChangeListener { buttonSave.setButtonEnabled(it) }

        buttonSave.setOnClickListener {
            profile = profile?.let {
                val passportDataModel = it.passport.passportData.copy(sn = tvPassport.getText().orNull())
                it.copy(passport = it.passport.copy(passportData = passportDataModel, pages = passportMapper.getUpdatedPages()))
            }

            viewModel.saveProfile(profile)
            changeState.reset()
            buttonSave.setButtonEnabled(false)

            activity?.setResult(Activity.RESULT_OK)
        }

        buttonSave.setButtonEnabled(false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.profile.observeFirstNonNull(viewLifecycleOwner) { profileModel ->
            profile = profileModel
            handleProfilePassport(profileModel.passport)
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
        val passportData = passport.passportData
        val passportNumber = "${passportData.sn ?: ""} ${passportData.code ?: ""}".trim()
        tvPassport.setText(passportNumber)

        passportMapper = PassportMapper(passport.pages)
        pageAdapter.setData(passportMapper.mapPages())
        pages_recycler_view.adapter = pageAdapter
        pages_recycler_view.layoutManager = GridLayoutManager(context, 2)

        tvPassport.text.addTextChangedListener {
            changeState.itemChanged("number", it.toString() != passportData.sn)
        }
    }

    override fun onImageClick(pageUiModel: PassportPageUiModel) {
        if (pageUiModel.imageFile != null) {
            deleteImage(pageUiModel.imageFile)
        } else {
            selectImage("${pageUiModel.page}.jpg")
        }
    }
}

