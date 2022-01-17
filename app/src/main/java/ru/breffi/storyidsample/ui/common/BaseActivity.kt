package ru.breffi.storyidsample.ui.common

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(menuItem)
    }

}
