package com.verygoodsecurity.vgscollect.view.material.internal

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class TextInputLayoutSavedState : View.BaseSavedState {

    var error: String? = null

    constructor(superState: Parcelable?) : super(superState)

    constructor(`in`: Parcel) : super(`in`) {
        error = `in`.readString()
    }

    override fun writeToParcel(out: Parcel, flags: Int) {
        super.writeToParcel(out, flags)
        out.writeString(error)
    }

    companion object {

        @Suppress("unused")
        @JvmField
        val CREATOR = object : Parcelable.Creator<TextInputLayoutSavedState> {

            override fun createFromParcel(source: Parcel): TextInputLayoutSavedState {
                return TextInputLayoutSavedState(source)
            }

            override fun newArray(size: Int): Array<TextInputLayoutSavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}