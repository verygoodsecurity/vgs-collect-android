package com.verygoodsecurity.vgscollect.storage

import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.storage.*
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.FieldType
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class FieldsStorageTest {

    private fun <T> any(): T = Mockito.any<T>()

    @Test
    fun test_add_item() {
        val store = TemporaryFieldsStorage()

        store.addItem(0, VGSFieldState(isFocusable = false))
        assertEquals(1, store.getItems().size)

        store.addItem(1, VGSFieldState(isFocusable = false))
        assertEquals(2, store.getItems().size)

        store.addItem(0, VGSFieldState(isFocusable = true))
        assertEquals(2, store.getItems().size)

        store.addItem(2, VGSFieldState(isFocusable = true))
        assertEquals(3, store.getItems().size)
    }

    @Test
    fun test_clear() {
        val store = TemporaryFieldsStorage()
        store.addItem(0, VGSFieldState())
        store.addItem(1, VGSFieldState())

        assertEquals(2, store.getItems().size)

        store.clear()
        assertEquals(0, store.getItems().size)
    }

    @Test
    fun test_get_items() {
        val store = TemporaryFieldsStorage()

        store.addItem(0, VGSFieldState(isFocusable = false))
        store.addItem(1, VGSFieldState(isFocusable = true))

        assertEquals(2, store.getItems().size)
    }

    @Test
    fun test_perform_subscription() {
        val store = TemporaryFieldsStorage()
        val listener = store.performSubscription()

        val item1 = VGSFieldState()
        listener.emit(0, item1)

        val itemsCase1 = store.getItems()
        assertEquals(1, itemsCase1.size)

        val item2 = VGSFieldState()
        listener.emit(1, item2)

        val itemsCase2 = store.getItems()
        assertEquals(2, itemsCase2.size)
    }

    @Test
    fun test_attach_state_change_listener() {
        val listener = mock(OnFieldStateChangeListener::class.java)
        val store = TemporaryFieldsStorage()
        store.attachStateChangeListener(listener)

        assertEquals(1, store.getFieldStateChangeListeners().size)
    }

    @Test
    fun test_trigger_state_change_listener() {
        val listener = mock(OnFieldStateChangeListener::class.java)
        val store = TemporaryFieldsStorage()
        store.attachStateChangeListener(listener)

        store.addItem(0, VGSFieldState())

        Mockito.verify(listener).onStateChange(any())
    }

    @Test
    fun test_attach_field_dependency_observer() {
        val listener = mock(FieldDependencyObserver::class.java)
        val store = TemporaryFieldsStorage()
        store.attachFieldDependencyObserver(listener)

        assertEquals(1, store.getDependencyObservers().size)
    }

    @Test
    fun test_trigger_field_dependency_observer_refresh_state() {
        val store = TemporaryFieldsStorage()

        val onFieldStateChangeListener = mock(FieldDependencyObserver::class.java)
        store.attachFieldDependencyObserver(onFieldStateChangeListener)

        val onVgsViewStateChangeListener = store.performSubscription()
        onVgsViewStateChangeListener.emit(2, VGSFieldState(type = FieldType.CARD_NUMBER))
        onVgsViewStateChangeListener.emit(1, VGSFieldState(type = FieldType.CVC))

        Mockito.verify(onFieldStateChangeListener).onRefreshState(any())
    }

    @Test
    fun test_trigger_field_dependency_observer_state_update() {
        val store = TemporaryFieldsStorage()

        val onFieldStateChangeListener = mock(FieldDependencyObserver::class.java)
        store.attachFieldDependencyObserver(onFieldStateChangeListener)

        val onVgsViewStateChangeListener = store.performSubscription()
        onVgsViewStateChangeListener.emit(2, VGSFieldState(type = FieldType.CARD_NUMBER))

        Mockito.verify(onFieldStateChangeListener).onStateUpdate(any())
    }

    @Test
    fun test_notify_user_field_changed() {
        var userLastUpdatedState:FieldState? = null
        val listener = object : OnFieldStateChangeListener {
            override fun onStateChange(state: FieldState) {
                userLastUpdatedState = state
            }
        }

        val store = TemporaryFieldsStorage()
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
    fun test_CVC_3_Length_dependency_detector() {
        val observer = mock(FieldDependencyObserver::class.java)
        val store = TemporaryFieldsStorage()
        store.attachFieldDependencyObserver(observer)

        val listener = store.performSubscription()
        listener.emit(0, VGSFieldState(isFocusable = false, type = FieldType.CVC))

        val state = createMASTERCARD()
        val content = FieldContent.CardNumberContent()
        content.cardtype = CardType.MASTERCARD
        state.content = content

        listener.emit(1, state)
    }

    private fun createMASTERCARD() : VGSFieldState {
        val state = VGSFieldState(isFocusable = false, type = FieldType.CARD_NUMBER )
        val content = FieldContent.CardNumberContent()
        content.cardtype = CardType.MASTERCARD
        state.content = content

        return state
    }
}