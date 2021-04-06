package com.verygoodsecurity.vgscollect.view.card.oldview

import android.app.Activity
import android.graphics.Typeface
import android.text.InputType
import android.view.View
import com.verygoodsecurity.vgscollect.TestApplication
import com.verygoodsecurity.vgscollect.any
import com.verygoodsecurity.vgscollect.core.storage.OnFieldStateChangeListener
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField
import com.verygoodsecurity.vgscollect.view.internal.InfoInputField
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApplication::class)
class VGSEditTextTest {
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var view: VGSEditText

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        view = VGSEditText(activity)
    }

    @Test
    fun test_view() {
        view.onAttachedToWindow()
        val internal = view.statePreparer.getView()
        assertNotNull(internal)
    }

    @Test
    fun test_attach_view() {
        view.onAttachedToWindow()

        assertEquals(1, view.childCount)
    }

    @Test
    fun test_check_internal_view() {
        val internal = view.statePreparer.getView()
        assertNotNull(internal)

        val child = view.statePreparer.getView()
        assertTrue(child is InfoInputField)
    }

    @Test
    fun test_info() {
        assertEquals(FieldType.INFO, view.getFieldType())
    }

    @Test
    fun test_info_input_type_text() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setInputType(InputType.TYPE_CLASS_TEXT)
        assertEquals(InputType.TYPE_CLASS_TEXT, view.getInputType())
    }

    @Test
    fun test_info_input_type_number() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setInputType(InputType.TYPE_CLASS_NUMBER)
        assertEquals(InputType.TYPE_CLASS_NUMBER, view.getInputType())
    }

    @Test
    fun test_info_input_type_date() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setInputType(InputType.TYPE_CLASS_DATETIME)
        assertEquals(InputType.TYPE_CLASS_DATETIME, view.getInputType())
    }

    @Test
    fun test_info_input_type_text_password() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val textPass = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        view.setInputType(textPass)
        assertEquals(textPass, view.getInputType())
    }

    @Test
    fun test_info_input_type_number_password() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val numPass = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        view.setInputType(numPass)
        assertEquals(numPass, view.getInputType())
    }

    @Test
    fun test_info_input_type_none() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        view.setInputType(InputType.TYPE_NULL)
        assertEquals(InputType.TYPE_NULL, view.getInputType())
    }

    @Test
    fun test_field_state_change_listener_first() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        val listener = mock(OnFieldStateChangeListener::class.java)
        view.setOnFieldStateChangeListener(listener)
        verify(listener, times(0)).onStateChange(any())

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        verify(listener, times(1)).onStateChange(any())
    }

    @Test
    fun test_field_state_change_listener_last() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val listener = mock(OnFieldStateChangeListener::class.java)
        view.setOnFieldStateChangeListener(listener)

        verify(listener, times(1)).onStateChange(any())
    }


    @Test
    fun test_on_focus_change_listener() {
        val child = view.statePreparer.getView()
        assertTrue(child is BaseInputField)

        (child as BaseInputField).prepareFieldTypeConnection()
        child.applyInternalFieldStateChangeListener()

        val listener = mock(View.OnFocusChangeListener::class.java)
        view.onFocusChangeListener = listener
        view.requestFocus()

        verify(listener).onFocusChange(view, true)
    }

    @Test
    fun set_typeface() {
        assertEquals(null, view.getTypeface())
        view.getTypeface().let {
            view.setTypeface(it, Typeface.BOLD)
        }

        assertEquals(view.getTypeface(), Typeface.DEFAULT_BOLD)
        view.setTypeface(null, Typeface.NORMAL)
        assertEquals(view.getTypeface(), Typeface.DEFAULT)
    }
}