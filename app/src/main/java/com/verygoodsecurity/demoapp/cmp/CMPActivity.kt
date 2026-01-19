package com.verygoodsecurity.demoapp.cmp

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.databinding.CmpActivityBinding
import com.verygoodsecurity.demoapp.databinding.CodeExampleLayoutBinding
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.Environment
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsAuthHandler
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.util.extension.cardCVC
import com.verygoodsecurity.vgscollect.util.extension.cardExpirationDate
import com.verygoodsecurity.vgscollect.util.extension.cardNumber
import io.github.kbiakov.codeview.adapters.Options
import io.github.kbiakov.codeview.highlight.ColorThemeData
import io.github.kbiakov.codeview.highlight.Font
import io.github.kbiakov.codeview.highlight.FontCache
import io.github.kbiakov.codeview.highlight.SyntaxColors
import org.json.JSONObject

class CMPActivity : AppCompatActivity(), VgsCollectResponseListener {

    private lateinit var binding: CmpActivityBinding
    private lateinit var codeExampleBinding: CodeExampleLayoutBinding
    private lateinit var collect: VGSCollect

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = CmpActivityBinding.inflate(layoutInflater)
        codeExampleBinding = CodeExampleLayoutBinding.bind(binding.ccInputsRoot)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = bars.top)
            WindowInsetsCompat.CONSUMED
        }
        VGSCollectLogger.logLevel = VGSCollectLogger.Level.DEBUG
        setLoading(true)
        VGSCollect.init(
            context = this@CMPActivity,
            id = "<VAULT_ID>",
            environment = Environment.SANDBOX.rawValue,
            formId = "<FORM_ID>",
            authHandler = VgsAuthHandler { onComplete ->
                Handler(Looper.getMainLooper()).postDelayed({
                    onComplete("<TOKEN>")
                }, 2000)
            },
            onSuccess = {
                setLoading(false)
                initViews()
            },
            onError = { code, message ->
                setLoading(false)
                Log.d(
                    this@CMPActivity::class.simpleName,
                    "Collect init error: core = $code, message = $message"
                )
            }
        )
    }

    override fun onResponse(response: VGSResponse?) {
        setLoading(false)
        Log.d(this@CMPActivity::class.simpleName, response.toString())
        updateCodeExample(response?.body)
    }

    private fun initViews() {
        initCollectViews()
        initProceedView()
        initCodeExampleView()
        updateCodeExample(null)
        binding.ccInputsRoot.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun initCollectViews() {
        collect.cardNumber(binding.vgsTiedPan)
        collect.cardExpirationDate(binding.vgsTiedExpiry)
        collect.cardCVC(binding.vgsTiedCvc)

    }

    private fun initProceedView() {
        binding.mbProceed.setOnClickListener {
            setLoading(true)
            binding.progressBar.visibility = View.VISIBLE
            collect.createCard()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initCodeExampleView() {
        val syntaxColor = ContextCompat.getColor(this, R.color.veryLightGray)
        val bgColor = ContextCompat.getColor(this, R.color.blackPearl)
        val lineNumberColor = ContextCompat.getColor(this, R.color.nobel)
        codeExampleBinding.tvLanguage.setTypeface(
            FontCache.get(this).getTypeface(this, Font.DroidSansMonoSlashed)
        )
        codeExampleBinding.tvLanguage.text = "JSON"
        codeExampleBinding.tvLanguage.visibility = View.VISIBLE
        codeExampleBinding.cvResponse.setOptions(
            Options(
                context = this.applicationContext,
                theme = ColorThemeData(
                    SyntaxColors(
                        string = syntaxColor,
                        punctuation = syntaxColor,
                    ),
                    numColor = lineNumberColor,
                    bgContent = bgColor,
                    bgNum = bgColor,
                    noteColor = syntaxColor,
                ),
            ).withFont(Font.DroidSansMonoSlashed)
        )
        codeExampleBinding.cvResponse.alpha = 1f
        codeExampleBinding.cvResponse.findViewById<RecyclerView>(R.id.rv_code_content).isNestedScrollingEnabled =
            false
    }

    private fun updateCodeExample(response: String?) {
        val json = try {
            JSONObject(response ?: "").toString(4)
        } catch (_: Exception) {
            ""
        }
        codeExampleBinding.cvResponse.setCode(json)
    }

    private fun setLoading(isLoading: Boolean) {
        binding.viewOverlay.isVisible = isLoading
        binding.progressBar.isVisible = isLoading
    }
}