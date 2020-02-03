package com.verygoodsecurity.vgscollect

import com.verygoodsecurity.vgscollect.core.model.mapUsefulPayloads
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.card.FieldType
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.regex.Pattern

class FieldStateExtensionTest {
    companion object {
        private const val NUMBER = "number"
        private const val CVC = "cvc"
        private const val CARD_HOLDER_NAME = "holder"
    }

    private val states = mutableListOf<VGSFieldState>()

    @Before
    fun prepareCollection() {
        states.clear()

        states.add(getCardNumberState("4111 1111 1111 3444"))
    }

    @Test
    fun test_mapPayloads() {
        states.add(getCVCState())
        states.add(getHolderState())
        val map = states.mapUsefulPayloads()

        assertEquals(3, map?.size)
    }

    @Test
    fun test_mapPayloads_With_Custom_data() {
        states.add(getCVCState())
        states.add(getHolderState())
        val map = states.mapUsefulPayloads(getCustomData(3))

        assertEquals(6, map?.size)
    }

    @Test
    fun test_Map_CardNumber_Payloads() {
        val map = states.mapUsefulPayloads()
        val state = map!![NUMBER]

        assertNotNull(state)
    }

    @Test
    fun test_Map_CardNumber_Payloads_with_divider() {
        val map = states.mapUsefulPayloads()
        val state = map!![NUMBER]

        val m = Pattern.compile("^[0-9]*\$").matcher(state)
        assertTrue(m.matches())
    }

    @Test
    fun test_Map_CardNumber_Payloads_without_divider() {
        states.add(getCardNumberState("4111111111113444"))
        val map = states.mapUsefulPayloads()
        val state = map!![NUMBER]

        val m = Pattern.compile("^[0-9]*\$").matcher(state)
        assertTrue(m.matches())
    }


    private fun getCardNumberState(number:String) : VGSFieldState {
        val content = FieldContent.InfoContent().apply { data = number }
        return VGSFieldState(fieldName = NUMBER, type = FieldType.CARD_NUMBER, content = content)
    }

    private fun getCVCState():VGSFieldState {
        val content = FieldContent.InfoContent().apply { data = "123" }
        return VGSFieldState(fieldName = CVC, type = FieldType.CVC, content = content)
    }

    private fun getHolderState():VGSFieldState {
        val content = FieldContent.InfoContent().apply { data = "Jo Hun" }
        return VGSFieldState(fieldName = CARD_HOLDER_NAME, type = FieldType.CARD_HOLDER_NAME, content = content)
    }

    private fun getCustomData(count:Int):Map<String, String> {
        return HashMap<String, String>().apply {
            for(i in 0 until count) {
                val key = "key $i"
                val value = "value $i"
                this[key] = value
            }
        }
    }
}