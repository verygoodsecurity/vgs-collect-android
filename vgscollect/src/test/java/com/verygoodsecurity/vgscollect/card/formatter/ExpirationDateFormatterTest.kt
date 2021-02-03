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

        textWatcher.onTextChanged("32/8", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "32/08")

        textWatcher.beforeTextChanged("32/08", 3, 1, 0)
        textWatcher.onTextChanged("32/8", 3, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "32/")

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

        textWatcher.beforeTextChanged("2039-04", 5, 1, 0)
        textWatcher.onTextChanged("2039-4", 5, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "2039-")

        textWatcher.beforeTextChanged("2039-10", 5, 1, 0)
        textWatcher.onTextChanged("2039-0", 5, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2039-0")

        textWatcher.beforeTextChanged("", 0, 4, 5)
        textWatcher.onTextChanged("2039-05", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2039-05")

        textWatcher.beforeTextChanged("2039-0", 6, 0, 1)
        textWatcher.onTextChanged("2039-05", 6, 0, 1)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "2039-05")
    }

    @Test
    fun test_flow_MM_YY() {
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

        textWatcher.beforeTextChanged("12-38", 0, 1, 0)
        textWatcher.onTextChanged("2-38", 0, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "-38" )

        textWatcher.beforeTextChanged("-38", 0, 0, 1)
        textWatcher.onTextChanged("9-38", 0, 0, 1)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "09-38")

        textWatcher.beforeTextChanged( "09-32", 3, 1, 0)
        textWatcher.onTextChanged("09-2", 3, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "09-2")

        textWatcher.beforeTextChanged( "09-2", 4, 0, 1)
        textWatcher.onTextChanged("09-29", 4, 0, 1)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "09-29")
    }

    @Test
    fun test_flow_MM_YYYY() {
        val editable = mock(Editable::class.java)

        formatter.setMask("MM/yyyy")

        textWatcher.onTextChanged("1", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "1")

        textWatcher.onTextChanged("19", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "1")

        textWatcher.onTextChanged("12", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12/")

        textWatcher.onTextChanged("12/3", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "12/")

        textWatcher.onTextChanged("12/2020", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(3)).replace(0, 0, "12/")

        textWatcher.onTextChanged("12/2039", 0,0,19)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12/2039")

        textWatcher.beforeTextChanged("12/2039", 5, 1, 0)
        textWatcher.onTextChanged("12/209", 5, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(4)).replace(0, 0, "12/")

        textWatcher.beforeTextChanged("10/2034", 0, 1, 0)
        textWatcher.onTextChanged("0/2034", 0, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "0/2034")

        textWatcher.beforeTextChanged("0/2034", 1, 0, 1)
        textWatcher.onTextChanged("08/2034", 1, 0, 1)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "08/2034")

        textWatcher.beforeTextChanged( "08/2032", 5, 1, 0)
        textWatcher.onTextChanged("08/202", 5, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "08/202")

        textWatcher.beforeTextChanged("08/202", 6, 0, 1)
        textWatcher.onTextChanged("08/2029", 6, 0, 1)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "08/2029")
    }

    @Test
    fun test_remove_divider_MM_YY() {
        val editable = mock(Editable::class.java)
        formatter.setMask("MM/yy")

        textWatcher.beforeTextChanged("09/39", 0, 5, 5)
        textWatcher.onTextChanged("09/39", 0, 5, 5)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "09/39")

        textWatcher.beforeTextChanged("09/39", 2, 1, 0)
        textWatcher.onTextChanged("0939", 2, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "09/39")

        textWatcher.beforeTextChanged("09/39", 0, 3, 0)
        textWatcher.onTextChanged("39", 0, 3, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "/39")

        textWatcher.beforeTextChanged("/39", 0, 0, 1)
        textWatcher.onTextChanged("7/39", 0, 0, 1)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "07/39")

        textWatcher.beforeTextChanged("07/39", 2, 3, 0)
        textWatcher.onTextChanged("07", 2, 3, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "07/")

        textWatcher.beforeTextChanged("07/", 3, 0, 1)
        textWatcher.onTextChanged("07/39", 3, 0, 1)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "07/39")

        textWatcher.beforeTextChanged("12/39", 0, 4, 0)
        textWatcher.onTextChanged("9", 0, 4, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12/39")

        textWatcher.beforeTextChanged("12/39", 0, 5, 0)
        textWatcher.onTextChanged("", 0, 5, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "")

        textWatcher.beforeTextChanged("12/39", 1, 4, 0)
        textWatcher.onTextChanged("1", 1, 4, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "12/39")

        textWatcher.beforeTextChanged("12/39", 4, 1, 0)
        textWatcher.onTextChanged("12/3", 4, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12/3")

        textWatcher.beforeTextChanged("12/3", 3, 1, 0)
        textWatcher.onTextChanged("12/", 3, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12/")

        textWatcher.beforeTextChanged("12/", 2, 1, 0)
        textWatcher.onTextChanged("12", 2, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12")
    }


    @Test
    fun test_remove_divider_MM_YYYY() {
        val editable = mock(Editable::class.java)
        formatter.setMask("MM/yyyy")

        textWatcher.beforeTextChanged("09/2039", 0, 5, 5)
        textWatcher.onTextChanged("09/2039", 0, 5, 5)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "09/2039")

        textWatcher.beforeTextChanged("09/2039", 2, 1, 0)
        textWatcher.onTextChanged("092039", 2, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "09/2039")

        textWatcher.beforeTextChanged("09/2039", 0, 3, 0)
        textWatcher.onTextChanged("2039", 0, 3, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "/2039")

        textWatcher.beforeTextChanged("/2039", 0, 0, 1)
        textWatcher.onTextChanged("7/2039", 0, 0, 1)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "07/2039")

        textWatcher.beforeTextChanged("07/39", 2, 3, 0)
        textWatcher.onTextChanged("07", 2, 3, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "07/")

        textWatcher.beforeTextChanged("07/", 3, 0, 1)
        textWatcher.onTextChanged("07/2039", 3, 0, 1)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "07/2039")

        textWatcher.beforeTextChanged("12/2039", 0, 4, 0)
        textWatcher.onTextChanged("9", 0, 4, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12/2039")

        textWatcher.beforeTextChanged("12/2039", 0, 7, 0)
        textWatcher.onTextChanged("", 0, 7, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "")

        textWatcher.beforeTextChanged("12/2039", 1, 4, 0)
        textWatcher.onTextChanged("1", 1, 4, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "12/2039")

        textWatcher.beforeTextChanged("12/2039", 4, 1, 0)
        textWatcher.onTextChanged("12/2", 4, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12/2")

        textWatcher.beforeTextChanged("12/2", 3, 1, 0)
        textWatcher.onTextChanged("12/", 3, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12/")

        textWatcher.beforeTextChanged("12/", 2, 1, 0)
        textWatcher.onTextChanged("12", 2, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "12")
    }

    @Test
    fun test_remove_divider_YY_MM() {
        val editable = mock(Editable::class.java)
        formatter.setMask("yy/MM")

        textWatcher.beforeTextChanged("38/12", 0, 5, 5)
        textWatcher.onTextChanged("38/12", 0, 5, 5)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "38/12")

        textWatcher.beforeTextChanged("38/12", 2, 1, 0)
        textWatcher.onTextChanged("3812", 2, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "38/12")

        textWatcher.beforeTextChanged("38/12", 0, 3, 0)
        textWatcher.onTextChanged("12", 0, 3, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "/12")

        textWatcher.beforeTextChanged("/12", 0, 0, 1)
        textWatcher.onTextChanged("39/12", 0, 0, 1)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "39/12")

        textWatcher.beforeTextChanged("39/12", 2, 3, 0)
        textWatcher.onTextChanged("39", 2, 3, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "39/")

        textWatcher.beforeTextChanged("39/", 3, 0, 1)
        textWatcher.onTextChanged("39/07", 3, 0, 1)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "39/07")

        textWatcher.beforeTextChanged("39/12", 0, 4, 0)
        textWatcher.onTextChanged("2", 0, 4, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "39/12")

        textWatcher.beforeTextChanged("39/12", 0, 5, 0)
        textWatcher.onTextChanged("", 0, 5, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "")

        textWatcher.beforeTextChanged("39/12", 1, 4, 0)
        textWatcher.onTextChanged("3", 1, 4, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(3)).replace(0, 0, "39/12")

        textWatcher.beforeTextChanged("39/12", 4, 1, 0)
        textWatcher.onTextChanged("39/1", 4, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "39/1")

        textWatcher.beforeTextChanged("39/1", 3, 1, 0)
        textWatcher.onTextChanged("39/", 3, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "39/")

        textWatcher.beforeTextChanged("39/", 2, 1, 0)
        textWatcher.onTextChanged("39", 2, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "39")
    }


    @Test
    fun test_remove_divider_YYYY_MM() {
        val editable = mock(Editable::class.java)
        formatter.setMask("yyyy/MM")

        textWatcher.beforeTextChanged("2038/12", 0, 5, 5)
        textWatcher.onTextChanged("2038/12", 0, 5, 5)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2038/12")

        textWatcher.beforeTextChanged("2038/12", 2, 1, 0)
        textWatcher.onTextChanged("203812", 2, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2038/12")

        textWatcher.beforeTextChanged("2038/12", 0, 5, 0)
        textWatcher.onTextChanged("12", 0, 5, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "/12")

        textWatcher.beforeTextChanged("/12", 0, 0, 1)
        textWatcher.onTextChanged("2039/12", 0, 0, 1)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2039/12")

        textWatcher.beforeTextChanged("2039/12", 4, 3, 0)
        textWatcher.onTextChanged("2039",  4, 3, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2039/")

        textWatcher.beforeTextChanged("2039/", 3, 0, 1)
        textWatcher.onTextChanged("2039/07", 3, 0, 1)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2039/07")

        textWatcher.beforeTextChanged("2039/12", 0, 4, 0)
        textWatcher.onTextChanged("2", 0, 4, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2039/12")

        textWatcher.beforeTextChanged("2039/12", 0, 5, 0)
        textWatcher.onTextChanged("", 0, 5, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "")

        textWatcher.beforeTextChanged("2039/12", 1, 4, 0)
        textWatcher.onTextChanged("20", 1, 4, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2039/12")

        textWatcher.beforeTextChanged("2039/12", 6, 1, 0)
        textWatcher.onTextChanged("2039/1", 6, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2039/1")

        textWatcher.beforeTextChanged("2039/1", 5, 1, 0)
        textWatcher.onTextChanged("2039/", 5, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable, times(2)).replace(0, 0, "2039/")

        textWatcher.beforeTextChanged("2039/", 4, 1, 0)
        textWatcher.onTextChanged("2039", 4, 1, 0)
        textWatcher.afterTextChanged(editable)
        verify(editable).replace(0, 0, "2039")
    }

}