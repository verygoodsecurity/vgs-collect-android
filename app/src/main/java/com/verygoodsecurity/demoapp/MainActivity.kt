package com.verygoodsecurity.demoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.data.SimpleResponse
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

        vgsForm.onResponceListener = this

        vgsForm.bindView(cardCVVField)
        vgsForm.bindView(cardHolderField)
        vgsForm.bindView(cardNumberField)
        vgsForm.bindView(expDateField)
    }

    override fun onDestroy() {
        vgsForm.onDestroy()
        super.onDestroy()
    }

    override fun onResponse(response: SimpleResponse?) {
        progressBar?.visibility = View.INVISIBLE
        response?.let {
            responseView.text = "CODE: ${response.code} \n\n ${response.responce}"

            Log.e("------->", "${response.code} \n ${response.responce}")
        }
    }
}
