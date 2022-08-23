package com.verygoodsecurity.demoapp.payopt

import android.animation.LayoutTransition
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import com.google.android.material.snackbar.Snackbar
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity
import com.verygoodsecurity.demoapp.payopt.adapter.Card
import com.verygoodsecurity.demoapp.payopt.adapter.CardsAdapter
import com.verygoodsecurity.demoapp.payopt.decorator.MarginItemDecoration
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer
import kotlinx.android.synthetic.main.activity_payment_optimization.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class PaymentOptimizationActivity : AppCompatActivity(R.layout.activity_payment_optimization),
    VgsCollectResponseListener {

    private var collect: VGSCollect? = null
    private var token: String? = null

    private lateinit var vaultId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCollect()
        initViews()
    }

    override fun onResponse(response: VGSResponse?) {
        try {
            when (response) {
                is VGSResponse.SuccessResponse -> {
                    val id = JSONObject(response.body!!).getJSONObject("data").getString("id")
                    Log.d(TAG, "Instrument id = $id")
                    transaction(id)
                }
                is VGSResponse.ErrorResponse -> TODO("Handle instrument not created")
                else -> throw IllegalArgumentException("Not implemented")
            }
        } catch (e: JSONException) {
            // TODO: Handle instrument null
        }
    }

    private fun initCollect() {
        with(intent?.extras) {
            vaultId = this?.getString(StartActivity.KEY_BUNDLE_VAULT_ID) ?: ""
            collect = VGSCollect(
                this@PaymentOptimizationActivity,
                vaultId,
                this?.getString(StartActivity.KEY_BUNDLE_ENVIRONMENT) ?: ""
            )
            collect?.addOnResponseListeners(this@PaymentOptimizationActivity)
        }
    }

    private fun initViews() {
        clRoot.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        mbPay?.setOnClickListener { pay() }
        initCards()
//        initExpiry()
//        bindViews()
    }

    private fun initCards() {
        rvCards?.addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_padding_material_medium)))
        rvCards?.adapter = CardsAdapter().also {
//            it.submitList(emptyList())
            it.submitList(listOf(Card("1", "Test", "4111", 11, 12, "VISA")))
        }
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
        setLoading(true)
        getAccessToken {
            Log.d(TAG, "Token = $it")
            token = it
            collect?.asyncSubmit(
                VGSRequest.VGSRequestBuilder()
                    .setMethod(HTTPMethod.POST)
                    .setPath("/financial_instruments")
                    .setCustomHeader(mapOf("Authorization" to "Bearer $it"))
                    .build()
            )
        }
    }

    private fun getAccessToken(onSuccess: (token: String) -> Unit) {
        Fuel.post("https://multiplexing-demo.verygoodsecurity.io/get-auth-token")
            .responseString { _, _, result ->
                try {
                    when (result) {
                        is Result.Success -> onSuccess.invoke(JSONObject(result.get()).getString("access_token"))
                        is Result.Failure -> handleError(result.error.toString())
                    }
                } catch (e: Exception) {
                    handleError(e.toString())
                }
            }
    }

    private fun createOrder(
        @Suppress("SameParameterValue") amount: Int,
        token: String,
        onSuccess: (id: String) -> Unit
    ) {
        val body = JSONObject().apply {
            put("order", JSONObject().apply {
                put("amount", amount)
                put("currency", "USD")
                put("financial_instrument_types", JSONArray().apply {
                    put("card")
                })
            })
            put("items", JSONArray().apply {
                put(JSONObject().apply {
                    put("name", "Test")
                    put("price", amount)
                    put("quantity", 1)
                })
            })
        }.toString()

        Fuel.post("https://multiplexing-demo.verygoodsecurity.io/orders")
            .body(body)
            .header("X-Auth-Token", token)
            .header("Content-Type", "application/json")
            .responseString { _, _, result ->
                try {
                    when (result) {
                        is Result.Success -> onSuccess.invoke(
                            JSONObject(result.get()).getJSONObject("data").getString("id")
                        )
                        is Result.Failure -> handleError(result.error.toString())
                    }
                } catch (e: Exception) {
                    handleError(e.toString())
                }
            }
    }

    private fun transaction(instrumentId: String) {
        with(token) {
            if (isNullOrEmpty()) {
                Log.d(TAG, "Access token is null.")
                setLoading(false)
                return
            }
            createOrder(DEFAULT_AMOUNT, this) { id ->
                Log.d(TAG, "Order id = $id")
                makeTransaction(this, instrumentId, id) { response ->
                    Log.d(TAG, "Transfer response = $response")
                    Snackbar.make(clRoot, "Payment successful", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun makeTransaction(
        token: String,
        instrumentId: String,
        orderId: String,
        onSuccess: (response: String?) -> Unit
    ) {
        val body = JSONObject().apply {
            put("order_id", orderId)
            put("source", instrumentId)
        }.toString()

        Fuel.post("https://$vaultId-4880868f-d88b-4333-ab70-d9deecdbffc4.sandbox.verygoodproxy.com/transfers")
            .body(body)
            .header("Authorization", "Bearer $token")
            .header("Content-Type", "application/json")
            .responseString { _, _, result ->
                setLoading(false)
                try {
                    when (result) {
                        is Result.Success -> onSuccess.invoke(result.get())
                        is Result.Failure -> handleError(result.error.toString())
                    }
                } catch (e: Exception) {
                    handleError(e.toString())
                }
            }
    }

    private fun handleError(message: String) {
        Snackbar.make(clRoot, message, Snackbar.LENGTH_SHORT).show()
        Log.d(TAG, message)
        setLoading(false)
    }

    private fun setLoading(isLoading: Boolean) {
        Handler(Looper.getMainLooper()).postDelayed({
            viewOverlay.isVisible = isLoading
            progressBar.isVisible = isLoading
        }, 200)
    }

    companion object {

        private const val TAG = "PaymentOptActivity"

        private const val DEFAULT_AMOUNT = 50
    }
}