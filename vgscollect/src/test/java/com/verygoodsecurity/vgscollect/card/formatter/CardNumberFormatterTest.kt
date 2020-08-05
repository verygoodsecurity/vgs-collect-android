package com.verygoodsecurity.vgscollect.card.formatter

import android.text.Editable
import android.text.TextWatcher
import com.verygoodsecurity.vgscollect.view.card.formatter.CardNumberFormatter
import com.verygoodsecurity.vgscollect.view.card.formatter.Formatter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class CardNumberFormatterTest {

    companion object {
        private const val TEST_VALUE_16 =  "4111111111111111"
        private const val TEST_VALUE_19 =  "4111111111111111110"
    }

    private lateinit var formatter: Formatter
    private lateinit var textWatcher: TextWatcher

    @Before
    fun setupFormatter() {
        with(CardNumberFormatter()) {
            formatter = this
            textWatcher = this
        }
    }

    @Test
    fun test_set_mask() {
        val c = CardNumberFormatter()
        assertEquals("#### #### #### #### ###" , c.getMask())
        assertEquals(23 , c.getMaskLength())
        c.setMask("#### ###### #####")
        assertEquals("#### ###### #####" , c.getMask())
        assertEquals(17 , c.getMaskLength())
    }

    @Test
    fun test_default_16_set_text_full() {
        val result = "4111 1111 1111 1111"

        textWatcher.onTextChanged(TEST_VALUE_16, 0,0,16)

        val e = mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        Mockito.verify(e).replace(0, 0, result)
    }

    @Test
    fun test_default_19_set_text_full() {
        val result = "4111 1111 1111 1111 110"

        textWatcher.onTextChanged(TEST_VALUE_19, 0,0,19)

        val e = mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        Mockito.verify(e).replace(0, 0, result)
    }

    @Test
    fun test_set_default_19_set_text_full_mask() {
        val result = "4111-1111-1111-1111-110"
        formatter.setMask("####-####-####-####-###")

        textWatcher.onTextChanged(TEST_VALUE_19, 0,0,19)

        val e = mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        Mockito.verify(e).replace(0, 0, result)
    }
}