package com.verygoodsecurity.demoapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.vgscollect.core.Environment
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity:AppCompatActivity() {

    private val spinnerAdapter:ArrayAdapter<String> by lazy {
        val envArr = arrayOf(
            Environment.SANDBOX.rawValue.toUpperCase(),
            Environment.LIVE.rawValue.toUpperCase()
        )
        val layout = android.R.layout.simple_spinner_item
        val spinnerArrayAdapter = ArrayAdapter<String>(this, layout, envArr)

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerArrayAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        setupSpinner()
        setupUI()

        startBtn?.setOnClickListener { startInteraction() }
    }

    private fun setupUI() {
        userVault?.setText(R.string.vault_id)
        userPath?.setText(R.string.endpoint)

        environmentSpinner.setSelection(BuildConfig.ENVIRINMENT.ordinal)
    }

    private fun setupSpinner() {
        environmentSpinner.adapter = spinnerAdapter
    }

    private fun startInteraction() {
        val vaultId = userVault.text.toString()
        val path = userPath.text.toString()
        val env = environmentSpinner.selectedItemPosition

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.VAULT_ID, vaultId)
        intent.putExtra(MainActivity.ENVIROMENT, env)
        intent.putExtra(MainActivity.PATH, path)

        startActivity(intent)
    }
}