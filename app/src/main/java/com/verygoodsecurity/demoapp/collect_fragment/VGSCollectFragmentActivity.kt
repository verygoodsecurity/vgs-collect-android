package com.verygoodsecurity.demoapp.collect_fragment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity

class VGSCollectFragmentActivity: AppCompatActivity() {

    companion object {
        const val USER_SCAN_REQUEST_CODE = 0x8
    }

    private lateinit var vaultId:String
    private lateinit var path:String
    private var envId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fragment_collect_demo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, windowInsets ->
            val bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = bars.top)
            WindowInsetsCompat.CONSUMED
        }

        retrieveSettings()

        attachMainFragment()
    }

    private fun attachMainFragment() {
        val paymentFragment = PaymentFragment().apply {
            val bndl = Bundle()
            bndl.putString(PaymentFragment.VAULT_ID, vaultId)
            bndl.putString(PaymentFragment.PATH, path)
            bndl.putString(PaymentFragment.VAULT_ID, vaultId)

            arguments = bndl
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.container, paymentFragment)
            .commit()
    }

    private fun retrieveSettings() {
        val bndl = intent?.extras

        vaultId = bndl?.getString(StartActivity.KEY_BUNDLE_VAULT_ID, "")?:""
        path = bndl?.getString(StartActivity.KEY_BUNDLE_PATH,"/")?:""

        envId = bndl?.getInt(StartActivity.KEY_BUNDLE_ENVIRONMENT, 0)?:0
    }
}