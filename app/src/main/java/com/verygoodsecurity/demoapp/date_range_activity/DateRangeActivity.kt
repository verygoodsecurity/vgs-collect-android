package com.verygoodsecurity.demoapp.date_range_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoapp.databinding.ActivityDateRangeBinding

class DateRangeActivity : AppCompatActivity() {

    // TODO: WIP
    //, VgsCollectResponseListener, OnFieldStateChangeListener {
    // private val path: String by lazy { getStringExtra(StartActivity.KEY_BUNDLE_PATH) }
    //    private val collect: VGSCollect by lazy {
    //        VGSCollect(
    //            this,
    //            getStringExtra(StartActivity.KEY_BUNDLE_VAULT_ID, ""),
    //            getStringExtra(StartActivity.KEY_BUNDLE_ENVIRONMENT, "")
    //        ).apply {
    //            addOnResponseListeners(this@DateRangeActivity)
    //            addOnFieldStateChangeListener(this@DateRangeActivity)
    //        }
    //    }

    private lateinit var binding: ActivityDateRangeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDateRangeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: WIP
        // initViews()
    }

    // TODO: WIP
    //private fun AppCompatActivity.getStringExtra(key: String, defaultValue: String = ""): String {
    //  return intent.extras?.getString(key) ?: defaultValue
    //}
}