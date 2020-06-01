package ru.breffi.storyidsample.ui.personal_data

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.fragment_personal_data.*
import kotlinx.android.synthetic.main.fragment_personal_data.buttonSave
import kotlinx.android.synthetic.main.static_hint_edittext.view.*
import ru.breffi.storyid.profile.model.*
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.ui.common.BasePageInjectableFragment
import ru.breffi.storyidsample.ui.common.model.ChangeState
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
    private val changeState = ChangeState()

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
        phone.text.isEnabled = false
        phone.text.isFocusable = false

        changeState.setChangeListener { buttonSave.setButtonEnabled(it) }

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
                profile = profile?.copy(email = mail.orNull(), emailVerified = false)

                email.wasChanged = false
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

        viewModel.profile.observeFirstNonNull(viewLifecycleOwner) { handleProfileResource(it) }
    }

    private fun handleProfileResource(profile: ProfileModel) {
        this.profile = profile

        phone.setText(profile.phone)
        email.setText(profile.email)

        phone.text.addTextChangedListener {
            changeState.itemChanged("phone", it.toString() != profile.phone)
        }
        email.text.addTextChangedListener {
            changeState.itemChanged("email", it.toString() != profile.email)
        }

        handleProfileDemographicsResource(profile.demographics)
    }

    private fun handleProfileDemographicsResource(profileDemographics: DemographicsModel) {
        name.setText(profileDemographics.name)
        surname.setText(profileDemographics.surname)
        patronymic.setText(profileDemographics.patronymic)

        name.text.addTextChangedListener {
            changeState.itemChanged("name", it.toString() != profileDemographics.name)
        }
        surname.text.addTextChangedListener {
            changeState.itemChanged("surname", it.toString() != profileDemographics.surname)
        }
        patronymic.text.addTextChangedListener {
            changeState.itemChanged("patronymic", it.toString() != profileDemographics.patronymic)
        }
    }
}

