package com.verygoodsecurity.vgscollect.card.formatter

import android.text.Editable
import android.text.TextWatcher
import com.verygoodsecurity.vgscollect.view.card.formatter.Formatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.FlexibleDateRangeFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.StrictDateRangeFormatter
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class DateRangeFormatterTest {

    companion object {
        private const val TEST_POSITIVE_VALUE_1 = "01/09/1984"
        private const val TEST_POSITIVE_VALUE_2 = "09/01/1984"
        private const val TEST_POSITIVE_VALUE_3 = "1984/01/09"
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
    fun test_flow_MM_DD_YYYY() {
        val editable = Mockito.mock(Editable::class.java)

        formatter.setMask("MM/dd/yyyy")

        textWatcher.onTextChanged("2", 0, 1, 1)
        textWatcher.afterTextChanged(editable)
        Mockito.verify(editable).replace(0, 0, "02")

        textWatcher.beforeTextChanged("02", 0, 0, 2)
        textWatcher.onTextChanged("022", 2, 1, 4)
        textWatcher.afterTextChanged(editable)
        Mockito.verify(editable).replace(0, 0, "02/2")

        textWatcher.beforeTextChanged("022", 0, 0, 2)
        textWatcher.onTextChanged("02242", 0, 0, 19)
        textWatcher.afterTextChanged(editable)
        Mockito.verify(editable).replace(0, 0, "02/24/2")

        textWatcher.beforeTextChanged("02242", 0, 0, 2)
        textWatcher.onTextChanged("02242022", 0,0, 10)
        textWatcher.afterTextChanged(editable)
        Mockito.verify(editable).replace(0, 0, "02/24/2022")

        textWatcher.beforeTextChanged("02242022", 0, 0, 2)
        textWatcher.onTextChanged("0222422", 0,0, 10)
        textWatcher.afterTextChanged(editable)
        Mockito.verify(editable).replace(0, 0, "02/24/2")

        textWatcher.beforeTextChanged("02242", 0, 0, 2)
        textWatcher.onTextChanged("02242024", 0,0, 10)
        textWatcher.afterTextChanged(editable)
        Mockito.verify(editable).replace(0, 0, "02/24/2024")
    }

    @Test
    fun test_flow_DD_MM_YYYY() {
        val editable = Mockito.mock(Editable::class.java)

        formatter.setMask("dd/MM/yyyy")

        textWatcher.onTextChanged("2", 0, 1, 1)
        textWatcher.afterTextChanged(editable)
        Mockito.verify(editable).replace(0, 0, "2")

        textWatcher.beforeTextChanged("2", 0, 0, 2)
        textWatcher.onTextChanged("212", 2, 1, 4)
        textWatcher.afterTextChanged(editable)
        Mockito.verify(editable).replace(0, 0, "21/02")

        textWatcher.beforeTextChanged("2102", 0, 0, 2)
        textWatcher.onTextChanged("21022", 0, 0, 19)
        textWatcher.afterTextChanged(editable)
        Mockito.verify(editable).replace(0, 0, "21/02/2")

        textWatcher.beforeTextChanged("21022", 0, 0, 2)
        textWatcher.onTextChanged("2103", 0,0, 10)
        textWatcher.afterTextChanged(editable)
        Mockito.verify(editable).replace(0, 0, "21/03")

        textWatcher.beforeTextChanged("2103", 0, 0, 2)
        textWatcher.onTextChanged("21032002", 0,0, 10)
        textWatcher.afterTextChanged(editable)
        Mockito.verify(editable).replace(0, 0, "21/03/2002")
    }

    @Test
    fun test_flow_YYYY_MM_DD() {
        val editable = Mockito.mock(Editable::class.java)

        formatter.setMask("yyyy/MM/dd")

        textWatcher.onTextChanged("2", 0, 1, 1)
        textWatcher.afterTextChanged(editable)
        Mockito.verify(editable).replace(0, 0, "2")

        textWatcher.beforeTextChanged("2", 0, 0, 2)
        textWatcher.onTextChanged("2020", 2, 1, 4)
        textWatcher.afterTextChanged(editable)
        Mockito.verify(editable).replace(0, 0, "2020")

        textWatcher.beforeTextChanged("2020", 0, 0, 2)
        textWatcher.onTextChanged("20202", 0, 0, 19)
        textWatcher.afterTextChanged(editable)
        Mockito.verify(editable).replace(0, 0, "2020")

        textWatcher.beforeTextChanged("2020", 0, 0, 2)
        textWatcher.onTextChanged("20209", 0,0, 10)
        textWatcher.afterTextChanged(editable)
        Mockito.verify(editable).replace(0, 0, "2020/09")

        textWatcher.beforeTextChanged("20209", 0, 0, 2)
        textWatcher.onTextChanged("20200923", 0,0, 10)
        textWatcher.afterTextChanged(editable)
        Mockito.verify(editable).replace(0, 0, "2020/09/23")
    }
}