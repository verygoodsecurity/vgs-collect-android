package com.verygoodsecurity.demoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoapp.activity_case.VGSCollectActivity
import com.verygoodsecurity.demoapp.fragment_case.VGSCollectFragmentActivity
import com.verygoodsecurity.demoapp.viewpager_case.VGSViewPagerActivity
import com.verygoodsecurity.vgscollect.core.Environment
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity:AppCompatActivity(), View.OnClickListener {

    companion object {
        const val VAULT_ID = "user_vault_id"
        const val ENVIROMENT = "user_env"
        const val PATH = "user_path"
    }

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

        startWithActivityBtn?.setOnClickListener(this)
        startWithFragmentBtn?.setOnClickListener(this)
        startWithViewPagerBtn?.setOnClickListener(this)
    }

    private fun setupUI() {
        userVault?.setText(BuildConfig.VAULT_ID)
        userPath?.setText(BuildConfig.PATH)

        environmentSpinner.setSelection(BuildConfig.ENVIRINMENT.ordinal)
    }

    private fun setupSpinner() {
        environmentSpinner.adapter = spinnerAdapter
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.startWithActivityBtn -> startInteractionWithActivity()
            R.id.startWithFragmentBtn -> startInteractionWithFragment()
            R.id.startWithViewPagerBtn -> startInteractionWithViewPager()
        }
    }

    private fun startInteractionWithActivity() {
        val intent = prepareIntent(VGSCollectActivity::class.java)
        startActivity(intent)
    }

    private fun startInteractionWithFragment() {
        val intent = prepareIntent(VGSCollectFragmentActivity::class.java)
        startActivity(intent)
    }

    private fun startInteractionWithViewPager() {
        val intent = prepareIntent(VGSViewPagerActivity::class.java)
        startActivity(intent)
    }

    private fun prepareIntent(componentClass: Class<out Activity>):Intent {
        return Intent(this, componentClass).apply {
            val vaultId = userVault.text.toString()
            val path = userPath.text.toString()
            val env = environmentSpinner.selectedItemPosition

            putExtra(VAULT_ID, vaultId)
            putExtra(ENVIROMENT, env)
            putExtra(PATH, path)
        }
    }
}