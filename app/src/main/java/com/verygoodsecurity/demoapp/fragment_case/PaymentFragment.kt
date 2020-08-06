package com.verygoodsecurity.demoapp.fragment_case

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.verygoodsecurity.api.cardio.ScanActivity
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.widget.CardVerificationCodeEditText
import com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText
import com.verygoodsecurity.vgscollect.widget.PersonNameEditText
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import kotlinx.android.synthetic.main.activity_collect_demo.*

class PaymentFragment: Fragment(), VgsCollectResponseListener, OnFieldStateChangeListener,
    View.OnClickListener {

    companion object {
        const val VAULT_ID = "vault_id"
        const val ENVIROMENT = "env_type"
        const val PATH = "path"
    }

    private lateinit var vault_id:String
    private lateinit var path:String
    private lateinit var env: Environment

    private lateinit var vgsForm: VGSCollect

    private var cardNumberField: InputFieldView? = null
    private var cardCVCField: InputFieldView? = null
    private var cardHolderField: InputFieldView? = null
    private var cardExpDateField: InputFieldView? = null

    private var responseContainerView:TextView? = null
    private var stateContainerView:TextView? = null
    private var previewCardNumber:TextView? = null
    private var previewCardBrand:ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        retrieveAttributes()

        vgsForm = VGSCollect(activity!!, vault_id, env)

        vgsForm.addOnResponseListeners(this)
        vgsForm.addOnFieldStateChangeListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.scan_card -> scanCard()
            R.id.details_item -> addDetailsFragment()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun addDetailsFragment() {
        activity?.supportFragmentManager?.beginTransaction()
            ?.addToBackStack("details")
            ?.replace(R.id.container, DetailsFragment())
            ?.commit()
    }

    private fun scanCard() {
        val intent = Intent(activity, ScanActivity::class.java)

        val scanSettings = hashMapOf<String?, Int>().apply {
            this[cardNumberField?.getFieldName()] = ScanActivity.CARD_NUMBER
            this[cardCVCField?.getFieldName()] = ScanActivity.CARD_CVC
            this[cardHolderField?.getFieldName()] = ScanActivity.CARD_HOLDER
            this[cardExpDateField?.getFieldName()] = ScanActivity.CARD_EXP_DATE
        }

        intent.putExtra(ScanActivity.SCAN_CONFIGURATION, scanSettings)

        startActivityForResult(intent,
            VGSCollectFragmentActivity.USER_SCAN_REQUEST_CODE
        )
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
        cardNumberField = view?.findViewById<VGSCardNumberEditText>(R.id.cardNumberField)
        vgsForm.bindView(cardNumberField)
        cardCVCField = view?.findViewById<CardVerificationCodeEditText>(R.id.cardCVCField)
        vgsForm.bindView(cardCVCField)
        cardHolderField = view?.findViewById<PersonNameEditText>(R.id.cardHolderField)
        vgsForm.bindView(cardHolderField)
        cardExpDateField = view?.findViewById<ExpirationDateEditText>(R.id.cardExpDateField)
        vgsForm.bindView(cardExpDateField)

        responseContainerView =  view?.findViewById(R.id.responseContainerView)
        stateContainerView =  view?.findViewById(R.id.stateContainerView)
        previewCardNumber =  view?.findViewById(R.id.previewCardNumber)
        previewCardBrand =  view?.findViewById(R.id.previewCardBrand)

        view?.findViewById<MaterialButton>(R.id.submitBtn)?.setOnClickListener(this)
        view?.findViewById<MaterialButton>(R.id.attachBtn)?.setOnClickListener(this)
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
        setEnabledResponseHeader(true)
        setStateLoading(false)

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

    override fun onClick(v: View) {
        when(v.id) {
            R.id.attachBtn -> attachFile()
            R.id.submitBtn -> submitData()
        }
    }

    private fun submitData() {
        setEnabledResponseHeader(false)
        setStateLoading(true)

        val customData = HashMap<String, Any>()
        customData["nickname"] = "Taras"

        val headers = HashMap<String, String>()
        headers["some-headers"] = "dynamic-header"

        val request: VGSRequest = VGSRequest.VGSRequestBuilder()
            .setMethod(HTTPMethod.POST)
            .setPath(path)
            .setCustomHeader(headers)
            .setCustomData(customData)
            .build()

        vgsForm.asyncSubmit(request)
    }

    private fun setEnabledResponseHeader(isEnabled:Boolean) {
        if(isEnabled) {
            attachBtn.setTextColor(
                ContextCompat.getColor(activity!!,
                R.color.state_active
            ))
        } else {
            responseContainerView?.text = ""
            attachBtn.setTextColor(
                ContextCompat.getColor(activity!!,
                R.color.state_unactive
            ))
        }
    }

    private fun setStateLoading(state:Boolean) {
        if(state) {
            progressBar?.visibility = View.VISIBLE
            submitBtn?.isEnabled = false
            attachBtn?.isEnabled = false
        } else {
            progressBar?.visibility = View.INVISIBLE
            submitBtn?.isEnabled = true
            attachBtn?.isEnabled = true
        }
    }

    private fun attachFile() {
        if(vgsForm.getFileProvider().getAttachedFiles().isEmpty()) {
            vgsForm.getFileProvider().attachFile("attachments.file")
        } else {
            vgsForm.getFileProvider().detachAll()
        }
    }
}