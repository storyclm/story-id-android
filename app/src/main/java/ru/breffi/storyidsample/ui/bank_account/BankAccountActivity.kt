package ru.breffi.storyidsample.ui.bank_account

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.Fragment
import ru.breffi.storyidsample.R
import ru.breffi.storyidsample.ui.common.BaseInjectableActivity
import kotlinx.android.synthetic.main.include_app_bar.*

class BankAccountActivity : BaseInjectableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_activity)

        var fragment = supportFragmentManager.findFragmentById(R.id.contentFrame) as BankAccountFragment?
        if (fragment == null) {
            fragment = BankAccountFragment.newInstance(forWithDraw = intent.getBooleanExtra(ARG_FOR_WITHDRAW, false))

            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.contentFrame, fragment)
            transaction.commit()
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.title_bank_account)
    }

    companion object {

        private const val EXTRA_TYPE = "type"
        private const val ARG_FOR_WITHDRAW = "for_withdraw"

        const val NEW_BANK_ACCOUNT_REQUEST_CODE = 1

        fun startForResult(fragment: Fragment) {
            val starter = Intent(fragment.activity, BankAccountActivity::class.java)
            starter.putExtra(ARG_FOR_WITHDRAW, true)
            fragment.startActivityForResult(starter, NEW_BANK_ACCOUNT_REQUEST_CODE)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

}