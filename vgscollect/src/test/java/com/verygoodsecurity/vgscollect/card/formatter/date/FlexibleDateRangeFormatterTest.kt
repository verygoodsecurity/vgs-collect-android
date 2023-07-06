package com.verygoodsecurity.vgscollect.card.formatter.date

import android.text.Editable
import android.text.TextWatcher
import com.verygoodsecurity.vgscollect.view.card.formatter.Formatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.FlexibleDateRangeFormatter
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class FlexibleDateRangeFormatterTest {

    companion object {
        private const val TEST_POSITIVE_VALUE_1 =  "99/99/9999"
        private const val TEST_POSITIVE_VALUE_2 =  "9999/99/99"
    }

    private lateinit var formatter: Formatter
    private lateinit var textWatcher: TextWatcher

    @Before
    fun setupFormatter() {
        with(FlexibleDateRangeFormatter()) {
            formatter = this
            textWatcher = this
        }
    }

    @Test
    fun test_set_mask() {
        val c = FlexibleDateRangeFormatter()
        Assert.assertEquals("##/##/####", c.getMask())
        c.setMask("MM/dd/yyyy")
        Assert.assertEquals("##/##/####", c.getMask())
        c.setMask("MM dd yyyy")
        Assert.assertEquals("##/##/####", c.getMask())
        c.setMask("MM-dd-yyyy")
        Assert.assertEquals("##/##/####", c.getMask())
        c.setMask("dd/MM/yyyy")
        Assert.assertEquals("##/##/####", c.getMask())
        c.setMask("dd MM yyyy")
        Assert.assertEquals("##/##/####", c.getMask())
        c.setMask("dd-MM-yyyy")
        Assert.assertEquals("##/##/####", c.getMask())
        c.setMask("yyyy/MM/dd")
        Assert.assertEquals("####/##/##", c.getMask())
        c.setMask("yyyy MM dd")
        Assert.assertEquals("####/##/##", c.getMask())
        c.setMask("yyyy-MM-dd")
        Assert.assertEquals("####/##/##", c.getMask())
        c.setMask("yy/mm/dd")
        Assert.assertEquals("##/##/####", c.getMask())
    }

    @Test
    fun test_default_text_mm_dd_yyyy() {
        formatter.setMask("MM/dd/yyyy")

        textWatcher.onTextChanged(TEST_POSITIVE_VALUE_1, 0, 0, 10)

        val e = Mockito.mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        Mockito.verify(e).replace(0, 0,
            TEST_POSITIVE_VALUE_1
        )
    }

    @Test
    fun test_default_text_dd_mm_yyyy() {
        formatter.setMask("dd/MM/yyyy")

        textWatcher.onTextChanged(TEST_POSITIVE_VALUE_1, 0, 0, 10)

        val e = Mockito.mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        Mockito.verify(e).replace(0, 0, TEST_POSITIVE_VALUE_1)
    }

    @Test
    fun test_default_text_yyyy_mm_dd() {
        formatter.setMask("yyyy/MM/dd")

        textWatcher.onTextChanged(TEST_POSITIVE_VALUE_2, 0, 0, 10)

        val e = Mockito.mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        Mockito.verify(e).replace(0, 0, TEST_POSITIVE_VALUE_2)
    }
}