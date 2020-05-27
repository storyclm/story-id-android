package ru.breffi.storyidsample.ui.bank_account

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.repository.work.BankAccountsSyncWorker
import ru.breffi.storyidsample.ui.common.BasePageInjectableFragment
import ru.breffi.storyidsample.utils.applyMask
import ru.breffi.storyidsample.utils.showErrorDialog
import ru.breffi.storyidsample.valueobject.Resource
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_bank_account.*
import kotlinx.android.synthetic.main.static_hint_edittext.view.*
import ru.breffi.storyid.profile.model.BankAccountModel
import ru.breffi.storyid.profile.model.CreateBankAccountModel
import javax.inject.Inject

class BankAccountFragment : BasePageInjectableFragment() {

    companion object {

        private const val KEY_FIELDS = "fields"

        private const val ARG_ID = "arg_id"
        private const val ARG_FOR_WITHDRAW = "arg_for_withdraw"

        fun newInstance(accountId: String? = null, forWithDraw: Boolean = false): BankAccountFragment {
            val args = Bundle()
            if (accountId != null) {
                args.putString(ARG_ID, accountId)
            }
            args.putBoolean(ARG_FOR_WITHDRAW, forWithDraw)
            val fragment = BankAccountFragment()
            fragment.arguments = args
            return fragment
        }

    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: BankAccountViewModel by viewModels { viewModelFactory }

    private lateinit var account: BankAccountModel

    private var savedFieldData: FieldData? = null

    override fun getTitle(context: Context, toolbarFreeWidth: Int): CharSequence {
        return if (arguments?.getString(ARG_ID) == null) {
            getString(R.string.title_bank_account)
        } else {
            ""
        }

    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_bank_account
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bic.text.inputType = InputType.TYPE_CLASS_PHONE
        bic.text.applyMask("_________")

        settlementAccount.text.inputType = InputType.TYPE_CLASS_PHONE
        settlementAccount.text.applyMask("___-__-___-_-____-_______")

        correspondentAccount.text.inputType = InputType.TYPE_CLASS_PHONE
        correspondentAccount.text.applyMask("___-__-___-_-____-_______")

        if (arguments?.getBoolean(ARG_FOR_WITHDRAW) == true) {
            buttonSave.text = "Вывести средства"
        } else {
            if (arguments?.getString(ARG_ID) != null) {
                buttonSave.text = getString(R.string.button_save_changes)
                setButtonEnabled(false)
            } else {
                buttonSave.text = getString(R.string.button_bind_card)
            }
        }

        buttonSave.setOnClickListener {
            if (checkFormValid()) {
                if (arguments?.getString(ARG_ID) != null) {
                    viewModel.bankAccountUpdateRequest.postValue(
                        BankAccountModel(
                            internalId = account.internalId,
                            name = accountName.getText(),
                            description = "",
                            settlementAccount = settlementAccount.getText(),
                            bank = bankName.getText(),
                            bic = bic.getText(),
                            correspondentAccount = correspondentAccount.getText()
                        )
                    )
                } else {
                    viewModel.bankAccountCreateRequest.postValue(
                        CreateBankAccountModel(
                            name = accountName.getText(),
                            description = "",
                            settlementAccount = settlementAccount.getText(),
                            bank = bankName.getText(),
                            bic = bic.getText(),
                            correspondentAccount = correspondentAccount.getText()
                        )
                    )
                }
            } else {
                AlertDialog.Builder(context, R.style.DialogTheme)
                    .setMessage(getString(R.string.error_form_not_valid))
                    .setPositiveButton(R.string.button_ok) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.bankAccountResponse.observe(viewLifecycleOwner) { handleBankAccountResource(it) }

        viewModel.bankAccount.observe(viewLifecycleOwner) { handleBankAccount(it) }

        if (arguments?.getString(ARG_ID) != null) {
            viewModel.bankAccountId.postValue(arguments?.getString(ARG_ID))
        }

        bic.text.addTextChangedListener { setButtonEnabled(true) }
        bankName.text.addTextChangedListener { setButtonEnabled(true) }
        settlementAccount.text.addTextChangedListener { setButtonEnabled(true) }
        correspondentAccount.text.addTextChangedListener { setButtonEnabled(true) }
        accountName.text.addTextChangedListener { setButtonEnabled(true) }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        savedInstanceState?.let {
            savedFieldData = it.getParcelable(KEY_FIELDS)
        }

        savedFieldData?.let {
            bic.setText(it.bic)
            bankName.setText(it.bank)
            correspondentAccount.setText(it.correspondent)
            settlementAccount.setText(it.account)
            accountName.setText(it.name)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (!isDetached) {
            createFieldData()
            outState.putParcelable(KEY_FIELDS, savedFieldData)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        createFieldData()
    }

    private fun createFieldData() {
        savedFieldData = FieldData(
            bic = bic.getText(),
            bank = bankName.getText(),
            correspondent = correspondentAccount.getText(),
            account = settlementAccount.getText(),
            name = accountName.getText()
        )
    }

    private fun handleBankAccount(account: BankAccountModel?) {
        if (account != null) {
            setTitle(account.name)
            this.account = account

            bic.setText(account.bic)
            bankName.setText(account.bank)
            settlementAccount.setText(account.settlementAccount)
            correspondentAccount.setText(account.correspondentAccount)
            accountName.setText(account.name)
        }
    }

    private fun handleBankAccountResource(resource: Resource<BankAccountModel>) {
        when (resource.status) {
            Resource.Status.LOADING -> {
                showProgress(true)
            }
            Resource.Status.SUCCESS -> {
                showProgress(false)
                if (resource.data != null) {
                    BankAccountsSyncWorker.start(requireContext())
                    if (arguments?.getBoolean(ARG_FOR_WITHDRAW) == true) {
                        val intent = Intent()
                        intent.putExtra("id", resource.data.internalId)
                        activity?.setResult(Activity.RESULT_OK, intent)
                        activity?.finish()
                    } else {
                        backFragment()
                    }
                }
            }
            Resource.Status.ERROR -> {
                showProgress(false)
                if (arguments?.getBoolean(ARG_FOR_WITHDRAW) == true) {
                    activity?.let {
                        resource.error?.showErrorDialog(it)
                    }
                } else {
                    if (resource.data != null) {
                        backFragment()
                    }
                }
            }
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

    private fun checkFormValid(): Boolean {
        var valid = true

        if (bic.isEmpty() || bic.text.length() < 9) {
            valid = false
        }

        if (bankName.isEmpty()) {
            valid = false
        }

        if (correspondentAccount.isEmpty() || correspondentAccount.text.length() < 20) {
            valid = false
        }

        if (settlementAccount.isEmpty() || settlementAccount.text.length() < 24) {
            valid = false
        }

        if (accountName.isEmpty()) {
            valid = false
        }

        return valid
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            progress.visibility = View.VISIBLE
            buttonSave.text = ""
        } else {
            progress.visibility = View.GONE

            if (arguments?.getBoolean(ARG_FOR_WITHDRAW) == true) {
                buttonSave.text = "Вывести средства"
            } else {
                if (arguments?.getString(ARG_ID) != null) {
                    buttonSave.text = getString(R.string.button_save_changes)
                    setButtonEnabled(false)
                } else {
                    buttonSave.text = getString(R.string.button_bind_card)
                }
            }
        }
        buttonSave.isEnabled = !show
    }

    @Parcelize
    data class FieldData(val bic: String, val bank: String, val correspondent: String, val account: String, val name: String) :
        Parcelable
}