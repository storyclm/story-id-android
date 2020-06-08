package ru.breffi.storyidsample.ui.auth

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.include_app_bar.*
import ru.breffi.storyid.auth.common.model.AuthState
import ru.breffi.storyidsample.ui.common.BaseInjectableActivity
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.ui.common.PositiveOrDismissDialog
import ru.breffi.storyidsample.ui.confirm_code.ConfirmCodeActivity
import ru.breffi.storyidsample.ui.common.navigation.NavigationActivity
import ru.breffi.storyidsample.ui.pin_code.PinCodeActivity
import ru.breffi.storyidsample.utils.applyMask
import ru.breffi.storyidsample.utils.hideKeyboard
import ru.breffi.storyidsample.utils.setButtonEnabled
import ru.breffi.storyidsample.utils.showErrorDialog
import ru.breffi.storyidsample.valueobject.Resource
import javax.inject.Inject

class AuthActivity : BaseInjectableActivity() {

    companion object {

        const val REQUEST_CONFIRM_CODE = 0

        fun start(context: Context) {
            val starter = Intent(context, AuthActivity::class.java)
            context.startActivity(starter)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: AuthViewModel by viewModels { viewModelFactory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        toolbar_title.text = getString(R.string.label_put_phone)

        nextButton.setButtonEnabled(false)

        phone.applyMask("+7 (___) ___-__-__")
        phone.addTextChangedListener {
            phone.setError(false)

            if (phone.text?.length == 18) {
                hideKeyboard()
                nextButton.setButtonEnabled(true)
            } else {
                nextButton.setButtonEnabled(false)
            }
        }
        phone.requestFocus()
        phone.postDelayed({ hideKeyboard() }, 100)

        nextButton.setOnClickListener {
            showProgress(true)
            viewModel.register(phone.text.toString())
        }


        viewModel.response.observe(this) { handleAuthState(it) }
    }

    private fun handleAuthState(resource: Resource<AuthState>) {
        if (resource.isSucceed) {
            showProgress(false)
            if (resource.data != null) {
                AlertDialog.Builder(context, R.style.DialogTheme)
                    .setMessage(R.string.label_verify)
                    .setPositiveButton(R.string.button_understand) { dialog, _ -> dialog.dismiss() }
                    .setOnDismissListener {
                        ConfirmCodeActivity.startForResult(this, phone.text.toString(), REQUEST_CONFIRM_CODE)
                    }
                    .show()
            }
        }

        if (resource.isError) {
            showProgress(false)
            resource.error?.showErrorDialog(this)
        }
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            progress.visibility = View.VISIBLE
            nextButton.setButtonEnabled(false)
        } else {
            progress.visibility = View.GONE
            nextButton.setButtonEnabled(true)
        }
        phone.isEnabled = !show
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CONFIRM_CODE -> {
                    if (data != null && data.getStringExtra(ConfirmCodeActivity.ARGS_NEXT_SCREEN) == ConfirmCodeActivity.ARG_SCREEN_PIN) {
                        PositiveOrDismissDialog(context, R.style.DialogTheme)
                            .setMessage(R.string.dialog_set_pin)
                            .setPositive(R.string.button_yes) {
                                openPinScreen()
                            }
                            .setDismiss(R.string.dialog_set_pin_skip) {
                                openMainScreen()
                            }
                            .show()
                    } else {
                        openMainScreen()
                    }
                }
            }
        }
    }

    private fun openMainScreen() {
        NavigationActivity.start(this, intent, true)
        finish()
    }

    private fun openPinScreen() {
        PinCodeActivity.start(this, PinCodeActivity.MODE_SET)
        finish()
    }
}
