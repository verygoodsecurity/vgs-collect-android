package com.verygoodsecurity.vgscollect.card.connection

import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.conection.InputCardNumberConnection
import com.verygoodsecurity.vgscollect.view.card.conection.InputRunnable
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandPreview
import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.CardBrand
import com.verygoodsecurity.vgscollect.view.card.filter.CardBrandFilter
import com.verygoodsecurity.vgscollect.view.card.filter.MutableCardFilter
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock


class InputCardNumberConnectionTest {
    private lateinit var connection: InputRunnable
    private lateinit var iCardBrand: InputCardNumberConnection.IDrawCardBrand
    private lateinit var stateListener:OnVgsViewStateChangeListener

    @Before
    fun setupConnection() {
        val divider = " "
        val client = getValidator()

        iCardBrand = getCardBrandPreviewListener()
        connection =
            InputCardNumberConnection(
                0,
                client,
                iCardBrand,
                divider
            )

        setupFilter(connection, divider)
        setupListener(connection)
    }

    @Test
    fun test_connection() {
        val client = getValidator()
        val connection: InputRunnable =
            InputCardNumberConnection(
                0,
                client
            )

        setupFilter(connection, null)
        setupListener(connection)

        val state = createFieldStateMastercard("")
        connection.setOutput(state)

        connection.run()

        val filteredContent = connection.getOutput().content as FieldContent.CardNumberContent
        assertEquals(filteredContent.cardtype, CardType.MASTERCARD)
        assertTrue(connection.getOutput().isValid)
    }

    @Test
    fun test_connection_divider() {
        val divider = "-"

        val client = getValidator()
        val connection: InputRunnable =
            InputCardNumberConnection(
                0,
                client,
                divider = divider
            )

        setupFilter(connection, divider)
        setupListener(connection)

        val state = createFieldStateMastercard(divider)
        connection.setOutput(state)

        connection.run()

        val filteredContent = connection.getOutput().content as FieldContent.CardNumberContent
        assertEquals(filteredContent.cardtype, CardType.MASTERCARD)
        assertTrue(connection.getOutput().isValid)
    }

    @Test
    fun test_draw_card_icon() {
        connection.run()

        Mockito.verify(iCardBrand, Mockito.times(2)).onCardBrandPreview(CardBrandPreview(CardType.UNKNOWN, CardType.UNKNOWN.regex, CardType.UNKNOWN.name, CardType.UNKNOWN.resId))

        val state = createFieldStateVisa(" ")
        connection.setOutput(state)

        connection.run()

        val c = CardBrandPreview(CardType.VISA,
            CardType.VISA.regex,
            CardType.VISA.name,
            CardType.VISA.resId,
            CardType.VISA.mask,
            CardType.VISA.algorithm,
            CardType.VISA.rangeNumber,
            CardType.VISA.rangeCVV)
        Mockito.verify(iCardBrand).onCardBrandPreview(c)
    }

    @Test
    fun test_set_change_listener() {
        val listener = mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)

        Mockito.verify(listener).emit(0, VGSFieldState(isValid = false))
    }

    @Test
    fun test_emit_item() {
        connection.run()
        Mockito.verify(stateListener, Mockito.times(2)).emit(0, VGSFieldState(isValid = false))
    }

    @Test
    fun test_set_output() {
        val textItem = createFieldStateVisa("")
        connection.setOutput(textItem)

        connection.run()
        Mockito.verify(stateListener).emit(0, textItem)
    }

    @Test
    fun test_custom_filter() {
        val customBrand = CardBrand("^777", "VGS", R.drawable.ic_jcb_light)
        val preview = CardBrandPreview(CardType.UNKNOWN,
            customBrand.regex,
            customBrand.cardBrandName,
            customBrand.drawableResId,
            customBrand.params.mask)

        val filter = mock(MutableCardFilter::class.java)
        Mockito.doReturn(preview).`when`(filter).detect(any())


        val content = FieldContent.CardNumberContent()
        content.data = "777111111111111"
        val state = VGSFieldState(isValid = false,
            isRequired = false,
            fieldName = "fieldName",
            content = content)
        connection.setOutput(state)

        connection.addFilter(filter)
        connection.run()
        Mockito.verify(stateListener).emit(0, state)
    }

    private fun getCardBrandPreviewListener(): InputCardNumberConnection.IDrawCardBrand {
        return mock(InputCardNumberConnection.IDrawCardBrand::class.java)
    }

    private fun getValidator():VGSValidator {
        val client = mock(VGSValidator::class.java)
        Mockito.doReturn(true).`when`(client).isValid(Mockito.anyString())

        return client
    }

    private fun setupFilter(
        connection: InputRunnable,
        divider: String?
    ) {
        val filter = CardBrandFilter(divider)
        connection.addFilter(filter)
    }

    private fun setupListener(connection: InputRunnable) {
        stateListener = mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(stateListener)
    }

    private fun createFieldStateMastercard(divider:String):VGSFieldState {
        val content = FieldContent.CardNumberContent()
        content.data = "5555${divider}5555${divider}5555${divider}4444"
        val textItem = VGSFieldState(isValid = false,
            isRequired = true,
            fieldName = "fieldName",
            content = content)

        return textItem
    }

    private fun createFieldStateVisa(divider:String):VGSFieldState {
        val content = FieldContent.CardNumberContent()
        content.data = "4111${divider}1111${divider}1111${divider}1111"
        val textItem = VGSFieldState(isValid = false,
            isRequired = false,
            fieldName = "fieldName",
            content = content)

        return textItem
    }

    private fun <T> any(): T = Mockito.any<T>()
}