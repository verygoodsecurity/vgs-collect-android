package com.verygoodsecurity.vgscollect.storage.fields

import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.FieldState
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.storage.FieldDependencyObserver
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.core.storage.StorageContractor
import com.verygoodsecurity.vgscollect.core.storage.content.field.TemporaryFieldsStorage
import com.verygoodsecurity.vgscollect.view.card.CardType
import com.verygoodsecurity.vgscollect.view.card.FieldType
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class FieldsStorageTest {

    private lateinit var store:TemporaryFieldsStorage

    @Before
    fun setupFieldStorage() {
        val contractor:StorageContractor<VGSFieldState> = mock(StorageContractor::class.java) as StorageContractor<VGSFieldState>
        Mockito.doReturn(true).`when`(contractor).checkState(any())
        store = TemporaryFieldsStorage(contractor)
    }

    @Test
    fun test_add_item() {

        store.addItem(0, VGSFieldState(isFocusable = false, fieldName = "1"))
        assertEquals(1, store.getItems().size)

        store.addItem(1, VGSFieldState(isFocusable = false, fieldName = "12"))
        assertEquals(2, store.getItems().size)

        store.addItem(0, VGSFieldState(isFocusable = true, fieldName = "3"))
        assertEquals(2, store.getItems().size)

        store.addItem(2, VGSFieldState(isFocusable = true, fieldName = "4"))
        assertEquals(3, store.getItems().size)
    }

    @Test
    fun test_clear() {
        store.addItem(0, VGSFieldState(fieldName = "n1"))
        store.addItem(1, VGSFieldState(fieldName = "n12"))

        assertEquals(2, store.getItems().size)

        store.clear()
        assertEquals(0, store.getItems().size)
    }

    @Test
    fun test_get_items() {
        store.addItem(0, VGSFieldState(isFocusable = false, fieldName = "n1"))
        store.addItem(1, VGSFieldState(isFocusable = true, fieldName = "n12"))

        assertEquals(2, store.getItems().size)
    }

    @Test
    fun test_remove_item() {
        store.addItem(0, VGSFieldState(isFocusable = false, fieldName = "n1"))

        assertEquals(1, store.getItems().size)

        store.remove(0)

        assertEquals(0, store.getItems().size)
    }

    @Test
    fun test_perform_subscription() {
        val listener = store.performSubscription()

        val item1 = VGSFieldState(fieldName = "n12")
        listener.emit(0, item1)

        val itemsCase1 = store.getItems()
        assertEquals(1, itemsCase1.size)

        val item2 = VGSFieldState(fieldName = "n1")
        listener.emit(1, item2)

        val itemsCase2 = store.getItems()
        assertEquals(2, itemsCase2.size)
    }

    @Test
    fun test_attach_state_change_listener() {
        val listener = mock(OnFieldStateChangeListener::class.java)
        store.attachStateChangeListener(listener)

        assertEquals(1, store.getFieldStateChangeListeners().size)
    }

    @Test
    fun test_trigger_state_change_listener() {
        val listener = mock(OnFieldStateChangeListener::class.java)
        store.attachStateChangeListener(listener)

        store.addItem(1, VGSFieldState(fieldName = "n13"))
        Mockito.verify(listener, Mockito.times(0)).onStateChange(any())

        store.addItem(1, VGSFieldState(fieldName = "n13", hasUserInteraction = true))

        Mockito.verify(listener).onStateChange(any())
    }

    @Test
    fun test_attach_field_dependency_observer() {
        val listener = mock(FieldDependencyObserver::class.java)
        store.attachFieldDependencyObserver(listener)

        assertEquals(1, store.getDependencyObservers().size)
    }

    @Test
    fun test_trigger_field_dependency_observer_refresh_state() {

        val onFieldStateChangeListener = mock(FieldDependencyObserver::class.java)
        store.attachFieldDependencyObserver(onFieldStateChangeListener)

        val onVgsViewStateChangeListener = store.performSubscription()
        onVgsViewStateChangeListener.emit(2, VGSFieldState(type = FieldType.CARD_NUMBER, fieldName = "n13"))
        onVgsViewStateChangeListener.emit(1, VGSFieldState(type = FieldType.CVC, fieldName = "n1"))

        Mockito.verify(onFieldStateChangeListener).onRefreshState(any())
    }

    @Test
    fun test_trigger_field_dependency_observer_state_update() {
        val onFieldStateChangeListener = mock(FieldDependencyObserver::class.java)
        store.attachFieldDependencyObserver(onFieldStateChangeListener)

        val onVgsViewStateChangeListener = store.performSubscription()
        onVgsViewStateChangeListener.emit(2, VGSFieldState(type = FieldType.CARD_NUMBER, fieldName = "n"))

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

        store.attachStateChangeListener(listener)

        store.addItem(0, VGSFieldState(isFocusable = false, isRequired = true, fieldName = "alias"))

        store.addItem(0, VGSFieldState(isFocusable = false, isRequired = true, fieldName = "alias", hasUserInteraction = true))
        Assert.assertNotNull("FieldState didn't update", userLastUpdatedState)

        val viewState = VGSFieldState(isFocusable = true, isRequired = false, fieldName = "alias")
        store.addItem(0, viewState)

        val isEqual = userLastUpdatedState?.hasFocus == viewState.isFocusable &&
                userLastUpdatedState?.isRequired == viewState.isRequired &&
                userLastUpdatedState?.fieldName == viewState.fieldName
        Assert.assertTrue("FieldState didn't update. User get different state", isEqual)
    }

    @Test
    fun test_CVC_3_Length_dependency_detector() {
        val observer = mock(FieldDependencyObserver::class.java)
        store.attachFieldDependencyObserver(observer)

        val listener = store.performSubscription()
        listener.emit(0, VGSFieldState(isFocusable = false, type = FieldType.CVC, fieldName = "n12"))

        val state = createMASTERCARD()
        val content = FieldContent.CardNumberContent()
        content.cardtype = CardType.MASTERCARD
        state.content = content

        listener.emit(1, state)
    }

    private fun createMASTERCARD() : VGSFieldState {
        val state = VGSFieldState(isFocusable = false, type = FieldType.CARD_NUMBER, fieldName = "n12" )
        val content = FieldContent.CardNumberContent()
        content.cardtype = CardType.MASTERCARD
        state.content = content

        return state
    }

    private fun <T> any(): T = Mockito.any<T>()

}