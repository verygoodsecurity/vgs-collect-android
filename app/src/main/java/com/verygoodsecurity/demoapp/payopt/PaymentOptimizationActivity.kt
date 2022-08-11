package com.verygoodsecurity.demoapp.payopt

import android.animation.LayoutTransition
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer
import kotlinx.android.synthetic.main.activity_payment_optimization.*

class PaymentOptimizationActivity : AppCompatActivity(R.layout.activity_payment_optimization),
    VgsCollectResponseListener {

    private var collect: VGSCollect? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCollect()
        initViews()
    }

    override fun onResponse(response: VGSResponse?) {
        try {
            when (response) {
                is VGSResponse.SuccessResponse -> transaction(response.body!!)
                is VGSResponse.ErrorResponse -> TODO("Handle instrument not created")
                else -> throw IllegalArgumentException("Not implemented")
            }
        } catch (e: NullPointerException) {
            // TODO: Handle instrument null
        }
    }

    private fun transaction(instrument: String) {

    }

    private fun initCollect() {
        with(intent?.extras) {
            collect = VGSCollect(
                this@PaymentOptimizationActivity,
                this?.getString(StartActivity.KEY_BUNDLE_VAULT_ID) ?: "",
                this?.getString(StartActivity.KEY_BUNDLE_ENVIRONMENT) ?: ""
            )
            collect?.addOnResponseListeners(this@PaymentOptimizationActivity)
        }
    }

    private fun initViews() {
        clRoot.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        mbPay?.setOnClickListener { pay() }
        initExpiry()
        bindViews()
    }

    private fun initExpiry() {
        vgsTiedExpiry?.setSerializer(
            VGSExpDateSeparateSerializer(
                "card.exp_month",
                "card.exp_year"
            )
        )
    }

    private fun bindViews() {
        collect?.bindView(vgsTiedCardHolder)
        collect?.bindView(vgsTiedCardNumber)
        collect?.bindView(vgsTiedExpiry)
        collect?.bindView(vgsTiedCvc)
    }

    private fun pay() {
        // TODO: Get token
        // TODO: Create order
        // TODO: Create instrument
        // TODO: Make payment
    }
}