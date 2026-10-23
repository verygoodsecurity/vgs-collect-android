package com.verygoodsecurity.demoapp.start

import com.verygoodsecurity.demoapp.BuildConfig
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.cmp.CMPActivity
import com.verygoodsecurity.demoapp.collect.compose.CollectComposeActivity
import com.verygoodsecurity.demoapp.collect.views.CollectViewsActivity
import com.verygoodsecurity.demoapp.google_pay.GooglePayActivity
import com.verygoodsecurity.demoapp.tokenization.compose.TokenizationComposeActivity
import com.verygoodsecurity.demoapp.tokenization.v1.TokenizationActivity as TokenizationActivityV1
import com.verygoodsecurity.demoapp.tokenization.v2.TokenizationActivity as TokenizationActivityV2

object StartItems {

    val all = listOf(
        StartItem.Header(R.string.start_section_collect),
        StartItem.Flow(
            R.string.start_collect_compose_title,
            R.string.start_collect_compose_description,
            R.drawable.ic_flow_collect,
            CollectComposeActivity::class.java,
            badge = StartBadge.RECOMMENDED
        ),
        StartItem.Flow(
            R.string.start_collect_views_title,
            R.string.start_collect_views_description,
            R.drawable.ic_flow_collect,
            CollectViewsActivity::class.java,
            badge = StartBadge.DEPRECATED
        ),
        StartItem.Header(R.string.start_section_tokenization),
        StartItem.Flow(
            R.string.start_tokenization_v2_title,
            R.string.start_tokenization_v2_description,
            R.drawable.ic_flow_aliases,
            TokenizationActivityV2::class.java,
            BuildConfig.TOKENIZATION_V2_ROUTE_ID,
            StartBadge.RECOMMENDED
        ),
        StartItem.Flow(
            R.string.start_tokenization_compose_title,
            R.string.start_tokenization_compose_description,
            R.drawable.ic_flow_tokenization,
            TokenizationComposeActivity::class.java,
            BuildConfig.TOKENIZATION_ROUTE_ID
        ),
        StartItem.Flow(
            R.string.start_tokenization_title,
            R.string.start_tokenization_description,
            R.drawable.ic_flow_tokenization,
            TokenizationActivityV1::class.java,
            BuildConfig.TOKENIZATION_ROUTE_ID,
            StartBadge.DEPRECATED
        ),
        StartItem.Header(R.string.start_section_more),
        StartItem.Flow(
            R.string.start_cmp_title,
            R.string.start_cmp_description,
            R.drawable.ic_flow_cmp,
            CMPActivity::class.java,
            glyph = true
        ),
        StartItem.Flow(
            R.string.start_google_pay_title,
            R.string.start_google_pay_description,
            R.drawable.ic_flow_google_pay,
            GooglePayActivity::class.java,
            glyph = true
        ),
    )
}
