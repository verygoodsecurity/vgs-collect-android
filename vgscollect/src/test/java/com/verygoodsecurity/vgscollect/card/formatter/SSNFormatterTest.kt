package com.verygoodsecurity.vgscollect.card.formatter

import android.text.Editable
import android.text.TextWatcher
import com.verygoodsecurity.vgscollect.view.card.formatter.Formatter
import com.verygoodsecurity.vgscollect.view.card.formatter.SSNumberFormatter
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class SSNFormatterTest {

    companion object {
        private const val TEST_VALUE =  "123123123"
    }

    private lateinit var formatter: Formatter
    private lateinit var textWatcher: TextWatcher

    @Before
    fun setupFormatter() {
        with(SSNumberFormatter()) {
            formatter = this
            textWatcher = this
        }
    }

    @Test
    fun test_mask() {
        val c = SSNumberFormatter()
        Assert.assertEquals("###-##-####", c.getMask())
        Assert.assertEquals(11, c.getMaskLength())
    }

    @Test
    fun test_text() {
        val result = "123-12-3123"

        textWatcher.onTextChanged(TEST_VALUE, 0,0,11)

        val e = Mockito.mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        Mockito.verify(e).replace(0, 0, result)
    }
}