package com.verygoodsecurity.vgscollect.card.connection

import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.conection.InputCardCVCConnection
import com.verygoodsecurity.vgscollect.view.card.conection.InputRunnable
import com.verygoodsecurity.vgscollect.view.card.validation.CompositeValidator
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*

class InputCardCVCConnectionTest {
    val connection: InputRunnable by lazy {
        val client = mock(CompositeValidator::class.java)
        doReturn(emptyList<String>()).`when`(client).validate(anyString())
        InputCardCVCConnection(
            0,
            client
        )
    }

    @Test
    fun setChangeListener() {
        val listener = mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)
        verify(listener, times(1)).emit(anyInt(), any())
    }

    @Test
    fun emitItem() {
        val listener = mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)
        verify(listener, times(1)).emit(anyInt(), any())
    }

    @Test
    fun setOutput() {
        val listener = mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)

        val textItem = VGSFieldState(fieldName = "fieldName")
        connection.setOutput(textItem)

        connection.run()
        verify(listener).emit(0, textItem)
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
        verify(listener).emit(0, textItem)
    }

    @Test
    fun emitNotRequired() {
        val listener = mock(OnVgsViewStateChangeListener::class.java)
        connection.setOutputListener(listener)

        val content = FieldContent.InfoContent()
        content.data = "testStr"
        val textItem = VGSFieldState(isValid = false,
            isRequired = true,
            fieldName = "fieldName",
            content = content)
        connection.setOutput(textItem)

        connection.run()
        verify(listener).emit(0, textItem)
    }

    private fun <T> any(): T = Mockito.any<T>()
}