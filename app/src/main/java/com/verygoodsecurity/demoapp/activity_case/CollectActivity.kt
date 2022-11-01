package com.verygoodsecurity.demoapp.activity_case

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.verygoodsecurity.api.cardio.ScanActivity
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity.Companion.KEY_BUNDLE_ENVIRONMENT
import com.verygoodsecurity.demoapp.StartActivity.Companion.KEY_BUNDLE_PATH
import com.verygoodsecurity.demoapp.StartActivity.Companion.KEY_BUNDLE_VAULT_ID
import com.verygoodsecurity.demoapp.databinding.ActivityCollectBinding
import com.verygoodsecurity.demoapp.databinding.CardInputLayoutBinding
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse

class CollectActivity : AppCompatActivity(), VgsCollectResponseListener {

    private val path: String by lazy { getStringExtra(KEY_BUNDLE_PATH) }

    private val collect: VGSCollect by lazy {
        VGSCollect(
            this,
            getStringExtra(KEY_BUNDLE_VAULT_ID, ""),
            getStringExtra(KEY_BUNDLE_ENVIRONMENT, "")
        ).apply { addOnResponseListeners(this@CollectActivity) }
    }

    private lateinit var binding: ActivityCollectBinding
    private lateinit var cardBinding: CardInputLayoutBinding

    private val scanResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            collect.onActivityResult(0, it.resultCode, it.data)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectBinding.inflate(layoutInflater)
        cardBinding = CardInputLayoutBinding.bind(binding.root)
        setContentView(binding.root)
        initViews()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.scan_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.scan_card -> true.also { scan() } // TODO: @Endorf, check this line, what you think?
        else -> super.onOptionsItemSelected(item)
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        collect.onActivityResult(requestCode, resultCode, data)
        updateFilesManageButtonState()
    }

    // Should be called, so VGSCollect can properly release all resources
    override fun onDestroy() {
        super.onDestroy()
        collect.onDestroy()
    }

    // Handle VGSCollect submit responses
    override fun onResponse(response: VGSResponse?) {
        setLoading(false)
        when (response) {
            is VGSResponse.SuccessResponse -> updateResponseView(response.body)
            is VGSResponse.ErrorResponse -> showToast(response.body ?: response.localizeMessage)
            else -> throw IllegalArgumentException("Not implemented.")
        }
    }

    private fun initViews() {
        initCardView()
        binding.mbFilesManage.setOnClickListener { handleFileClickedManageButtonClicked() }
        binding.mbSubmit.setOnClickListener { submit() }
    }

    private fun initCardView() {
        cardBinding.groupAddress.visibility = View.VISIBLE
        bindViewsToCollect()
    }

    // Bind all view to VGSCollect, otherwise they not be sent to proxy
    private fun bindViewsToCollect() {
        collect.bindView(cardBinding.vgsTiedCardHolder)
        collect.bindView(cardBinding.vgsTiedCardNumber)
        collect.bindView(cardBinding.vgsTiedExpiry)
        collect.bindView(cardBinding.vgsTiedCvc)
        collect.bindView(cardBinding.vgsTiedCity)
        collect.bindView(cardBinding.vgsTiedPostalCode)
    }

    private fun handleFileClickedManageButtonClicked() {
        with(collect.getFileProvider()) {
            if (getAttachedFiles().isNotEmpty()) {
                detachAll()
            } else {
                attachFile(this@CollectActivity, "<FILE_NAME>")
            }
        }
        updateFilesManageButtonState()
    }

    // Send data to VGS proxy
    private fun submit() {
        setLoading(true)
        val request: VGSRequest = VGSRequest.VGSRequestBuilder()
            .setMethod(HTTPMethod.POST)
            .setPath(path)
            .setCustomHeader(mapOf("custom-header-name" to "value"))
            .setCustomData(mapOf("custom_data" to "value"))
            .build()
        collect.asyncSubmit(request)
    }

    // Start scanning process
    private fun scan() {
        scanResultLauncher.launch(Intent(this, ScanActivity::class.java).apply {
            putExtra(
                ScanActivity.SCAN_CONFIGURATION, hashMapOf(
                    // Provide vgs inputs field names, so scan activity can map scanning result to proper input field
                    cardBinding.vgsTiedCardHolder.getFieldName() to ScanActivity.CARD_HOLDER,
                    cardBinding.vgsTiedCardNumber.getFieldName() to ScanActivity.CARD_NUMBER,
                    cardBinding.vgsTiedExpiry.getFieldName() to ScanActivity.CARD_EXP_DATE,
                    cardBinding.vgsTiedCvc.getFieldName() to ScanActivity.CARD_CVC,
                )
            )
        })
    }

    private fun updateFilesManageButtonState() {
        binding.mbFilesManage.text =
            if (collect.getFileProvider().getAttachedFiles().isEmpty()) "Attach" else "Detach"
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.root.isEnabled = !isLoading
    }

    private fun updateResponseView(body: String?) {
        showToast(body ?: "Success")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun AppCompatActivity.getStringExtra(key: String, defaultValue: String = ""): String {
        return intent.extras?.getString(key) ?: defaultValue
    }
}