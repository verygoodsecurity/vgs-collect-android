package com.verygoodsecurity.demoapp.matchers

fun withCardNumberState(str:String):WithCardNumberStateMatcher {
    return WithCardNumberStateMatcher(str)
}

fun withCardHolderState(str:String):WithCardHolderStateMatcher {
    return WithCardHolderStateMatcher(str)
}

fun withCardCVCState(str:String):WithCardCVCStateMatcher {
    return WithCardCVCStateMatcher(str)
}

fun withCardExpDateState(str:String):WithCardExpDateStateMatcher {
    return WithCardExpDateStateMatcher(str)
}