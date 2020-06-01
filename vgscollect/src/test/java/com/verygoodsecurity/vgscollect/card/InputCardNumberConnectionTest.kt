package com.verygoodsecurity.vgscollect.card

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.InputCardNumberConnection
import com.verygoodsecurity.vgscollect.view.card.InputRunnable
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandWrapper
import com.verygoodsecurity.vgscollect.view.card.filter.VGSCardFilter
import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.filter.DefaultCardBrandFilter
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock


class InputCardNumberConnectionTest {
    private lateinit var connection: InputRunnable
    private lateinit var iCardBrand: InputCardNumberConnection.IDrawCardBrand

    @Before
    fun setupConnection() {
        val client = mock(VGSValidator::class.java)
        Mockito.doReturn(true).`when`(client).isValid(Mockito.anyString())
        val filter = mock(VGSCardFilter::class.java)
        Mockito.doReturn(
            CardBrandWrapper(
                CardType.VISA,
                CardType.VISA.regex,
                CardType.VISA.name,
                CardType.VISA.resId)
        ).`when`(filter).detect(Mockito.anyString())
        iCardBrand = mock(InputCardNumberConnection.IDrawCardBrand::class.java)
        connection = InputCardNumberConnection(0, client, iCardBrand)
        connection.addFilter(filter)
    }

    @Test
    fun customDivider() {
        val divider = "-"

        val client = mock(VGSValidator::class.java)
        Mockito.doReturn(true).`when`(client).isValid(Mockito.anyString())

        val connection: InputRunnable = InputCardNumberConnection(0, client, divider = divider)
        connection.addFilter(DefaultCardBrandFilter(CardType.values(), null, divider))

        val listener = mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)

        val content = FieldContent.CardNumberContent()
        content.data = "5555-5555-5555-4444"
        val textItem = VGSFieldState(isValid = false,
            isRequired = true,
            fieldName = "fieldName",
            content = content)
        connection.setOutput(textItem)

        connection.run()

        val filteredContent = connection.getOutput().content as FieldContent.CardNumberContent
        assertTrue(filteredContent.cardtype == CardType.MASTERCARD)
        assertTrue(connection.getOutput().isValid)
    }

    @Test
    fun test_draw_card_icon() {
        connection.run()

        Mockito.verify(iCardBrand).drawCardBrandPreview(CardType.NONE, CardType.NONE.name, CardType.NONE.resId)

        val content = FieldContent.CardNumberContent()
        content.data = "4111"
        val textItem = VGSFieldState(isValid = false,
            isRequired = false,
            fieldName = "fieldName",
            content = content)
        connection.setOutput(textItem)

        connection.run()

        Mockito.verify(iCardBrand).drawCardBrandPreview(CardType.VISA, CardType.VISA.name, CardType.VISA.resId)
    }

    @Test
    fun setChangeListener() {
        val listener = mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)

        Mockito.verify(listener).emit(0, VGSFieldState(isValid = false))
    }

    @Test
    fun emitItem() {
        val listener = mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)
        connection.run()
        Mockito.verify(listener, Mockito.times(2)).emit(0, VGSFieldState(isValid = false))
    }

    @Test
    fun setOutput() {
        val listener = mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)

        val textItem = VGSFieldState(fieldName = "fieldName")
        connection.setOutput(textItem)

        connection.run()
        Mockito.verify(listener).emit(0, VGSFieldState(isValid = false, fieldName = "fieldName"))
    }

    @Test
    fun emitEmptyNotRequired() {
        val listener = mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)

        val content = FieldContent.InfoContent()
        content.data = ""
        val textItem = VGSFieldState(isValid = false,
            isRequired = false,
            fieldName = "fieldName",
            content = content)
        connection.setOutput(textItem)

        connection.run()

        assertTrue(connection.getOutput().isValid)
        Mockito.verify(listener).emit(0, textItem)
    }

    @Test
    fun emitShortRequired() {
        val listener = mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)

        val content = FieldContent.CardNumberContent()
        content.data = "5555 55"
        val textItem = VGSFieldState(isValid = false,
            isRequired = true,
            fieldName = "fieldName",
            content = content)
        connection.setOutput(textItem)

        connection.run()

        assertFalse(connection.getOutput().isValid)
        Mockito.verify(listener).emit(0, textItem)
    }

    @Test
    fun emitRequired() {

        val listener = mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)

        val content = FieldContent.CardNumberContent()
        content.data = "4111 1111 1111 1111"
        val textItem = VGSFieldState(isValid = false,
            isRequired = true,
            fieldName = "fieldName",
            content = content)
        connection.setOutput(textItem)

        connection.run()

        assertTrue(connection.getOutput().isValid)
        Mockito.verify(listener).emit(0, textItem)
    }

    private fun <T> any(): T = Mockito.any<T>()
}