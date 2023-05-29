package com.verygoodsecurity.vgscollect.card.formatter.date

import android.text.Editable
import android.text.TextWatcher
import com.verygoodsecurity.vgscollect.view.card.formatter.Formatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.FlexibleDateRangeFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.StrictDateRangeFormatter
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class StrictDateRangeFormatterTest {

    companion object {
        private const val TEST_POSITIVE_VALUE_1 = "01/09/1984"
        private const val TEST_POSITIVE_VALUE_2 = "09/01/1984"
        private const val TEST_POSITIVE_VALUE_3 = "1984/01/09"
        private const val TEST_NEGATIVE_VALUE_1 = "01-9823"
        private const val TEST_NEGATIVE_VALUE_2 = "17-90-4030"
    }

    private lateinit var formatter: Formatter
    private lateinit var textWatcher: TextWatcher

    @Before
    fun setupFormatter() {
        with(StrictDateRangeFormatter()) {
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
    fun test_default_text_MM_DD_YYYY() {
        formatter.setMask("MM/dd/yyyy")

        textWatcher.onTextChanged(TEST_POSITIVE_VALUE_1, 0, 0, 10)

        val e = Mockito.mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        Mockito.verify(e).replace(0, 0, TEST_POSITIVE_VALUE_1)
    }

    @Test
    fun test_default_text_DD_MM_YYYY() {
        formatter.setMask("dd/MM/yyyy")

        textWatcher.onTextChanged(TEST_POSITIVE_VALUE_2, 0, 0, 10)

        val e = Mockito.mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        Mockito.verify(e).replace(0, 0, TEST_POSITIVE_VALUE_2)
    }

    @Test
    fun test_default_text_YYYY_MM_DD() {
        formatter.setMask("yyyy/MM/dd")

        textWatcher.onTextChanged(TEST_POSITIVE_VALUE_3, 0, 0, 10)

        val e = Mockito.mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        Mockito.verify(e).replace(0, 0, TEST_POSITIVE_VALUE_3)
    }

    @Test
    fun test_negative() {
        formatter.setMask("MM/dd/yyyy")

        textWatcher.onTextChanged(TEST_NEGATIVE_VALUE_1, 0, 0, 7)

        val e = Mockito.mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        Mockito.verify(e).replace(0, 0, "01")
    }

    @Test
    fun test_2_negative() {
        formatter.setMask("MM/dd/yyyy")

        textWatcher.onTextChanged(TEST_NEGATIVE_VALUE_2, 0, 0, 10)

        val e = Mockito.mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        Mockito.verify(e).replace(0, 0, "")
    }

    @Test
    fun test_default_set_mask_text() {
        formatter.setMask("MM-dd-yyyy")

        textWatcher.onTextChanged(TEST_POSITIVE_VALUE_1, 0, 0, 10)

        val e = Mockito.mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        Mockito.verify(e).replace(0, 0,
            TEST_POSITIVE_VALUE_1
        )
    }
}