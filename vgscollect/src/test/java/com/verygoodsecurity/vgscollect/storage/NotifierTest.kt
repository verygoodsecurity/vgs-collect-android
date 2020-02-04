package com.verygoodsecurity.vgscollect.storage

import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.storage.DependencyListener
import com.verygoodsecurity.vgscollect.core.storage.DependencyType
import com.verygoodsecurity.vgscollect.core.storage.Notifier
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.FieldType
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy

class NotifierTest {

    @Test
    fun test_cvc_3_length_dependency() {
        val listener = mock(DependencyListener::class.java)
        val listenerCVC = InputFieldView.DependencyNotifier(listener)

        val notifier = Notifier()
        notifier.addDependencyListener(FieldType.CVC, listenerCVC)

        val testDependency = Dependency(DependencyType.LENGTH, 3)
        notifier.onDependencyDetected(FieldType.CVC, testDependency)

        Mockito.verify(listener).dispatchDependencySetting(testDependency)
    }

    @Test
    fun test_cvc_4_length_dependency() {
        val listener = mock(DependencyListener::class.java)
        val listenerCVC = InputFieldView.DependencyNotifier(listener)

        val notifier = Notifier()
        notifier.addDependencyListener(FieldType.CVC, listenerCVC)

        val testDependency = Dependency(DependencyType.LENGTH, 4)
        notifier.onDependencyDetected(FieldType.CVC, testDependency)

        Mockito.verify(listener).dispatchDependencySetting(testDependency)
    }

    @Test
    fun test_cvc_3_length_detect_after_set_State() {
        val notifier = spy(Notifier())

        val state = createMASTERCARD()
        notifier.onRefreshState(state)

        Mockito.verify(notifier).onDependencyDetected(FieldType.CVC, Dependency(DependencyType.LENGTH, 3))
    }

    @Test
    fun test_cvc_4_length_detect_after_set_State() {
        val notifier = spy(Notifier())

        val state = createAmEx()
        notifier.onRefreshState(state)

        Mockito.verify(notifier).onDependencyDetected(FieldType.CVC, Dependency(DependencyType.LENGTH, 4))
    }

    @Test
    fun test_call_onRefreshState() {
        val notifier = spy(Notifier())

        val state = createAmEx()
        notifier.onRefreshState(state)
        Mockito.verify(notifier).onRefreshState(state)
    }

    @Test
    fun test_call_onStateUpdate() {
        val notifier = spy(Notifier())

        val state = createAmEx()
        notifier.onStateUpdate(state)
        Mockito.verify(notifier).onStateUpdate(state)
    }

    private fun createAmEx() : VGSFieldState {
        val state = VGSFieldState(isFocusable = false, type = FieldType.CARD_NUMBER )
        val content = FieldContent.CardNumberContent()
        content.cardtype = CardType.AMERICAN_EXPRESS
        state.content = content

        return state
    }

    private fun createMASTERCARD() : VGSFieldState {
        val state = VGSFieldState(isFocusable = false, type = FieldType.CARD_NUMBER )
        val content = FieldContent.CardNumberContent()
        content.cardtype = CardType.MASTERCARD
        state.content = content

        return state
    }
}