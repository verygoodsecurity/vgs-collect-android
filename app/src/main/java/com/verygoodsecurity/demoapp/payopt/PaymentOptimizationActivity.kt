package com.verygoodsecurity.demoapp.payopt

import android.animation.LayoutTransition
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity.Companion.KEY_BUNDLE_ENVIRONMENT
import com.verygoodsecurity.demoapp.StartActivity.Companion.KEY_BUNDLE_VAULT_ID
import com.verygoodsecurity.demoapp.payopt.adapter.CardsAdapter
import com.verygoodsecurity.demoapp.payopt.decorator.MarginItemDecoration
import com.verygoodsecurity.demoapp.payopt.model.Card
import com.verygoodsecurity.demoapp.payopt.source.RemoteDataSource
import com.verygoodsecurity.demoapp.payopt.source.ResponseListener
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.view.InputFieldView
import kotlinx.android.synthetic.main.activity_payment_optimization.*
import org.json.JSONException
import org.json.JSONObject

class PaymentOptimizationActivity : AppCompatActivity(R.layout.activity_payment_optimization),
    CardsAdapter.NewCardBindListener, VgsCollectResponseListener {

    private val vault by lazy { intent.extras?.getString(KEY_BUNDLE_VAULT_ID) ?: "" }
    private val environment by lazy { intent.extras?.getString(KEY_BUNDLE_ENVIRONMENT) ?: "" }
    private val source = RemoteDataSource()
    private val adapter = CardsAdapter(this)

    private var collect: VGSCollect? = null
    private var accessToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCollect()
        initViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        collect?.onDestroy()
    }

    override fun bind(inputs: Array<InputFieldView>) {
        collect?.bindView(*inputs)
    }

    override fun unbind(inputs: Array<InputFieldView>) {
        collect?.unbindView(*inputs)
    }

    override fun onResponse(response: VGSResponse?) {
        try {
            when (response) {
                is VGSResponse.SuccessResponse -> {
                    val data = JSONObject(response.body!!).getJSONObject("data")
                    val card = data.getJSONObject("card")
                    transaction(
                        Card(
                            data.getString("id"),
                            card.getString("name"),
                            card.getString("last4"),
                            card.getInt("exp_month"),
                            card.getInt("exp_year"),
                            card.getString("brand")
                        ),
                        true
                    )
                }
                is VGSResponse.ErrorResponse -> {
                    setLoading(false)
                    showSnackBar(response.localizeMessage)
                }
                else -> throw IllegalArgumentException("Not implemented")
            }
        } catch (e: JSONException) {
            setLoading(false)
            showSnackBar("Reading financial instrument response error.")
        }
    }

    private fun initCollect() {
        collect = VGSCollect(this, vault, environment)
        collect?.addOnResponseListeners(this@PaymentOptimizationActivity)
    }

    private fun initViews() {
        clRoot.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        mbPay?.setOnClickListener { pay() }
        initCards()
    }

    private fun initCards() {
        (rvCards?.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        rvCards?.addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_padding_material_medium)))
        rvCards?.adapter = adapter
    }

    private fun pay() {
        setLoading(true)
        source.fetchAccessToken(object : ResponseListener<String> {

            override fun onSuccess(data: String) {
                accessToken = data
                val card = adapter.getSelected()
                if (card == null) createFinInstrument() else transaction(card, false)
            }

            override fun onError(message: String) {
                setLoading(false)
                showSnackBar(message)
            }
        })
    }

    private fun createFinInstrument() {
        collect?.asyncSubmit(
            VGSRequest.VGSRequestBuilder()
                .setMethod(HTTPMethod.POST)
                .setPath("/financial_instruments")
                .setCustomHeader(mapOf("Authorization" to "Bearer $accessToken"))
                .build()
        )
    }

    private fun transaction(card: Card, saveCard: Boolean) {
        with(accessToken) {
            if (isNullOrEmpty()) {
                showSnackBar("Access token is null.")
                return
            }
            source.createOrder(this, object : ResponseListener<String> {

                override fun onSuccess(data: String) {
                    Log.d(TAG, "Order created, id = $data.")
                    source.createPayment(
                        this@with,
                        card.finId,
                        data,
                        object : ResponseListener<String> {

                            override fun onSuccess(data: String) {
                                if (saveCard) adapter.addItem(card)
                                setLoading(false)
                                showSnackBar("Payment successful")
                            }

                            override fun onError(message: String) {
                                setLoading(false)
                                showSnackBar("Payment failed, reason = $message")
                            }
                        })
                }

                override fun onError(message: String) {
                    setLoading(false)
                    showSnackBar(message)
                }
            })
        }
    }

    private fun showSnackBar(message: String) {
        Log.d(TAG, message)
        Snackbar.make(clRoot, message, Snackbar.LENGTH_SHORT)
            .setAnchorView(mbPay)
            .show()
    }

    private fun setLoading(isLoading: Boolean) {
        viewOverlay.isVisible = isLoading
        progressBar.isVisible = isLoading
    }

    companion object {

        private const val TAG = "PaymentOptActivity"
    }
}