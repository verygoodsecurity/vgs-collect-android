package com.verygoodsecurity.vgscollect.card.formatter

import android.text.Editable
import android.text.TextWatcher
import com.verygoodsecurity.vgscollect.view.card.formatter.Formatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.StrictExpirationDateFormatter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class ExpirationDateFormatterTest {

    companion object {
        private const val TEST_VALUE_1 =  "03/27"
        private const val TEST_VALUE_2 =  "12/2023"
        private const val TEST_VALUE_3 =  "12-2034"
    }

    private lateinit var formatter: Formatter
    private lateinit var textWatcher: TextWatcher

    @Before
    fun setupFormatter() {
        with(StrictExpirationDateFormatter()) {
            formatter = this
            textWatcher = this
        }
    }

    @Test
    fun set_mask() {
        val c = StrictExpirationDateFormatter()
        assertEquals("##/####", c.getMask())
        c.setMask("yyyy MM")
        assertEquals("#### ##", c.getMask())
        c.setMask("yyyy mm")
        assertEquals("#### ##", c.getMask())
        c.setMask("mm-yy")
        assertEquals("##-##", c.getMask())
        c.setMask("yy/mm")
        assertEquals("##/##", c.getMask())
    }

    @Test
    fun test_default_text_full() {
        textWatcher.onTextChanged(TEST_VALUE_2, 0,0,5)

        val e = mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        verify(e).replace(0, 0, TEST_VALUE_2)
    }

    @Test
    fun test_default_set_mask_text_full() {
        formatter.setMask("MM-yyyy")

        textWatcher.onTextChanged(TEST_VALUE_3, 0,0,19)

        val e = mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        verify(e).replace(0, 0, TEST_VALUE_3)
    }

    @Test
    fun test_set_default_short() {
        formatter.setMask("MM/yy")

        textWatcher.onTextChanged(TEST_VALUE_1, 0,0,19)

        val e = mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        verify(e).replace(0, 0, TEST_VALUE_1)
    }

    @Test
    fun test_short_flow_YY_MM() {
        val editable = mock(Editable::class.java)

        formatter.setMask("yy/MM")

        textWatcher.onTextChanged("2", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2")

        textWatcher.onTextChanged("20", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "2")

        textWatcher.onTextChanged("26", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "26/")

        textWatcher.onTextChanged("26/8", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "26/08")

        textWatcher.beforeTextChanged("", 0, 5, 4)
        textWatcher.onTextChanged("26/8", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "26/")

        textWatcher.beforeTextChanged("", 0, 5, 6)
        textWatcher.onTextChanged("26/11", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "26/11")

        textWatcher.beforeTextChanged("", 0, 4, 5)
        textWatcher.onTextChanged("26/1", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "26/1")

        textWatcher.beforeTextChanged("", 0, 5, 6)
        textWatcher.onTextChanged("26/10", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "26/10")
    }

    @Test
    fun test_short_flow_YYYY_MM() {
        val editable = mock(Editable::class.java)

        formatter.setMask("yyyy-MM")

        textWatcher.onTextChanged("1", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "")

        textWatcher.onTextChanged("2039", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2039-")

        textWatcher.onTextChanged("2039-4", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2039-04")

        textWatcher.beforeTextChanged("", 0, 5, 4)
        textWatcher.onTextChanged("2039-4", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "2039-")

        textWatcher.beforeTextChanged("", 0, 4, 5)
        textWatcher.onTextChanged("2039-10", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2039-10")

        textWatcher.beforeTextChanged("", 0, 5, 4)
        textWatcher.onTextChanged("2039-0", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2039-0")

        textWatcher.beforeTextChanged("", 0, 4, 5)
        textWatcher.onTextChanged("2039-05", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2039-05")

        textWatcher.beforeTextChanged("", 0, 5, 4)
        textWatcher.onTextChanged("239-05", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "-05")

        textWatcher.beforeTextChanged("", 0, 5, 4)
        textWatcher.onTextChanged("-5", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "")
    }

    @Test
    fun test_short_flow_MM_YY() {
        val editable = mock(Editable::class.java)

        formatter.setMask("MM-yy")

        textWatcher.onTextChanged("1", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "1")

        textWatcher.onTextChanged("19", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "1")

        textWatcher.onTextChanged("12", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12-")

        textWatcher.onTextChanged("12-3", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12-3")

        textWatcher.onTextChanged("12-34", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12-34")

        textWatcher.onTextChanged("12-4", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12-4")

        textWatcher.onTextChanged("12/41", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12-41")

        textWatcher.beforeTextChanged("", 0, 5, 4)
        textWatcher.onTextChanged("2/41", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "-41")

        textWatcher.beforeTextChanged("", 0, 4, 5)
        textWatcher.onTextChanged("9/41", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "09-41")
    }

    @Test
    fun test_short_flow_MM_YYYY() {
        val editable = mock(Editable::class.java)

        formatter.setMask("MM-yyyy")

        textWatcher.onTextChanged("1", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "1")

        textWatcher.onTextChanged("19", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "1")

        textWatcher.onTextChanged("12", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12-")

        textWatcher.onTextChanged("12-3", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "12-")

        textWatcher.onTextChanged("12-2020", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(3)).replace(0, 0, "12-")

        textWatcher.onTextChanged("12-2039", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12-2039")

        textWatcher.beforeTextChanged("", 0, 5, 4)
        textWatcher.onTextChanged("12-209", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(4)).replace(0, 0, "12-")

        textWatcher.beforeTextChanged("", 0, 4, 5)
        textWatcher.onTextChanged("12-2034", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12-2034")

        textWatcher.beforeTextChanged("", 0, 5, 4)
        textWatcher.onTextChanged("2-2034", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "-2034")

        textWatcher.beforeTextChanged("", 0, 4, 5)
        textWatcher.onTextChanged("3-2034", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "03-2034")

        textWatcher.beforeTextChanged("", 0, 5, 4)
        textWatcher.onTextChanged("03-234", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "03-")
    }
}