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
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), VgsCollectResponseListener {

    val vgsForm = VGSCollect("tntxrsfgxcn", Environment.SANDBOX)

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
//        cardNumberFieldLay.editText.add
        vgsForm.bindView(cardNumberField)
        vgsForm.bindView(cardCVVField)
        vgsForm.bindView(cardHolderField)
        vgsForm.bindView(cardExpDateField)

        setFonts(cardNumberField)
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
