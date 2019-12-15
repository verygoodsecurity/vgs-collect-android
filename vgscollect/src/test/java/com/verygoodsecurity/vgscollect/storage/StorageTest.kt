package com.verygoodsecurity.vgscollect.storage

import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.storage.DefaultStorage
import com.verygoodsecurity.vgscollect.core.storage.DependencyDispatcher
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.FieldType
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class StorageTest {

    @Test
    fun addItem() {
        val store = DefaultStorage()

        store.addItem(0, VGSFieldState(isFocusable = false))
        Assert.assertEquals(1, store.getStates().size)

        store.addItem(1, VGSFieldState(isFocusable = false))
        Assert.assertEquals(2, store.getStates().size)

        store.addItem(0, VGSFieldState(isFocusable = true))
        Assert.assertEquals(2, store.getStates().size)

        store.addItem(2, VGSFieldState(isFocusable = true))
        Assert.assertEquals(3, store.getStates().size)
    }

    @Test
    fun notifyUserFieldChanged() {
        var userLastUpdatedState:FieldState? = null
        val listener = object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                userLastUpdatedState = state
            }
        }

        val store = DefaultStorage()
        store.attachStateChangeListener(listener)

        store.addItem(0, VGSFieldState(isFocusable = false, isRequired = true, fieldName = "alias"))
        Assert.assertNotNull("FieldState didn't update", userLastUpdatedState)

        val viewState = VGSFieldState(isFocusable = true, isRequired = false, fieldName = "alias1")
        store.addItem(0, viewState)

        val isEqual = userLastUpdatedState?.hasFocus == viewState.isFocusable &&
                userLastUpdatedState?.isRequired == viewState.isRequired &&
                userLastUpdatedState?.fieldName == viewState.fieldName
        Assert.assertTrue("FieldState didn't update. User get different state", isEqual)
    }

    @Test
    fun performSubscription() {
        val store = DefaultStorage()
        val listener = store.performSubscription()

        val item = VGSFieldState()
        listener.emit(0, item)
        val cTest = store.getStates()
        Assert.assertTrue(cTest.any())
        val siTest = cTest.find { it == item }
        Assert.assertNotNull(siTest)

        val item2 = VGSFieldState()
        listener.emit(1, item2)
        val cTest2 = store.getStates()
        Assert.assertEquals(2, cTest2.size)
    }

    @Test
    fun clear() {
        val store = DefaultStorage()
        store.addItem(0, VGSFieldState())
        store.addItem(1, VGSFieldState())

        Assert.assertEquals(2, store.getStates().size)

        store.clear()
        Assert.assertEquals(0, store.getStates().size)
    }

    @Test
    fun testDefaultDependencyDetector() {
        val notifier = mock(DependencyDispatcher::class.java)
        val store = DefaultStorage(notifier)

        val listener = store.performSubscription()
        listener.emit(0, VGSFieldState(isFocusable = false, type = FieldType.CVC))
        listener.emit(0, VGSFieldState(isFocusable = false, type = FieldType.CARD_NUMBER))

        Mockito.verify(notifier).onDependencyDetected(FieldType.CVC, Dependency(value = 4))
    }

    @Test
    fun testCVC_3_LengthDependencyDetector() {
        val notifier = mock(DependencyDispatcher::class.java)
        val store = DefaultStorage(notifier)

        val listener = store.performSubscription()
        listener.emit(0, VGSFieldState(isFocusable = false, type = FieldType.CVC))

        val state = VGSFieldState(isFocusable = false, type = FieldType.CARD_NUMBER)
        val content = FieldContent.CardNumberContent()
        content.cardtype = CardType.MASTERCARD
        state.content = content

        listener.emit(1, state)

        Mockito.verify(notifier).onDependencyDetected(FieldType.CVC, Dependency(value = 3))
    }

    @Test
    fun testCVC_4_LengthDependencyDetector() {
        val notifier = mock(DependencyDispatcher::class.java)
        val store = DefaultStorage(notifier)

        val listener = store.performSubscription()
        listener.emit(0, VGSFieldState(isFocusable = false, type = FieldType.CVC))

        val state = VGSFieldState(isFocusable = false, type = FieldType.CARD_NUMBER)
        val content = FieldContent.CardNumberContent()
        content.cardtype = CardType.AMERICAN_EXPRESS
        state.content = content

        listener.emit(1, state)

        Mockito.verify(notifier).onDependencyDetected(FieldType.CVC, Dependency(value = 4))
    }
}