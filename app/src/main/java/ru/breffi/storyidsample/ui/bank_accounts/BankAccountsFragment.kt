package ru.breffi.storyidsample.ui.bank_accounts

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.ui.bank_account.BankAccountFragment
import ru.breffi.storyidsample.ui.common.BasePageInjectableFragment
import ru.breffi.storyidsample.valueobject.Resource
import kotlinx.android.synthetic.main.fragment_bank_accounts.*
import kotlinx.android.synthetic.main.item_bank_account.view.*
import ru.breffi.storyid.profile.model.BankAccountModel
import javax.inject.Inject

class BankAccountsFragment : BasePageInjectableFragment() {

    companion object {

        fun newInstance(): BankAccountsFragment {
            return BankAccountsFragment()
        }

    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: BankAccountsViewModel by viewModels { viewModelFactory }

    override fun getTitle(context: Context, toolbarFreeWidth: Int): CharSequence {
        return getString(R.string.title_bank_accounts)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_bank_accounts
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newCard.setOnClickListener {
            nextFragment(BankAccountFragment.newInstance())
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.bankAccounts.observe(viewLifecycleOwner) { handleBankAccountsResource(it) }
        viewModel.startLoading()
    }

    private fun handleBankAccountsResource(resource: Resource<List<BankAccountModel>>) {
        val accountsList = resource.data
        if (accountsList != null && accountsList.isNotEmpty()) {
            showAccounts(accountsList)
        }

        when (resource.status) {
            Resource.Status.LOADING -> {
                showProgress(true)
            }
            Resource.Status.SUCCESS -> {
                showProgress(false)
            }
            Resource.Status.ERROR -> {
                showProgress(false)
                resource.error?.printStackTrace()
            }
        }
    }

    private fun showAccounts(accountsList: List<BankAccountModel>) {
        cardsLayout.removeAllViews()

        for (account in accountsList) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_bank_account, cardsLayout, false)
            view.accountName.text = account.name

            view.setOnClickListener {
                nextFragment(BankAccountFragment.newInstance(account.internalId))
            }

            cardsLayout.addView(view)
        }

        cardsCard.visibility = View.VISIBLE
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            progress.visibility = View.VISIBLE
        } else {
            progress.visibility = View.GONE
        }
    }
}