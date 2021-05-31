package ru.breffi.storyidsample.ui.personal_data

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_personal_data.*
import kotlinx.android.synthetic.main.fragment_personal_data.buttonSave
import kotlinx.android.synthetic.main.static_hint_edittext.view.*
import ru.breffi.storyid.profile.model.*
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.ui.common.BasePageInjectableFragment
import ru.breffi.storyidsample.ui.common.model.ChangeMonitor
import ru.breffi.storyidsample.utils.applyMask
import ru.breffi.storyidsample.utils.orNull
import ru.breffi.storyidsample.utils.setButtonEnabled
import ru.breffi.storyidsample.utils.validateEmail
import javax.inject.Inject


class PersonalDataFragment : BasePageInjectableFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: PersonalDataViewModel by viewModels { viewModelFactory }

    private var profile: ProfileModel? = null
    private val changeMonitor = ChangeMonitor()

    companion object {

        fun newInstance(): PersonalDataFragment {
            return PersonalDataFragment()
        }

    }

    override fun getTitle(context: Context, toolbarFreeWidth: Int): CharSequence {
        return getString(R.string.title_personal_data)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_personal_data
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phone.text.applyMask("+7 (___) ___-__-__")
//        phone.text.isEnabled = false
//        phone.text.isFocusable = false

        changeMonitor.setChangeListener { buttonSave.setButtonEnabled(it) }

        email.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (!email.getText().validateEmail()) {
                    email.setError(true)
                }
            }
        }

        email.text.addTextChangedListener {
            email.setError(false)
        }

        buttonSave.setOnClickListener {

            profile = profile?.let {
                val demographicsModel = it.demographics
                    .copy(
                        name = name.getText(),
                        surname = surname.getText(),
                        patronymic = patronymic.getText()
                    )
                it.copy(demographics = demographicsModel)
            }

            name.wasChanged = false
            surname.wasChanged = false
            patronymic.wasChanged = false

            val mail = email.getText().trim()
            if (mail.validateEmail()) {
                profile = profile?.let {
                    val profileId = it.profileId.copy(email = mail.orNull(), emailVerified = false, phone = phone.getText().getDigits())
                    it.copy(profileId = profileId)
                }

                email.wasChanged = false
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

        viewModel.profile.observeFirstNonNull(viewLifecycleOwner) { handleProfileResource(it) }
    }

    private fun handleProfileResource(profile: ProfileModel) {
        this.profile = profile
        val profileId = profile.profileId
        phone.setText(profileId.phone)
        email.setText(profileId.email)

        phone.text.addTextChangedListener {
            changeMonitor.itemChanged("phone", it.toString() != profileId.phone)
        }
        email.text.addTextChangedListener {
            changeMonitor.itemChanged("email", it.toString() != profileId.email)
        }

        handleProfileDemographicsResource(profile.demographics)
    }

    private fun handleProfileDemographicsResource(profileDemographics: DemographicsModel) {
        name.setText(profileDemographics.name)
        surname.setText(profileDemographics.surname)
        patronymic.setText(profileDemographics.patronymic)

        name.text.addTextChangedListener {
            changeMonitor.itemChanged("name", it.toString() != profileDemographics.name)
        }
        surname.text.addTextChangedListener {
            changeMonitor.itemChanged("surname", it.toString() != profileDemographics.surname)
        }
        patronymic.text.addTextChangedListener {
            changeMonitor.itemChanged("patronymic", it.toString() != profileDemographics.patronymic)
        }
    }

    fun String.getDigits(): String {
        var s = this
        return if (s.length > 1) {
            s = s.replace("+7", "7")
            s.getOnlyNumeric()
        } else {
            s.replace("+", "")
        }
    }

    private fun String.getOnlyNumeric(): String {
        return replace("[^\\d]".toRegex(), "")
    }
}

