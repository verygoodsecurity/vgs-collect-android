package com.verygoodsecurity.demoapp.viewpager_case

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.widget.VGSTextInputLayout
import kotlinx.android.synthetic.main.activity_viewpager_collect_demo.*

class VGSViewPagerActivity : AppCompatActivity(), VgsCollectResponseListener, View.OnClickListener {

    companion object {
        const val USER_SCAN_REQUEST_CODE = 0x7
    }

    private lateinit var vault_id: String
    private lateinit var path: String
    private lateinit var env: Environment

    private lateinit var vgsForm: VGSCollect

    private lateinit var adapter: VGSPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewpager_collect_demo)

        retrieveSettings()

        initAdapter()
        setupViewPager()
        setupTopCardPreview()

        nextBtn?.setOnClickListener(this)
        backBtn?.setOnClickListener(this)

        vgsForm.addOnResponseListeners(this)

        val staticData = mutableMapOf<String, String>()
        staticData["static_data"] = "static custom data"
        vgsForm.setCustomData(staticData)
    }

    private fun setupTopCardPreview() {
        cardPreviewLayout?.setOnClickListener {
            val view = viewPager.getChildAt(viewPager.currentItem)
            val layV = view?.findViewById<VGSTextInputLayout>(R.id.cardNumberFieldLay)
            layV?.setError(null)
        }
    }

    private fun retrieveSettings() {
        val bndl = intent?.extras

        vault_id = bndl?.getString(StartActivity.VAULT_ID, "") ?: ""
        path = bndl?.getString(StartActivity.PATH, "/") ?: ""

        val envId = bndl?.getInt(StartActivity.ENVIROMENT, 0) ?: 0
        env = Environment.values()[envId]

        vgsForm = VGSCollect(this, vault_id, env)
    }

    private fun setupViewPager() {
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false
    }

    private fun initAdapter() {
        adapter = VGSPageAdapter(this, vgsForm)

        adapter.setCardNumberTextWatcher(getCardNumberTextWatcher())
        adapter.setCardHolderTextWatcher(getCardHolderTextWatcher())
        adapter.setCardExpDateTextWatcher(getCardExpDateTextWatcher())
        adapter.setCardCVCTextWatcher(getCardCVCTextWatcher())
    }

    private fun getCardNumberTextWatcher(): OnFieldStateChangeListener {
        return object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                cardValid = state.isValid
                previewCardNumber?.text = (state as FieldState.CardNumberState).number

                if (state.cardBrand == CardType.VISA.name) {
                    previewCardBrand?.setImageResource(R.drawable.ic_custom_visa)
                } else {
                    previewCardBrand?.setImageResource(state.drawableBrandResId)
                }
            }
        }
    }

    private fun getCardCVCTextWatcher(): OnFieldStateChangeListener {
        return object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                cvcValid = state.isValid
            }
        }
    }

    private fun getCardExpDateTextWatcher(): OnFieldStateChangeListener {
        return object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                cardExpDateValid = state.isValid
            }
        }
    }

    private fun getCardHolderTextWatcher(): OnFieldStateChangeListener {
        return object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                cardHolderValid = state.isValid
            }
        }
    }

    override fun onResponse(response: VGSResponse?) {
        when (response) {
            is VGSResponse.SuccessResponse -> Toast.makeText(this, "Success", Toast.LENGTH_LONG)
                .show()//responseContainerView.text = response.toString()
            is VGSResponse.ErrorResponse -> Toast.makeText(this, "Error", Toast.LENGTH_LONG)
                .show()//responseContainerView.text = response.toString()
            else -> return
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        vgsForm.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        vgsForm.onDestroy()
        super.onDestroy()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.backBtn -> turnBackPage()
            R.id.nextBtn -> turnPageOn()
        }
    }

    private fun turnBackPage() {
        val position = viewPager.currentItem - 1
        if (position < 0) {
            viewPager?.setCurrentItem(0, false)
        } else {
            viewPager?.setCurrentItem(position, true)
        }

        nextBtn?.setText("Next")
        nextBtn?.icon = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_right)

        backBtn?.visibility = if (position == 0) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    var cardValid = false
    var cvcValid = false
    var cardHolderValid = false
    var cardExpDateValid = false

    private fun turnPageOn() {
        val position = viewPager.currentItem + 1
        val isValid: Boolean = when (position) {
            1 -> cardValid
            2 -> cardHolderValid
            3 -> cvcValid && cardExpDateValid
            else -> false
        }

        if (isValid) {
            backBtn?.visibility = View.VISIBLE
            if (position == adapter.itemCount - 1) {
                nextBtn?.setText("Submit")
                nextBtn?.icon = null
            }

            when {
                position > adapter.itemCount -> viewPager?.setCurrentItem(
                    viewPager.currentItem,
                    false
                )
                position == adapter.itemCount -> submitData()
                else -> viewPager?.setCurrentItem(position, true)
            }
        }
    }

    private fun submitData() {
        vgsForm.asyncSubmit("/post", HTTPMethod.POST)
    }
}