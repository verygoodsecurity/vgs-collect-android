package com.verygoodsecurity.vgscollect.card.connection

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.conection.BaseInputConnection
import com.verygoodsecurity.vgscollect.view.card.conection.InputCardExpDateConnection
import com.verygoodsecurity.vgscollect.view.card.conection.InputRunnable
import com.verygoodsecurity.vgscollect.view.card.validation.RegexValidator
import com.verygoodsecurity.vgscollect.view.card.validation.VGSValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito

class InputCardExpDateConnectionTest {
    val connection: InputRunnable by lazy {
        val client = Mockito.mock(VGSValidator::class.java)
        Mockito.doReturn(true).`when`(client).isValid(Mockito.anyString())
        InputCardExpDateConnection(
            0,
            client
        )
    }

    @Test
    fun setChangeListener() {
        val listener = Mockito.mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)
        Mockito.verify(listener).emit(anyInt(), any())
    }

    @Test
    fun emitItem() {
        val listener = Mockito.mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)
        Mockito.verify(listener).emit(anyInt(), any())
    }

    @Test
    fun setOutput() {
        val listener = Mockito.mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)

        val textItem = VGSFieldState(fieldName = "fieldName")
        connection.setOutput(textItem)

        connection.run()
        Mockito.verify(listener).emit(0, textItem)
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
        Mockito.verify(listener).emit(0, textItem)
    }

    @Test
    fun emitNotRequired() {
        val listener = Mockito.mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)

        val content = FieldContent.InfoContent()
        content.data = "testStr"
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