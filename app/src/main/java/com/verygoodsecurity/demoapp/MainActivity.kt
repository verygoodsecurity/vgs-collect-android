package com.verygoodsecurity.demoapp

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.SimpleResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder


class MainActivity : AppCompatActivity(), VgsCollectResponseListener {

    val vgsForm = VGSCollect("tntxrsfgxcn")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendBtn.setOnClickListener {
            progressBar?.visibility = View.VISIBLE
            vgsForm.submit(this@MainActivity)
        }

        vgsForm.onResponceListener = object : VgsCollectResponseListener {
            override fun onResponse(response: SimpleResponse?) {
                progressBar?.visibility = View.INVISIBLE
                response?.let {
                    responseView.text = "CODE: ${response.code} \n\n ${response.responce}"
                    Log.d("------->", "${response.code} \n ${response.responce}")
                }
            }
        }

        vgsForm.onFieldStateChangeListener = object :OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {}

            override fun onStateChange(states: Collection<FieldState>) {
                val builder = StringBuilder()
                states.forEach {
                    builder.append(it.alias).append("\n")
                        .append("isValid: ").append(it.isValid).append("\n")
                        .append("isEmpty: ").append(it.isEmpty).append("\n")
                        .append("isRequired: ").append(it.isRequired).append("\n")
                    if(it is FieldState.CardNumberState) {
                        builder.append("last4: ").append(it.last4).append("\n")
                            .append("bin: ").append(it.bin).append("\n")
                    }

                    builder.append("\n")
                }
                responseView?.text = builder.toString()
            }
        }
//        cardNumberFieldLay.editText.add
        vgsForm.bindView(cardNumberField)
        vgsForm.bindView(cardCVVField)
        vgsForm.bindView(cardHolderField)
        vgsForm.bindView(cardExpDateField)
    }
    override fun onDestroy() {
        vgsForm.onDestroy()
        super.onDestroy()
    }


















    override fun onResponse(response: SimpleResponse?) {
        progressBar?.visibility = View.INVISIBLE
        response?.let {
            responseView.text = "CODE: ${response.code} \n\n ${response.responce}"

            Log.d("------->", "${response.code} \n ${response.responce}")
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
