package ru.breffi.storyidsample.ui.confirm_code

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import ru.breffi.storyidsample.repository.work.ProfileSyncWorker
import ru.breffi.storyidsample.ui.common.BaseInjectableActivity
import kotlinx.android.synthetic.main.activity_confirm_code.*
import kotlinx.android.synthetic.main.include_app_bar.*
import ru.breffi.storyid.auth.common.model.AuthState
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.repository.work.BankAccountsSyncWorker
import ru.breffi.storyidsample.repository.work.FilesSyncWorker
import ru.breffi.storyidsample.utils.*
import ru.breffi.storyidsample.valueobject.Resource
import java.io.IOException
import java.util.regex.Pattern
import javax.inject.Inject

class ConfirmCodeActivity : BaseInjectableActivity() {

    companion object {

        const val CODE_PATTERN = "\\b(\\d{4})\\b"

        const val ARGS_NEXT_SCREEN = "arg_next_screen"
        const val ARG_SCREEN_PIN = "arg_screen_pin"

        private const val EXTRA_PHONE = "extra_phone"

        private const val STATE_TICK = "state_tick"
        private const val TIME_TO_WAIT = 90L

        private const val SMS_CONSENT_REQUEST = 2  // Set to an unused request code

        fun startForResult(activity: Activity, phone: String, requestCode: Int) {
            val starter = Intent(activity, ConfirmCodeActivity::class.java)
            starter.putExtra(EXTRA_PHONE, phone)
            activity.startActivityForResult(starter, requestCode)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ConfirmCodeViewModel by viewModels { viewModelFactory }

    private lateinit var phone: String

    private var timeToWaitInSeconds: Long =
        TIME_TO_WAIT

    private var tick: Long = 0
    private var timer: CountDownTimer? = null

    private val smsVerificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

                when (smsRetrieverStatus.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        // Get consent intent
                        val consentIntent =
                            extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                        try {
                            // Start activity to show consent dialog to user, activity must be started in
                            // 5 minutes, otherwise you'll receive another TIMEOUT intent
                            startActivityForResult(consentIntent,
                                SMS_CONSENT_REQUEST
                            )
                        } catch (e: ActivityNotFoundException) {
                            // Handle the exception ...
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        // Time out occurred, handle the error.
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_code)

        toolbar_title.text = getString(R.string.label_put_code)
        nextButton.setButtonEnabled(false)
        nextButton.setOnClickListener {
            viewModel.resendCode(phone)
            AlertDialog.Builder(context, R.style.DialogTheme)
                .setMessage(R.string.label_verify)
                .setPositiveButton(R.string.button_understand) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }

        phone = intent.getStringExtra(EXTRA_PHONE)

        verificationCode.requestFocus()
        verificationCode.addTextChangedListener {
            verificationCode.setError(false)

            if (verificationCode.length() == 4) {
                verificationCode.isEnabled = false

                viewModel.confirm(getCode())
            }
        }

        startResendCodeTimer(savedInstanceState)


        viewModel.confirmResponse.observe(this) { handleConfirmResource(it) }
        viewModel.resendResponse.observe(this) { handleResendResource(it) }

        val gpsVersion = PackageInfoCompat.getLongVersionCode(
            packageManager.getPackageInfo(
                GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE,
                0
            )
        )

        if (gpsVersion >= 17000000) {
            val task = SmsRetriever.getClient(context).startSmsUserConsent(null)

            task
                .addOnSuccessListener {}
                .addOnFailureListener {}

            val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
            registerReceiver(smsVerificationReceiver, intentFilter)
        }
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            progress.visibility = View.VISIBLE
            nextButton.setButtonEnabled(false)
        } else {
            progress.visibility = View.INVISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    private fun handleConfirmResource(resource: Resource<AuthState>) {
        when {
            resource.isSucceed -> {
                if (resource.data != null) {
                    showProgress(false)

                    ProfileSyncWorker.start(applicationContext)
                    BankAccountsSyncWorker.start(applicationContext)
                    FilesSyncWorker.start(applicationContext)

                    timer?.cancel()
                    hideKeyboard()

                    val intent = Intent()
                    intent.putExtra(
                        ARGS_NEXT_SCREEN,
                        ARG_SCREEN_PIN
                    )
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
            resource.isLoading -> {
                showProgress(true)
            }
            resource.isError -> {
                showProgress(false)
                //timer?.cancel()

                resource.error?.printStackTrace()

                if (resource.error is IOException) {
                    resource.error.showErrorDialog(this)
                    verificationCode.isEnabled = true
                    verificationCode.text?.clear()
                } else {
                    AlertDialog.Builder(context, R.style.DialogTheme)
                        .setMessage(
                            resource.error?.getMessage(context)
                                ?: getString(R.string.label_wrong_code)
                        )
                        .setPositiveButton(R.string.button_understand) { dialog, _ -> dialog.dismiss() }
                        .setOnDismissListener {
                            verificationCode.isEnabled = true
                            verificationCode.text?.clear()
                        }
                        .create()
                        .show()
                }
            }
        }
    }

    private fun handleResendResource(resource: Resource<AuthState>) {
        if (resource.isSucceed) {

            if (resource.data != null) {
                timeToWaitInSeconds =
                    TIME_TO_WAIT
                verificationCode.text?.clear()
                startResendCodeTimer(null)
            }

        } else if (resource.isError) {
            resource.error?.showErrorDialog(this)
        }
    }

    private fun getCode(): String {
        return verificationCode.text.toString()
    }

    private fun startResendCodeTimer(savedInstanceState: Bundle?) {
        resendCode.visibility = View.VISIBLE
        nextButton.setButtonEnabled(false)
        var startInMillis = timeToWaitInSeconds * 1000
        if (savedInstanceState != null) {
            startInMillis = savedInstanceState.getLong(STATE_TICK)
        }

        timer = object : CountDownTimer(startInMillis, 1000) {
            override fun onTick(l: Long) {
                tick = l
                if (resendCode != null) {
                    resendCode.text = getString(R.string.label_resend_code_disabled, tick / 1000)
                }
            }

            override fun onFinish() {
                resendCode.visibility = View.INVISIBLE
                nextButton.setButtonEnabled(true)
            }
        }

        (timer as CountDownTimer).start()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SMS_CONSENT_REQUEST ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)

                    if (!TextUtils.isEmpty(message)) {
                        val pattern = Pattern.compile(CODE_PATTERN)
                        val matcher = pattern.matcher(message)
                        if (matcher.find()) {
                            verificationCode.setText(matcher.group())
                        }
                    }
                } else {
                }
        }
    }

}