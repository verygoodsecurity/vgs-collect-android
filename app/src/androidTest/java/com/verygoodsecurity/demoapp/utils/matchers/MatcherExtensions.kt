package com.verygoodsecurity.demoapp.utils.matchers

fun withCardBrand(brandName: String): WithCardBrandMatcher {
    return WithCardBrandMatcher(brandName)
}

fun withCardNumberState(
    str: String? = null,
    bin: String? = null,
    last: String? = null
): WithCardNumberStateMatcher {
    return WithCardNumberStateMatcher(str, bin, last)
}

fun withCardHolderState(str: String): WithCardHolderStateMatcher {
    return WithCardHolderStateMatcher(str)
}

fun withCardCVCState(str: String): WithCardCVCStateMatcher {
    return WithCardCVCStateMatcher(str)
}

fun withCardExpDateState(str: String): WithCardExpDateStateMatcher {
    return WithCardExpDateStateMatcher(str)
}

fun withEditTextState(str: String): WithVGSEditTextStateMatcher {
    return WithVGSEditTextStateMatcher(str)
}