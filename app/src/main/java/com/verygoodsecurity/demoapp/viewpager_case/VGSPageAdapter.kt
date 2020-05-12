package com.verygoodsecurity.demoapp.viewpager_case

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.InputFieldView

class VGSPageAdapter(
    mContext: Context,
    private val vgsForm: VGSCollect
) : RecyclerView.Adapter<VGSPageAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(mContext)

    private val layArr = arrayOf<Int>(
        R.layout.card_number_page,
        R.layout.card_holder_page,
        R.layout.card_date_cvc_page)

    private var cardNumberTextWatcher:OnFieldStateChangeListener? = null
    private var cardHoldertextWatcher:OnFieldStateChangeListener? = null
    private var cardExpDateTextWatcher:OnFieldStateChangeListener? = null
    private var cardCVCTextWatcher:OnFieldStateChangeListener? = null

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout:View = inflater.inflate(layArr[viewType], parent, false)

        layout.findViewById<InputFieldView>(R.id.cardNumberField)?.let {
            vgsForm.bindView(it)
            it.setOnFieldStateChangeListener(cardNumberTextWatcher)
        }

        layout.findViewById<InputFieldView>(R.id.cardCVCField)?.let {
            vgsForm.bindView(it)
            it.setOnFieldStateChangeListener(cardCVCTextWatcher)
        }


        layout.findViewById<InputFieldView>(R.id.cardExpDateField)?.let {
            vgsForm.bindView(it)
            it.setOnFieldStateChangeListener(cardExpDateTextWatcher)
        }


        layout.findViewById<InputFieldView>(R.id.cardHolderField)?.let {
            vgsForm.bindView(it)
            it.setOnFieldStateChangeListener(cardHoldertextWatcher)
        }

        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(layArr[position])
    }

    override fun getItemCount(): Int = layArr.size

    fun setCardHolderTextWatcher(cardHoldertextWatcher: OnFieldStateChangeListener?) {
        this.cardHoldertextWatcher = cardHoldertextWatcher
    }

    fun setCardExpDateTextWatcher(cardExpDateTextWatcher: OnFieldStateChangeListener) {
        this.cardExpDateTextWatcher = cardExpDateTextWatcher
    }

    fun setCardCVCTextWatcher(cardCVCTextWatcher: OnFieldStateChangeListener) {
        this.cardCVCTextWatcher = cardCVCTextWatcher
    }

    fun setCardNumberTextWatcher(cardNumberTextWatcher: OnFieldStateChangeListener) {
        this.cardNumberTextWatcher = cardNumberTextWatcher
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(res:Int) { }
    }

}