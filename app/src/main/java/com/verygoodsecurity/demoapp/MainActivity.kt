package com.verygoodsecurity.demoapp

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.SimpleResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder

class MainActivity : AppCompatActivity(), VgsCollectResponseListener, View.OnClickListener {
    override fun onClick(v: View?) {
        progressBar?.visibility = View.VISIBLE
        when(v?.id) {
            R.id.sendPost -> vgsForm.asyncSubmit(this@MainActivity, "/post", HTTPMethod.POST)
            R.id.sendGet -> vgsForm.asyncSubmit(this@MainActivity, "/get", HTTPMethod.GET)
        }
    }

    val vgsForm = VGSCollect("tntxrsfgxcn")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sendPost?.setOnClickListener(this)
        sendGet?.setOnClickListener(this)

        vgsForm.onResponseListener = this

        vgsForm.onFieldStateChangeListener = getOnFieldStateChangeListener()

        vgsForm.bindView(cardNumberField)
        vgsForm.bindView(cardCVVField)
        vgsForm.bindView(cardHolderField)
        vgsForm.bindView(cardExpDateField)
    }
    override fun onDestroy() {
        vgsForm.onDestroy()
        super.onDestroy()
    }











    fun getOnFieldStateChangeListener():OnFieldStateChangeListener {
        return object :OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {}

            override fun onStateChange(states: Collection<FieldState>) {
                val builder = StringBuilder()
                states.forEach {
                    builder.append(it.alias).append("\n")
                        .append("   hasFocus: ").append(it.hasFocus).append("\n")
                        .append("   isValid: ").append(it.isValid).append("\n")
                        .append("   isEmpty: ").append(it.isEmpty).append("\n")
                        .append("   isRequired: ").append(it.isRequired).append("\n")
                    if(it is FieldState.CardNumberState) {
                        builder.append("    type: ").append(it.cardType).append("\n")
                            .append("       last4: ").append(it.last4).append("\n")
                            .append("       bin: ").append(it.bin).append("\n")
                    }

                    builder.append("\n")
                }
                responseView?.text = builder.toString()
            }
        }
    }


    override fun onResponse(response: SimpleResponse?) {
        progressBar?.visibility = View.INVISIBLE
        response?.let {
            responseView.text = "CODE: ${response.code} \n\n ${response.response}"
        }
    }
















    private fun setTruncateAt(textView:VGSEditText) {
        textView.setEllipsize(TextUtils.TruncateAt.START)
        textView.setMaxLines(2)
    }
    private fun setHintTextColor(textView:VGSEditText) {
        textView.setHintTextColor(Color.BLUE)
    }
    private fun canScrollHorizontally(textView:VGSEditText) {
        textView.canScrollHorizontally(true)
    }
    private fun setGravity(textView:VGSEditText) {
        textView.setGravity(Gravity.CENTER_VERTICAL or Gravity.RIGHT)
    }
    private fun setTextAppearance(textView:VGSEditText) {
        textView.setTextAppearance(android.R.style.TextAppearance_Large)

        textView.setTextAppearance(this, android.R.style.TextAppearance_Large)
    }
    private fun setFonts(textView:VGSEditText) {
        textView.setTypeface(Typeface.DEFAULT_BOLD)

//        val mtypeFace = Typeface.createFromAsset(assets, "barethos1.ttf")
//        textView.setTypeface(mtypeFace)
//
//        val typeface = ResourcesCompat.getFont(this, R.font.barethos2)!!
//        textView.setTypeface(typeface)
    }
}
