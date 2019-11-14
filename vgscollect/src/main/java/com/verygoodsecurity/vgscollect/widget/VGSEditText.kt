package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.text.validation.card.VGSTextInputType

class VGSEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : InputFieldView(context, attrs, defStyleAttr) {

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.VGSEditText,
            0, 0
        ).apply {

            try {
                val type = getInteger(R.styleable.VGSEditText_fieldType, -1)
                val aliasName = getString(R.styleable.VGSEditText_aliasName)
                val hint = getString(R.styleable.VGSEditText_hint)
                val textSize = getDimension(R.styleable.VGSEditText_textSize, 0f)
                val textColor = getColor(R.styleable.VGSEditText_textColor, 0)
                val text = getString(R.styleable.VGSEditText_text)
                val textStyle = getInt(R.styleable.VGSEditText_textStyle, -1)
                val cursorVisible = getBoolean(R.styleable.VGSEditText_cursorVisible, true)
                val isRequired = getBoolean(R.styleable.VGSEditText_isRequired, true)
                val singleLine = getBoolean(R.styleable.VGSEditText_singleLine, true)
                val scrollHorizontally = getBoolean(R.styleable.VGSEditText_scrollHorizontally, true)
                val gravity = getInt(R.styleable.VGSEditText_gravity, 0)
                val ellipsize = getInt(R.styleable.VGSEditText_ellipsize, 0)

                val minLines = getInt(R.styleable.VGSEditText_minLines, 0)
                val maxLines = getInt(R.styleable.VGSEditText_maxLines, 0)

                setAliasName(aliasName)
                setFieldType(type)
                setHint(hint)
                setTextColor(textColor)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                setText(text)
                setCursorVisible(cursorVisible)
                setGravity(gravity)
                canScrollHorizontally(scrollHorizontally)
                setEllipsize(ellipsize)
                setMaxLines(maxLines)
                setMinLines(minLines)
                setSingleLine(singleLine)
                setIsRequired(isRequired)
                setTypeface(getTypeface(), textStyle)
            } finally {
                recycle()
            }
        }
    }

    override fun setAliasName(alias:String?) {
        super.setAliasName(alias)
    }

    override fun addView(child: View?, index: Int) {
        super.addView(child, index)
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        super.addView(child, params)
    }

    override fun addView(child: View?, width: Int, height: Int) {
        super.addView(child, width, height)
    }

    override fun setEllipsize(type: Int) {
        super.setEllipsize(type)
    }

    override fun setEllipsize(ellipsis: TextUtils.TruncateAt) {
        super.setEllipsize(ellipsis)
    }

    override fun setMinLines(lines:Int) {
        super.setMinLines(lines)
    }

    override fun setMaxLines(lines:Int) {
        super.setMaxLines(lines)
    }

    override fun setSingleLine(singleLine:Boolean) {
        super.setSingleLine(singleLine)
    }

    override fun setFocusable(focusable: Boolean) {
        super.setFocusable(focusable)
    }

    override fun setFocusableInTouchMode(focusableInTouchMode: Boolean) {
        super.setFocusableInTouchMode(focusableInTouchMode)
    }

    override fun isEnabled():Boolean {
        return super.isEnabled()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
    }

    override fun setHint(text:String?) {
        super.setHint(text)
    }

    override fun setHintTextColor(colors: ColorStateList) {
        super.setHintTextColor(colors)
    }

    override fun setHintTextColor(color:Int) {
        super.setHintTextColor(color)
    }

    override fun canScrollHorizontally(canScroll:Boolean) {
        super.canScrollHorizontally(canScroll)
    }

    override fun getGravity(): Int {
        return super.getGravity()
    }

    override fun setGravity(gravity:Int) {
        super.setGravity(gravity)
    }

    override fun setCursorVisible(isVisible:Boolean) {
        super.setCursorVisible(isVisible)
    }

    override fun setTextAppearance( context: Context, resId:Int) {
        super.setTextAppearance(context, resId)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun setTextAppearance(resId:Int) {
        super.setTextAppearance(resId)
    }

    override fun getTypeface():Typeface {
        return super.getTypeface()
    }

    override fun setTypeface(typeface: Typeface) {
        super.setTypeface(typeface)
    }

    override fun setTypeface( tf:Typeface, style:Int) {
        super.setTypeface(tf, style)
    }

    override fun setText( resId:Int) {
        super.setText(resId)
    }

    override fun setText( resId:Int, type: TextView.BufferType) {
        super.setText(resId, type)
    }

    override fun setText(text:CharSequence?) {
        super.setText(text)
    }

    override fun setText( text:CharSequence?, type: TextView.BufferType) {
        super.setText(text, type)
    }

    override fun setTextSize( size:Float ) {
        super.setTextSize(size)
    }

    override fun setTextSize( unit:Int, size:Float) {
        super.setTextSize(unit, size)
    }

    override fun setTextColor(color:Int) {
        super.setTextColor(color)
    }

    override fun setIsRequired(state:Boolean) {
        super.setIsRequired(state)
    }

    override fun setFieldType(type:VGSTextInputType) {
        super.setFieldType(type)
    }

    override fun setFieldType(type:Int) {
        super.setFieldType(type)
    }
}