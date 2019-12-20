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
import org.junit.Test
import org.mockito.Mockito


class InputCardNumberConnectionTest {
    val connection: InputRunnable by lazy {
        val client = Mockito.mock(VGSValidator::class.java)
        Mockito.doReturn(true).`when`(client).isValid(Mockito.anyString())
        val filter = Mockito.mock(VGSCardFilter::class.java)
        Mockito.doReturn(CardBrandWrapper(cardType = CardType.VISA)).`when`(filter).detect(Mockito.anyString())
        val i = InputCardNumberConnection(0, client)
        i.addFilter(filter)
        i
    }

    @Test
    fun customDivider() {
        val divider = "-"

        val client = Mockito.mock(VGSValidator::class.java)
        Mockito.doReturn(true).`when`(client).isValid(Mockito.anyString())

        val connection: InputRunnable = InputCardNumberConnection(0, client, divider = divider)
        connection.addFilter(DefaultCardBrandFilter(CardType.values(), null, divider))

        val listener = Mockito.mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)

        val content = FieldContent.CardNumberContent()
        content.data = "5555-5551-1111-1890"
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
    fun drawCardIcon() {
        val drawer = Mockito.mock(InputCardNumberConnection.IdrawCardBrand::class.java)
        val client = Mockito.mock(VGSValidator::class.java)
        val conn = InputCardNumberConnection(0, client, drawer)

        conn.run()

        Mockito.verify(drawer).drawCardBrandPreview()
    }


    @Test
    fun setChangeListener() {
        val listener = Mockito.mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)

        Mockito.verify(listener).emit(0, VGSFieldState(isValid = false))
    }

    @Test
    fun emitItem() {
        val listener = Mockito.mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)
        connection.run()
        Mockito.verify(listener, Mockito.times(2)).emit(0, VGSFieldState(isValid = false))
    }

    @Test
    fun setOutput() {
        val listener = Mockito.mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)

        val textItem = VGSFieldState(fieldName = "fieldName")
        connection.setOutput(textItem)

        connection.run()
        Mockito.verify(listener).emit(0, VGSFieldState(isValid = false, fieldName = "fieldName"))
    }

    @Test
    fun emitEmptyNotRequired() {
        val listener = Mockito.mock(OnVgsViewStateChangeListener::class.java)
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
        Mockito.verify(listener).emit(0, VGSFieldState(isValid = true, isRequired = false, fieldName = "fieldName", content = content))
    }

    @Test
    fun emitShortRequired() {
        val listener = Mockito.mock(OnVgsViewStateChangeListener::class.java)
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
        Mockito.verify(listener).emit(0, VGSFieldState(isValid = false, isRequired = true, fieldName = "fieldName", content = content))
    }

    @Test
    fun emitRequired() {

        val listener = Mockito.mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)

        val content = FieldContent.CardNumberContent()
        content.data = "4111 1111 5555 5555"
        val textItem = VGSFieldState(isValid = false,
            isRequired = true,
            fieldName = "fieldName",
            content = content)
        connection.setOutput(textItem)

        connection.run()

        assertTrue(connection.getOutput().isValid)
        Mockito.verify(listener).emit(0, VGSFieldState(isValid = true, isRequired = true, fieldName = "fieldName", content = content))
    }
}