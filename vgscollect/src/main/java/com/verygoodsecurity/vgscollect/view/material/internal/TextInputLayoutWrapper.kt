package com.verygoodsecurity.vgscollect.view.material.internal

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.widget.VGSTextInputLayout
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/** @suppress */
internal class TextInputLayoutWrapper @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextInputLayout(context, attrs, defStyleAttr) {

    private var bounds: Rect? = null
    private var recalculateMethod: Method? = null
    private var collapsingTextHelper: Any? = null

    companion object {
        private const val COLLAPSING_HELPER = "collapsingTextHelper"
        private const val COLLAPSED_BOUNDS = "collapsedBounds"
        private const val RECALCULATE = "recalculate"
    }

    init {
        tryInitCollapsingTextHelper()
    }

    private fun tryInitCollapsingTextHelper() {
        try {
            //Search internal and private class over object called as variable
            val cthField = TextInputLayout::class.java.getDeclaredField(COLLAPSING_HELPER)
            cthField.isAccessible = true
            collapsingTextHelper = cthField.get(this)

            //Search in private class the other component to create a view
            val boundsField = collapsingTextHelper?.javaClass?.getDeclaredField(COLLAPSED_BOUNDS)
            boundsField?.isAccessible = true
            bounds = boundsField?.get(collapsingTextHelper) as Rect
            recalculateMethod = collapsingTextHelper?.javaClass?.getDeclaredMethod(RECALCULATE)

        } catch (e: NoSuchFieldException) {
            collapsingTextHelper = null
            bounds = null
            recalculateMethod = null
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            collapsingTextHelper = null
            bounds = null
            recalculateMethod = null
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            collapsingTextHelper = null
            bounds = null
            recalculateMethod = null
            e.printStackTrace()
        }
    }

    fun isReady():Boolean {
        return editText != null
    }

    private var state: InputLayoutState? = null
    fun restoreState(state: InputLayoutState) {
        this.state = state
    }

    override fun addView(child: View?) {
        val v = handleNewChild(child)
        super.addView(v)
        if(isReady()) {
            state?.restore(this)
        }
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        val v = handleNewChild(child)
        super.addView(v, params)
    }

    override fun addView(child: View?, index: Int) {
        val v = handleNewChild(child)
        super.addView(v, index)
    }

    override fun addView(child: View?, width: Int, height: Int) {
        val v = handleNewChild(child)
        super.addView(v, width, height)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        val v = handleNewChild(child)?:child
        super.addView(v, index, params)
    }

    private  fun handleNewChild(child: View?):View? {
        return child?.run {
            when(this) {
                is BaseInputField ->  {
                    this.setIsListeningPermitted(true)
                    applyLayoutParams(this)
                    this
                }
                is InputFieldView -> this.statePreparer.let{
                    val v = it.getView()
                    return applyAndReturnDefaultLayoutParams(child, v)
                }
                is ViewGroup -> this
                else -> {
                    VGSCollectLogger.warn(VGSTextInputLayout.TAG, "${this::class.java.name} is not VGS EditText")
                    null
                }
            }
        }
    }

    private fun applyAndReturnDefaultLayoutParams(parentView: View, v: View?):View {
        if(v is TextView) {
            applyLayoutParams(v)
        }
        return v?:parentView
    }

    private fun applyLayoutParams(v: TextView?) {
        v?.apply {
            val LP = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            LP.weight = 1.0f
            LP.setMargins(0,0,0,0)
            if(LP.gravity == -1) {
                LP.gravity = Gravity.CENTER_VERTICAL
            }
            layoutParams = LP
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        adjustBounds()
    }

    private fun adjustBounds() {
        if (collapsingTextHelper == null) return

        try {
            bounds?.top = 0
            recalculateMethod?.invoke(collapsingTextHelper)
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}