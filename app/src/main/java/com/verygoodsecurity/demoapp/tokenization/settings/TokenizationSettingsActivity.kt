package com.verygoodsecurity.demoapp.tokenization.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.verygoodsecurity.demoapp.R

class TokenizationSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tokenization_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fcvRoot)) { v, windowInsets ->
            val bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = bars.top)
            WindowInsetsCompat.CONSUMED
        }
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