package com.verygoodsecurity.vgscollect.card.connection

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.conection.BaseInputConnection
import com.verygoodsecurity.vgscollect.view.card.conection.InputRunnable
import com.verygoodsecurity.vgscollect.view.card.conection.InputSSNConnection
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator
import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class InputSSNConnectionTest {
    private lateinit var connection: InputRunnable
    private lateinit var stateListener: OnVgsViewStateChangeListener

    @Before
    fun setupConnection() {
        val divider = " "
        val client = getValidator()

        connection =
            InputSSNConnection(
                0,
                client
            )

        setupListener(connection)
    }

    @Test
    fun test_connection() {
        val client = getValidator()
        val connection: InputRunnable =
            InputSSNConnection(
                0,
                client
            )

        setupListener(connection)

        val state = createFieldState()
        connection.setOutput(state)

        connection.run()

        Assert.assertTrue(connection.getOutput().isValid)
    }

    @Test
    fun test_set_change_listener() {
        val listener = Mockito.mock(OnVgsViewStateChangeListener::class.java)
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
        val textItem = createFieldState()
        connection.setOutput(textItem)

        connection.run()
        Mockito.verify(stateListener).emit(0, textItem)
    }

    private fun getValidator(): VGSValidator {
        val client = Mockito.mock(VGSValidator::class.java)
        Mockito.doReturn(true).`when`(client).isValid(Mockito.anyString())

        return client
    }

    private fun setupListener(connection: InputRunnable) {
        stateListener = Mockito.mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(stateListener)
    }

    private fun createFieldState(): VGSFieldState {
        val content = FieldContent.SSNContent()
        content.data = "123-12-3123"
        val textItem = VGSFieldState(isValid = false,
            isRequired = true,
            fieldName = "fieldName",
            content = content)

        return textItem
    }

    private fun <T> any(): T = Mockito.any<T>()
}