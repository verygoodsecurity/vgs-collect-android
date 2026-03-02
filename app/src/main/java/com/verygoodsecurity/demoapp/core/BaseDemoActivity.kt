package com.verygoodsecurity.demoapp.core

import android.R.id.content as contentId
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import com.verygoodsecurity.demoapp.start.StartActivity
import com.verygoodsecurity.demoapp.utils.getStringExtra
import com.verygoodsecurity.demoapp.utils.idling.GlobalIdlingResource
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import io.github.kbiakov.codeview.CodeView
import io.github.kbiakov.codeview.adapters.Options
import io.github.kbiakov.codeview.highlight.ColorThemeData
import io.github.kbiakov.codeview.highlight.Font
import io.github.kbiakov.codeview.highlight.FontCache
import io.github.kbiakov.codeview.highlight.SyntaxColors
import org.json.JSONArray
import org.json.JSONObject

abstract class BaseDemoActivity(@LayoutRes layoutId: Int) : AppCompatActivity(layoutId) {

    protected val id: String by lazy { getStringExtra(StartActivity.KEY_BUNDLE_VAULT_ID) }
    protected val path: String by lazy { getStringExtra(StartActivity.KEY_BUNDLE_PATH) }
    protected val environment: String by lazy { getStringExtra(StartActivity.KEY_BUNDLE_ENVIRONMENT) }

    private val content: ViewGroup by lazy { findViewById(contentId) }
    private val codeView: CodeView? by lazy { findViewById(com.verygoodsecurity.demoapp.R.id.codeView) }
    private val codeViewToggle: MaterialButtonToggleGroup? by lazy { findViewById(com.verygoodsecurity.demoapp.R.id.mbtgType) }
    private val progressBar: ProgressBar? by lazy { findViewById(com.verygoodsecurity.demoapp.R.id.progressBar) }

    private lateinit var touchBlockerView: View
    private lateinit var responseCodeTextView: TextView

    private val scanResultLauncher =
        registerForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            form.onActivityResult(0, it.resultCode, it.data)
        }

    private var statesCodeExample: String? = null
    private var responseCodeExample: String? = null

    abstract val form: VGSCollect

    protected abstract fun createScanIntent(): Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        applyEdgeToEdge()
        attachTouchBlockerView()
        attachResponseCodeView()
        setupCollect()
        setupCodeView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(com.verygoodsecurity.demoapp.R.menu.scan_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        com.verygoodsecurity.demoapp.R.id.scan_card -> true.also { scanResultLauncher.launch(createScanIntent()) }
        else -> super.onOptionsItemSelected(item)
    }

    protected fun setLoading(isLoading: Boolean) {
        if (isLoading) GlobalIdlingResource.increment() else GlobalIdlingResource.decrement()
        touchBlockerView.isVisible = isLoading
        progressBar?.isVisible = isLoading
    }

    private fun applyEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(content) { v, windowInsets ->
            val bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = bars.top)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun attachTouchBlockerView() {
        touchBlockerView = View(this).apply {
            setBackgroundColor(Color.TRANSPARENT)
            isClickable = true
            isFocusable = true
            visibility = View.GONE
        }
        content.addView(
            touchBlockerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun attachResponseCodeView() {
        responseCodeTextView = TextView(this).apply {
            id = com.verygoodsecurity.demoapp.R.id.tvResponseCode
            visibility = View.GONE
        }
        content.addView(
            responseCodeTextView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun setupCollect() {
        form.addOnResponseListeners(object : VgsCollectResponseListener {

            override fun onResponse(response: VGSResponse?) {
                updateCodeView(response)
                updateResponseCodeView(response)
            }
        })
        form.addOnFieldStateChangeListener(object : OnFieldStateChangeListener {

            override fun onStateChange(state: FieldState) {
                updateCodeView(form.getAllStates())
            }
        })
    }

    private fun setupCodeView() {
        codeViewToggle?.addOnButtonCheckedListener { _, _, _ -> updateCodeView() }
        setCodeViewToggle(com.verygoodsecurity.demoapp.R.id.mbStates)
        val syntaxColor = ContextCompat.getColor(this, com.verygoodsecurity.demoapp.R.color.veryLightGray)
        val bgColor = ContextCompat.getColor(this, com.verygoodsecurity.demoapp.R.color.blackPearl)
        val lineNumberColor = ContextCompat.getColor(this, com.verygoodsecurity.demoapp.R.color.nobel)
        codeView?.setOptions(
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
        codeView?.alpha = 1f
        codeView?.findViewById<RecyclerView>(com.verygoodsecurity.demoapp.R.id.rv_code_content)?.isNestedScrollingEnabled = false
        val tvLanguage = findViewById<TextView>(com.verygoodsecurity.demoapp.R.id.tvLanguage)
        tvLanguage.setTypeface(
            FontCache.get(this).getTypeface(this, Font.DroidSansMonoSlashed)
        )
        tvLanguage.text = getString(com.verygoodsecurity.demoapp.R.string.code_view_json_language_title)
        tvLanguage.visibility = View.VISIBLE
    }

    private fun updateCodeView(response: VGSResponse?) {
        responseCodeExample = try {
            JSONObject(response?.body ?: "").getJSONObject("json").toString(4)
        } catch (_: Exception) {
            (response as? VGSResponse.ErrorResponse)?.localizeMessage ?: ""
        }
        setCodeViewToggle(com.verygoodsecurity.demoapp.R.id.mbResponse)
        updateCodeView()
    }

    private fun updateCodeView(states: List<FieldState>) {
        statesCodeExample = JSONObject()
            .put(
                "states",
                JSONArray().apply {
                    states.forEach {
                        put(JSONObject().apply {
                            put("fieldName", it.fieldName)
                            put("hasFocus", it.hasFocus)
                            put("contentLength", it.contentLength)
                            put("isValid", it.isValid)
                        })
                    }
                }
            )
            .toString(4)
        updateCodeView()
    }

    private fun updateCodeView() {
        codeView?.setCode(
            if (codeViewToggle?.checkedButtonId == com.verygoodsecurity.demoapp.R.id.mbStates) {
                statesCodeExample ?: ""
            } else {
                responseCodeExample ?: ""
            }
        )
    }

    private fun setCodeViewToggle(@IdRes buttonId: Int) {
        codeViewToggle?.check(buttonId)
    }

    private fun updateResponseCodeView(response: VGSResponse?) {
        responseCodeTextView.text = response?.code?.toString()
    }
}