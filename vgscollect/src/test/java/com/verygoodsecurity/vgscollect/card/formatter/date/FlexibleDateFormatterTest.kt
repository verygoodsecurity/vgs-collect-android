package com.verygoodsecurity.vgscollect.card.formatter.date

import android.text.Editable
import android.text.TextWatcher
import com.verygoodsecurity.vgscollect.view.card.formatter.Formatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.FlexibleDateFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.StrictDateFormatter
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class FlexibleDateFormatterTest {

    companion object {
        private const val TEST_POSITIVE_VALUE_1 =  "03/01"
        private const val TEST_POSITIVE_VALUE_2 =  "99/9999"
        private const val TEST_POSITIVE_VALUE_3 =  "12-2034"
    }

    private lateinit var formatter: Formatter
    private lateinit var textWatcher: TextWatcher

    @Before
    fun setupFormatter() {
        with(FlexibleDateFormatter()) {
            formatter = this
            textWatcher = this
        }
    }

    @Test
    fun test_set_mask() {
        val c = StrictDateFormatter()
        Assert.assertEquals("##/####", c.getMask())
        c.setMask("yyyy MM")
        Assert.assertEquals("#### ##", c.getMask())
        c.setMask("yyyy mm")
        Assert.assertEquals("#### ##", c.getMask())
        c.setMask("mm-yy")
        Assert.assertEquals("##-##", c.getMask())
        c.setMask("yy/mm")
        Assert.assertEquals("##/##", c.getMask())
    }

    @Test
    fun test_default_text_full() {
        textWatcher.onTextChanged(TEST_POSITIVE_VALUE_2, 0,0,7)

        val e = Mockito.mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        Mockito.verify(e).replace(0, 0,
            TEST_POSITIVE_VALUE_2
        )
    }

    @Test
    fun test_default_set_mask_text_full() {
        formatter.setMask("MM-yyyy")

        textWatcher.onTextChanged(TEST_POSITIVE_VALUE_3, 0,0,7)

        val e = Mockito.mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        Mockito.verify(e).replace(0, 0,
            TEST_POSITIVE_VALUE_3
        )
    }

    @Test
    fun test_set_default_short() {
        formatter.setMask("MM/yy")

        textWatcher.onTextChanged(TEST_POSITIVE_VALUE_1, 0,0,5)

        val e = Mockito.mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        Mockito.verify(e).replace(0, 0,
            TEST_POSITIVE_VALUE_1
        )
    }

}