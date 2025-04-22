package com.verygoodsecurity.demoapp.cmp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.databinding.CmpActivityBinding
import com.verygoodsecurity.demoapp.databinding.CodeExampleLayoutBinding
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer
import io.github.kbiakov.codeview.adapters.Options
import io.github.kbiakov.codeview.highlight.ColorThemeData
import io.github.kbiakov.codeview.highlight.SyntaxColors
import org.json.JSONObject

class CMPActivity : AppCompatActivity(), VgsCollectResponseListener {

    private lateinit var binding: CmpActivityBinding
    private lateinit var codeExampleBinding: CodeExampleLayoutBinding

    private val collectCMP: VGSCollect by lazy {
        VGSCollect.initCMP(
            context = this@CMPActivity,
            environment = Environment.SANDBOX
        ).apply {
            addOnResponseListeners(this@CMPActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CmpActivityBinding.inflate(layoutInflater)
        codeExampleBinding = CodeExampleLayoutBinding.bind(binding.root)
        setContentView(binding.root)
        VGSCollectLogger.logLevel = VGSCollectLogger.Level.DEBUG
        initViews()
    }

    override fun onResponse(response: VGSResponse?) {
        binding.progressBar.visibility = View.GONE
        println(response)
        updateCodeExample(response?.body)
    }

    private fun initViews() {
        initPanView()
        initExpiryView()
        initProceedView()
        initCodeExampleView()
        updateCodeExample(null)
    }

    private fun initProceedView() {
        binding.mbProceed.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            collectCMP.setCustomHeaders(mapOf("Content-type" to "application/vnd.api+json"))
            collectCMP.createCard(accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI1RVhBTnFldTBIbUNGVHZDUGs1c21iQjNjcVo1c0FLUzNKWFRjeHdTWjY4In0.eyJleHAiOjE3NDUzMzU0NDQsImlhdCI6MTc0NTMzNDI0NCwianRpIjoiYmYwODg0ZGEtZTUwYy00ZWE2LWI4ODUtYzE2NjA1MmEyZDhlIiwiaXNzIjoiaHR0cHM6Ly9hdXRoLnZlcnlnb29kc2VjdXJpdHkuY29tL2F1dGgvcmVhbG1zL3ZncyIsImF1ZCI6WyJjYWxtLXNhbmRib3giLCJjYWxtLWxpdmUiXSwic3ViIjoiMGExNzc1M2UtMGU5My00MDQyLTlkMmQtMTMxMzUzMjA0MDY1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiQUNrNEZhbWZGLUNyZWF0ZUNhcmQtVmRTOWQiLCJyZXNvdXJjZV9hY2Nlc3MiOnsiY2FsbS1zYW5kYm94Ijp7InJvbGVzIjpbImNhcmRzOnJlYWQiLCJhY2NvdW50czp3cml0ZSIsImNhcmRzOndyaXRlIiwibmV0d29yay10b2tlbnM6d3JpdGUiLCJhY2NvdW50czpyZWFkIiwibmV0d29yay10b2tlbnM6cmVhZCIsIm1lcmNoYW50czp3cml0ZSJdfSwiY2FsbS1saXZlIjp7InJvbGVzIjpbImNhcmRzOnJlYWQiLCJhY2NvdW50czp3cml0ZSIsImNhcmRzOndyaXRlIiwibmV0d29yay10b2tlbnM6d3JpdGUiLCJhY2NvdW50czpyZWFkIiwibmV0d29yay10b2tlbnM6cmVhZCIsIm1lcmNoYW50czp3cml0ZSJdfX0sInNjb3BlIjoiYWNjb3VudHM6d3JpdGUgY2FyZHM6cmVhZCBhY2NvdW50czpyZWFkIG1lcmNoYW50czp3cml0ZSBzZXJ2aWNlLWFjY291bnQgY2FyZHM6d3JpdGUgdXNlcl9pZCIsInNlcnZpY2VfYWNjb3VudCI6dHJ1ZSwiY2xpZW50SG9zdCI6IjQ1Ljg5LjkwLjE4MiIsImNsaWVudEFubm90YXRpb25zIjp7InZncy5pby92YXVsdC1pZCI6InRudGNheGRrYm1pIn0sImNsaWVudEFkZHJlc3MiOiI0NS44OS45MC4xODIiLCJjbGllbnRfaWQiOiJBQ2s0RmFtZkYtQ3JlYXRlQ2FyZC1WZFM5ZCJ9.U8e5PG48c27hr_GVREJOfDJsDAXhBR5ZZOaknZlo1_wen_QaZOCpq_-TXEeOIEHBy3xCL-11mPpWhZWMy7B5lvFCULD41EcaPxCFIzeXPNQ1Vec-ACL5ghL0qqAd-CyAinpP18ZH2g0jkTM7GfHcz2ZRc0nXZhjIw3PX4a3tdXwjr13A9pRe5bilUZcds1TXUUMsbzaUx6xe0fFaMHHBghmBYcT2eapYVrEP4lpLJYqHCmgXT9Zjb_Lc_MK_Cu_ua5tk3gz2WTip1JMGvb1XSjiUn3gQDrapsDiaeQbVCRsdMjCgffx8s6ozO9OzJ6r2vMLBSFrgG92cLnRYhHmcEQ")
        }
    }


    private fun initPanView() {
        collectCMP.bindView(binding.vgsTiedPan)
    }

    private fun initExpiryView() {
        binding.vgsTiedExpiry.setSerializer(
            VGSExpDateSeparateSerializer(
                monthFieldName = "data.attributes.exp_month",
                yearFieldName = "data.attributes.exp_year"
            )
        )
        collectCMP.bindView(binding.vgsTiedExpiry)
    }

    private fun initCodeExampleView() {
        val syntaxColor = ContextCompat.getColor(this, R.color.veryLightGray)
        val bgColor = ContextCompat.getColor(this, R.color.blackPearl)
        val lineNumberColor = ContextCompat.getColor(this, R.color.nobel)
        codeExampleBinding.cvResponse.setOptions(
            Options(
                context = this.applicationContext, theme = ColorThemeData(
                    SyntaxColors(
                        string = syntaxColor,
                        punctuation = syntaxColor,
                    ),
                    numColor = lineNumberColor,
                    bgContent = bgColor,
                    bgNum = bgColor,
                    noteColor = syntaxColor,
                )
            )
        )
        codeExampleBinding.cvResponse.alpha = 1f
    }

    private fun updateCodeExample(response: String?) {
        val json = try {
            JSONObject(response ?: "").toString(4)
        } catch (_: Exception) {
            ""
        }
        codeExampleBinding.cvResponse.setCode(json)
    }
}