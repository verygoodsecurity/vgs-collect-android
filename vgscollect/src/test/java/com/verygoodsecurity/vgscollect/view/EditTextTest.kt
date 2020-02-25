package com.verygoodsecurity.vgscollect.view

import android.app.Activity
import android.os.Build
import android.util.Log
import android.widget.EditText
import com.verygoodsecurity.vgscollect.view.internal.CardInputField
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class EditTextTest {

    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var view: VGSCardNumberEditText


    @Before
    fun setUp() {
        // Create an activity (Can be any sub-class: i.e. AppCompatActivity, FragmentActivity, etc)
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        // Create the view using the activity context
//        view = VGSCardNumberEditText(activity)
//        Log.e("test", "setDivider")
    }

    @Test
    fun testActNotNull(){
        assertNotNull(activity)
    }

    @Test
    fun bla() {
        assertNotNull(activity)
        val v = VGSCardNumberEditText(activity)
       val view = EditText(activity)
        assertNotNull(view)
        assertNotNull(v)
        view.setText("bla")
//        view.setDivider(' ')
//        assertEquals(, view.getDivider())
        assertEquals("bla", view.text.toString())
    }

//    @Test
//    fun `clicking the text increments the fibonacci sequence`() {
//        val expected = arrayOf(2, 3, 5, 8, 13, 21, 34, 55, 89, 144)
//        expected.forEach {
//            view.performClick()
//            assertEquals("$it", view.text)
//        }
//    }
}