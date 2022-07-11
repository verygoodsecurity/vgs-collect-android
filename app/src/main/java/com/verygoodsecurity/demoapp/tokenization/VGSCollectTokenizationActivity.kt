package com.verygoodsecurity.demoapp.tokenization

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.verygoodsecurity.demoapp.BuildConfig
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.network.tokenization.VGSTokenizationRequest
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.widget.VGSTextInputLayout
import kotlinx.android.synthetic.main.activity_collect_tokenization_demo.*
import kotlin.properties.Delegates

class VGSCollectTokenizationActivity :
    AppCompatActivity(R.layout.activity_collect_tokenization_demo),
    InputFieldView.OnTextChangedListener, VgsCollectResponseListener {

    private val defaultTextColor by lazy { ContextCompat.getColor(this, R.color.fiord) }
    private val defaultBackgroundColor by lazy { ContextCompat.getColor(this, R.color.fiord_20) }
    private val errorTextColor by lazy { ContextCompat.getColor(this, R.color.brown) }
    private val errorBackgroundColor by lazy { ContextCompat.getColor(this, R.color.vanillaIce) }

    private lateinit var collect: VGSCollect

    private var response: String? by Delegates.observable(null) { _, _, new ->
        mbReset.isVisible = !new.isNullOrBlank()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCollect()
        initViews()
    }

    override fun onTextChange(view: InputFieldView, isEmpty: Boolean) {
        val (title, layout) = (when (view.id) {
            R.id.vgsTiedCardHolder -> mtvCardHolderHint to vgsTilCardHolder
            R.id.vgsTiedCardNumber -> mtvCardNumberHint to vgsTilCardNumber
            R.id.vgsTiedExpiry -> mtvExpiryHint to vgsTilExpiry
            R.id.vgsTiedCvc -> mtvCvcHint to vgsTilCvc
            else -> throw IllegalArgumentException("Not implemented.")
        })
        setInputValid(title, layout)
    }

    override fun onResponse(response: VGSResponse?) {
        Log.d(this::class.java.simpleName, response.toString())
        setLoading(false)
        when (response) {
            is VGSResponse.SuccessResponse -> this.response = response.body
            is VGSResponse.ErrorResponse -> showSnackBar("Code: ${response.code}, ${response.localizeMessage}")
            null -> {}
        }
    }

    private fun initCollect() {
        with(intent?.extras) {
            collect = VGSCollect(
                this@VGSCollectTokenizationActivity,
                this?.getString(StartActivity.KEY_BUNDLE_VAULT_ID) ?: "",
                this?.getString(StartActivity.KEY_BUNDLE_ENVIRONMENT) ?: ""
            )
            collect.addOnResponseListeners(this@VGSCollectTokenizationActivity)
        }
    }

    private fun initViews() {
        bindViews()
        configureTokenization()
        initTextChangeListener()
        initClickListeners()
    }

    private fun bindViews() {
        collect.bindView(vgsTiedCardHolder)
        collect.bindView(vgsTiedCardNumber)
        collect.bindView(vgsTiedExpiry)
        collect.bindView(vgsTiedCvc)
    }

    private fun configureTokenization() {
        // TODO: Configure tokenization
    }

    private fun initTextChangeListener() {
        vgsTiedCardHolder.addOnTextChangeListener(this)
        vgsTiedCardNumber.addOnTextChangeListener(this)
        vgsTiedExpiry.addOnTextChangeListener(this)
        vgsTiedCvc.addOnTextChangeListener(this)
    }

    private fun initClickListeners() {
        mbTokenize.setOnClickListener {
            runIfInputsValid {
                tokenize()
            }
        }
        mcvCard.setOnClickListener { copyResponseToClipboard() }
        mbReset.setOnClickListener { resetView() }
    }

    private fun tokenize() {
        setLoading(true)
        collect.tokenize(
            VGSTokenizationRequest.VGSRequestBuilder()
                .setRouteId(BuildConfig.ROUTE_ID)
                .build()
        )
    }

    private fun copyResponseToClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("", response))
        showSnackBar("Tokenization response copied.")
    }

    private fun resetView() {
        vgsTiedCardHolder.setText(null)
        vgsTiedCardNumber.setText(null)
        vgsTiedExpiry.setText(null)
        vgsTiedCvc.setText(null)
        response = null
    }

    private fun runIfInputsValid(action: () -> Unit) {
        var isValid = true
        if (vgsTiedCardHolder.getState()?.isValid == false) {
            setInputInvalid(mtvCardHolderHint, vgsTilCardHolder)
            isValid = false
        }
        if (vgsTiedCardNumber.getState()?.isValid == false) {
            setInputInvalid(mtvCardNumberHint, vgsTilCardNumber)
            isValid = false
        }
        if (vgsTiedExpiry.getState()?.isValid == false) {
            setInputInvalid(mtvExpiryHint, vgsTilExpiry)
            isValid = false
        }
        if (vgsTiedCvc.getState()?.isValid == false) {
            setInputInvalid(mtvCvcHint, vgsTilCvc)
            isValid = false
        }
        if (isValid) action.invoke()
    }

    private fun setInputValid(title: MaterialTextView, layout: VGSTextInputLayout) {
        title.setTextColor(defaultTextColor)
        layout.setBoxBackgroundColor(defaultBackgroundColor)
    }

    private fun setInputInvalid(title: MaterialTextView, layout: VGSTextInputLayout) {
        title.setTextColor(errorTextColor)
        layout.setBoxBackgroundColor(errorBackgroundColor)
    }

    private fun setLoading(isLoading: Boolean) {
        viewOverlay.isVisible = isLoading
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).apply {
            anchorView = mbTokenize
            animationMode = Snackbar.ANIMATION_MODE_SLIDE
        }.show()
    }
}