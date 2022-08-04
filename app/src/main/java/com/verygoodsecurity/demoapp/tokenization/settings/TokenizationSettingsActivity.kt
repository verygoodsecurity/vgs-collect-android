package com.verygoodsecurity.demoapp.tokenization.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoapp.R

class TokenizationSettingsActivity : AppCompatActivity(R.layout.activity_tokenization_settings) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        showSettingsFragment()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSettingsFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fcvRoot, TokenizationSettingsFragment.create())
            .commit()
    }

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, TokenizationSettingsActivity::class.java))
        }
    }
}