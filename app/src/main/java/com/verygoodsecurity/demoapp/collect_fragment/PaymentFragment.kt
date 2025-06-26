package com.verygoodsecurity.demoapp.collect_fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.verygoodsecurity.api.blinkcard.VGSBlinkCardIntentBuilder
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.databinding.FragmentPaymentBinding
import com.verygoodsecurity.demoapp.utils.idling.GlobalIdlingResource
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSRequest
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.CardType

class PaymentFragment : Fragment(), VgsCollectResponseListener, OnFieldStateChangeListener,
    View.OnClickListener {

    companion object {
        const val VAULT_ID = "vault_id"
        const val ENVIRONMENT = "env_type"
        const val PATH = "path"
    }

    private lateinit var vaultId: String
    private lateinit var path: String
    private lateinit var env: Environment

    private lateinit var vgsForm: VGSCollect

    private lateinit var binding: FragmentPaymentBinding

    // Used to start and receive result from scan activity
    private val scanResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            vgsForm.onActivityResult(0, it.resultCode, it.data)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        retrieveAttributes()

        vgsForm = VGSCollect(requireActivity(), vaultId, env)

        vgsForm.addOnResponseListeners(this)
        vgsForm.addOnFieldStateChangeListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
        val scanIntent = VGSBlinkCardIntentBuilder(requireActivity())
            .setCardHolderFieldName(binding.cardHolderField.getFieldName())
            .setCardNumberFieldName(binding.cardNumberField.getFieldName())
            .setExpirationDateFieldName(binding.cardExpDateField.getFieldName())
            .setCVCFieldName(binding.cardCVCField.getFieldName())
            .build()
        scanResultLauncher.launch(scanIntent)
    }

    private fun retrieveAttributes() {
        arguments?.let {
            vaultId = it.getString(VAULT_ID, "")
            path = it.getString(PATH, "/")

            val envId = it.getInt(ENVIRONMENT, 0)
            env = Environment.values()[envId]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews()
    }

    private fun initializeViews() {
        vgsForm.bindView(binding.cardNumberField)
        vgsForm.bindView(binding.cardCVCField)
        vgsForm.bindView(binding.cardHolderField)
        vgsForm.bindView(binding.cardExpDateField)

        binding.submitBtn.setOnClickListener(this)
        binding.attachBtn.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        vgsForm.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        vgsForm.onDestroy()
        super.onDestroy()
    }

    @SuppressLint("SetTextI18n")
    override fun onResponse(response: VGSResponse?) {
        setEnabledResponseHeader(true)
        setStateLoading(false)
        binding.responseContainerView.text = "CODE: ${response?.code.toString()}"
        GlobalIdlingResource.decrement()
    }

    override fun onStateChange(state: FieldState) {
        if (state is FieldState.CardNumberState) handleCardNumberState(state)
        refreshAllStates()
    }

    private fun handleCardNumberState(state: FieldState.CardNumberState) {
        binding.previewCardNumber.text = state.number
        if (state.cardBrand == CardType.VISA.name) {
            binding.previewCardBrand.setImageResource(R.drawable.ic_custom_visa)
        } else {
            binding.previewCardBrand.setImageResource(state.drawableBrandResId)
        }
    }

    private fun refreshAllStates() {
        val states = vgsForm.getAllStates()
        val builder = StringBuilder()
        states.forEach {
            builder.append(it.toString()).append("\n\n")
        }
        binding.stateContainerView.text = builder.toString()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.attachBtn -> attachFile()
            R.id.submitBtn -> submitData()
        }
    }

    private fun submitData() {
        GlobalIdlingResource.increment()
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

    private fun setEnabledResponseHeader(isEnabled: Boolean) {
        if (isEnabled) {
            binding.attachBtn.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.state_active
                )
            )
        } else {
            binding.responseContainerView.text = ""
            binding.attachBtn.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.state_unactive
                )
            )
        }
    }

    private fun setStateLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
            binding.submitBtn.isEnabled = false
            binding.attachBtn.isEnabled = false
        } else {
            binding.progressBar.visibility = View.INVISIBLE
            binding.submitBtn.isEnabled = true
            binding.attachBtn.isEnabled = true
        }
    }

    private fun attachFile() {
        if (vgsForm.getFileProvider().getAttachedFiles().isEmpty()) {
            vgsForm.getFileProvider().attachFile(requireActivity(), "attachments.file")
        } else {
            vgsForm.getFileProvider().detachAll()
        }
    }
}