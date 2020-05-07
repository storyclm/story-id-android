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
import kotlinx.android.synthetic.main.static_hint_edittext.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import ru.breffi.storyid.profile.model.*
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.ui.common.BasePageInjectableFragment
import ru.breffi.storyidsample.utils.applyMask
import ru.breffi.storyidsample.utils.validateEmail
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class PersonalDataFragment : BasePageInjectableFragment(), CoroutineScope {

    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: PersonalDataViewModel by viewModels { viewModelFactory }

    var profile: ProfileModel? = null


    companion object {

        fun newInstance(): PersonalDataFragment {
            val fragment = PersonalDataFragment()
            return fragment
        }

    }

    override fun getTitle(context: Context, toolbarFreeWidth: Int): CharSequence {
        return getString(R.string.title_personal_data)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_personal_data
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mJob = Job()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        phone.text.applyMask("+7 (___) ___-__-__")
        phone.text.isEnabled = false
        phone.text.isFocusable = false

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
            if (needSaveDemographics()) {
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
            }

            if (needSaveProfile()) {
                val mail = email.getText().trim()
                if (mail.validateEmail()) {
                    profile = profile?.copy(email = mail, emailVerified = false)

                    email.wasChanged = false
                }
            }

            viewModel.saveProfile(profile)
            setButtonEnabled(false)

            activity?.setResult(Activity.RESULT_OK)
        }

        setButtonEnabled(false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.profile.observe(viewLifecycleOwner) { handleProfileResource(it) }
        viewModel.start()
    }

    private fun handleProfileResource(profile: ProfileModel?) {
        if (profile != null) {
            this.profile = profile

            phone.setText(profile.phone)
            email.setText(profile.email)

            handleProfileDemographicsResource(profile.demographics)
        }

        email.text.addTextChangedListener {
            setButtonEnabled(true)
        }
    }

    private fun handleProfileDemographicsResource(profileDemographics: DemographicsModel) {
        name.setText(profileDemographics.name)
        surname.setText(profileDemographics.surname)
        patronymic.setText(profileDemographics.patronymic)

        name.text.addTextChangedListener {
            setButtonEnabled(true)
        }
        surname.text.addTextChangedListener {
            setButtonEnabled(true)
        }
        patronymic.text.addTextChangedListener {
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

    private fun needSaveProfile(): Boolean {
        return email.wasChanged
    }

    private fun needSaveDemographics(): Boolean {
        return name.wasChanged || surname.wasChanged || patronymic.wasChanged
    }

    override fun onDestroy() {
        super.onDestroy()
        mJob.cancel()
    }
}

