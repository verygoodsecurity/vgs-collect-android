package com.verygoodsecurity.demoapp.collect.views

import android.content.Intent
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
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_ENVIRONMENT
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_PATH
import com.verygoodsecurity.demoapp.start.StartActivity.Companion.KEY_BUNDLE_VAULT_ID
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

    protected val id: String by lazy { getStringExtra(KEY_BUNDLE_VAULT_ID) }
    protected val path: String by lazy { getStringExtra(KEY_BUNDLE_PATH) }
    protected val environment: String by lazy { getStringExtra(KEY_BUNDLE_ENVIRONMENT) }

    private val codeView: CodeView? by lazy { findViewById(R.id.codeView) }
    private val progressBar: ProgressBar? by lazy { findViewById(R.id.progressBar) }

    private val scanResultLauncher =
        registerForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            form.onActivityResult(0, it.resultCode, it.data)
        }

    abstract val form: VGSCollect

    protected abstract fun createScanIntent(): Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        applyEdgeToEdge(findViewById<ViewGroup>(android.R.id.content).getChildAt(0))
        setupCollect()
        setupCodeView()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.scan_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.scan_card -> true.also { scanResultLauncher.launch(createScanIntent()) }
        else -> super.onOptionsItemSelected(item)
    }

    protected fun setLoading(isLoading: Boolean) {
        if (isLoading) GlobalIdlingResource.increment() else GlobalIdlingResource.decrement()
        progressBar?.isVisible = isLoading
    }

    private fun applyEdgeToEdge(root: View) {
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, windowInsets ->
            val bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = bars.top)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupCollect() {
        form.addOnResponseListeners(object : VgsCollectResponseListener {

            override fun onResponse(response: VGSResponse?) {
                updateCodeView(response)
            }
        })
        form.addOnFieldStateChangeListener(object : OnFieldStateChangeListener {

            override fun onStateChange(state: FieldState) {
                println("TEST:DD Base activity state change")
                updateCodeView(form.getAllStates())
            }
        })
    }

    private fun setupCodeView() {
        val syntaxColor = ContextCompat.getColor(this, R.color.veryLightGray)
        val bgColor = ContextCompat.getColor(this, R.color.blackPearl)
        val lineNumberColor = ContextCompat.getColor(this, R.color.nobel)
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
        codeView?.findViewById<RecyclerView>(R.id.rv_code_content)?.isNestedScrollingEnabled = false
        val tvLanguage = findViewById<TextView>(R.id.tvLanguage)
        tvLanguage.setTypeface(
            FontCache.get(this).getTypeface(this, Font.DroidSansMonoSlashed)
        )
        tvLanguage.text = "JSON"
        tvLanguage.visibility = View.VISIBLE
    }

    private fun updateCodeView(response: VGSResponse?) {
        val content = try {
            JSONObject(response?.body ?: "").getJSONObject("json").toString(4)
        } catch (_: Exception) {
            (response as? VGSResponse.ErrorResponse)?.localizeMessage ?: ""
        }
        codeView?.setCode(content)
    }

    private fun updateCodeView(states: List<FieldState>) {
        val content = JSONObject()
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
        codeView?.setCode(content)
    }
}