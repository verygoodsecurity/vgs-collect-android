package com.verygoodsecurity.demoapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import com.verygoodsecurity.vgscollect.widget.VGSTextInputLauout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val el = VGSTextInputLauout(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                marginEnd = resources.getDimension(R.dimen.default_margin).toInt()
                marginStart = resources.getDimension(R.dimen.default_margin).toInt()
            }
            setHint("account number")
        }

        val et = VGSEditText(this).apply {
            this.setTextColor(Color.BLUE)
            this.setText("number")
        }
        el.addView(et)

        findViewById<ViewGroup>(R.id.parentPanel).addView(el)
    }
}
