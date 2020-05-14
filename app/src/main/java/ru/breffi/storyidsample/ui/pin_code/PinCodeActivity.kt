package ru.breffi.storyidsample.ui.pin_code

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.breffi.storyidsample.ui.auth.AuthActivity
import ru.breffi.storyidsample.ui.common.BaseInjectableActivity
import ru.breffi.storyidsample.ui.common.navigation.NavigationActivity
import kotlinx.android.synthetic.main.activity_pin_code.*
import kotlinx.android.synthetic.main.include_app_bar.*
import ru.breffi.storyidsample.R
import javax.inject.Inject

class PinCodeActivity : BaseInjectableActivity() {

    companion object {

        const val ARGS_MODE = "args_mode"
        const val ARGS_REMOVE = "args_remove"
        const val ARGS_FROM_SETTINGS = "args_from_settings"

        const val MODE_SET = "mode_set"
        const val MODE_CHECK = "mode_check"

        const val FIRST_PIN = 0
        const val SECOND_PIN = 1

        fun start(activity: Activity, mode: String, fromSettings: Boolean = false) {
            val starter = Intent(activity, PinCodeActivity::class.java)
            starter.putExtra(ARGS_MODE, mode)
            starter.putExtra(ARGS_FROM_SETTINGS, fromSettings)
            starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity.startActivity(starter)
        }

        fun startForResult(fragment: Fragment, mode: String, forRemove: Boolean = false, fromSettings: Boolean = false, requestCode: Int) {
            val starter = Intent(fragment.activity, PinCodeActivity::class.java)
            starter.putExtra(ARGS_MODE, mode)
            starter.putExtra(ARGS_REMOVE, forRemove)
            starter.putExtra(ARGS_FROM_SETTINGS, fromSettings)
            fragment.startActivityForResult(starter, requestCode)
        }
    }

    private lateinit var firstPin: String
    private lateinit var secondPin: String
    private var step: Int =
        FIRST_PIN
    private lateinit var mode: String
    private var fromSettings: Boolean = false

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: PinCodeViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_code)
        toolbar_title.text = getString(R.string.label_set_pin)
        mode = intent.getStringExtra(ARGS_MODE)
        fromSettings = intent.getBooleanExtra(ARGS_FROM_SETTINGS, false)

        if (mode == MODE_CHECK) {
            forgetPin.text = getString(R.string.label_forget_code)
            putCodeLabel.text = getString(R.string.text_put_pin)
        } else {
            if (fromSettings) {
                forgetPin.text = getString(R.string.label_cancel)
            } else {
                forgetPin.text = getString(R.string.label_skip)
            }
            putCodeLabel.text = getString(R.string.text_set_pin)
        }

        init()

        forgetPin.setOnClickListener {
            if (mode == MODE_CHECK) {
                AuthActivity.start(this)
            } else {
                goNext()
            }
        }

        digitKeyboard.onDigitInput = {
            when (step) {
                FIRST_PIN -> {
                    if (mode == MODE_CHECK) {
                        putCodeLabel.text = getString(R.string.text_put_pin)
                    } else {
                        putCodeLabel.text = getString(R.string.text_set_pin)
                    }
                }
                SECOND_PIN -> {
                    putCodeLabel.text = getString(R.string.text_put_pin_again)
                }
            }

            input(it)
        }

        delete.setOnClickListener {
            when (step) {
                FIRST_PIN -> {
                    if (firstPin.isNotEmpty()) {
                        firstPin = firstPin.substring(0, firstPin.length - 1)
                        pinBar.rating = firstPin.length.toFloat()
                    }
                }
                SECOND_PIN -> {
                    if (secondPin.isNotEmpty()) {
                        secondPin = secondPin.substring(0, secondPin.length - 1)
                        pinBar.rating = secondPin.length.toFloat()
                    }
                }
            }
        }

    }

    private fun init() {
        firstPin = ""
        secondPin = ""
        step =
            FIRST_PIN
        pinBar.rating = 0f
    }

    private fun input(digit: Int) {
        when (step) {
            FIRST_PIN -> {
                firstPin += digit.toString()
                pinBar.rating = firstPin.length.toFloat()

                if (firstPin.length == 4) {
                    when (mode) {

                        MODE_SET -> {
                            step =
                                SECOND_PIN
                            pinBar.rating = 0f
                            putCodeLabel.text = getString(R.string.text_put_pin_again)
                        }

                        MODE_CHECK -> {
                            if (viewModel.checkPin(firstPin)) {
                                if (intent.getBooleanExtra(ARGS_REMOVE, false)) {
                                    val intent = Intent()
                                    intent.putExtra("delete", true)
                                    setResult(Activity.RESULT_OK, intent)
                                } else {
                                    NavigationActivity.start(this, intent, true)
                                }
                                finish()
                            } else {
                                putCodeLabel.text = getString(R.string.error_pin_is_incorrect)
                                init()
                            }
                        }
                    }

                }
            }
            SECOND_PIN -> {
                secondPin += digit.toString()
                pinBar.rating = secondPin.length.toFloat()

                if (secondPin.length == 4) {
                    if (firstPin == secondPin) {
                        viewModel.setPin(firstPin)
                        if (callingActivity == null) {
                            goNext()
                        } else {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    } else {
                        putCodeLabel.text = getString(R.string.error_pins_not_equals)
                        init()
                    }

                }
            }
        }
    }

    private fun goNext() {
        if (!fromSettings) {
            NavigationActivity.start(this, intent, true)
        }
        finish()
    }


}
