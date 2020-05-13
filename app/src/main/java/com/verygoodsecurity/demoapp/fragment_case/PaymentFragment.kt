package com.verygoodsecurity.demoapp.fragment_case

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.widget.CardVerificationCodeEditText
import com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText
import com.verygoodsecurity.vgscollect.widget.PersonNameEditText
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText

class PaymentFragment: Fragment(), VgsCollectResponseListener, OnFieldStateChangeListener {

    companion object {
        const val VAULT_ID = "vault_id"
        const val ENVIROMENT = "env_type"
        const val PATH = "path"
    }

    private lateinit var vault_id:String
    private lateinit var path:String
    private lateinit var env: Environment

    private lateinit var vgsForm: VGSCollect

    private var responseContainerView:TextView? = null
    private var stateContainerView:TextView? = null
    private var previewCardNumber:TextView? = null
    private var previewCardBrand:ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retrieveAttributes()

        vgsForm = VGSCollect(activity!!, vault_id, env)
        vgsForm.addOnResponseListeners(this)
        vgsForm.addOnFieldStateChangeListener(this)

    }

    private fun retrieveAttributes() {
        arguments?.let {
            vault_id = it.getString(VAULT_ID, "")
            path = it.getString(PATH,"/")

            val envId = it.getInt(ENVIROMENT, 0)
            env = Environment.values()[envId]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews()
    }

    private fun initializeViews() {
        val cardNumberField = view?.findViewById<VGSCardNumberEditText>(R.id.cardNumberField)
        vgsForm.bindView(cardNumberField)
        val cardCVCField = view?.findViewById<CardVerificationCodeEditText>(R.id.cardCVCField)
        vgsForm.bindView(cardCVCField)
        val cardHolderField = view?.findViewById<PersonNameEditText>(R.id.cardHolderField)
        vgsForm.bindView(cardHolderField)
        val cardExpDateField = view?.findViewById<ExpirationDateEditText>(R.id.cardExpDateField)
        vgsForm.bindView(cardExpDateField)

        responseContainerView =  view?.findViewById(R.id.responseContainerView)
        stateContainerView =  view?.findViewById(R.id.stateContainerView)
        previewCardNumber =  view?.findViewById(R.id.previewCardNumber)
        previewCardBrand =  view?.findViewById(R.id.previewCardBrand)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        vgsForm.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        vgsForm.onDestroy()
        super.onDestroy()
    }

    override fun onResponse(response: VGSResponse?) {
        when (response) {
            is VGSResponse.SuccessResponse -> responseContainerView?.text = response.toString()
            is VGSResponse.ErrorResponse -> responseContainerView?.text = response.toString()
        }
    }

    override fun onStateChange(state: FieldState) {
        when(state) {
            is FieldState.CardNumberState -> handleCardNumberState(state)
        }
        refreshAllStates()
    }

    private fun handleCardNumberState(state: FieldState.CardNumberState) {
        previewCardNumber?.text = state.number
        if(state.cardBrand == CardType.VISA.name) {
            previewCardBrand?.setImageResource(R.drawable.ic_custom_visa)
        } else {
            previewCardBrand?.setImageResource(state.drawableBrandResId)
        }
    }

    private fun refreshAllStates() {
        val states = vgsForm.getAllStates()
        val builder = StringBuilder()
        states.forEach {
            builder.append(it.toString()).append("\n\n")
        }
        stateContainerView?.text = builder.toString()
    }
}