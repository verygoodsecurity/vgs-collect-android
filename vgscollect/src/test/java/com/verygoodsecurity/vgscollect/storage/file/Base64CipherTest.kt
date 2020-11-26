package com.verygoodsecurity.vgscollect.storage.file

import android.app.Activity
import android.database.Cursor
import com.verygoodsecurity.vgscollect.core.storage.content.file.Base64Cipher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.spy
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class Base64CipherTest {
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private lateinit var cipher: Base64Cipher

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()

        cipher = Base64Cipher(activity)
    }

    @Test
    fun test_save() {
        val fieldName = "name"
        cipher.save(fieldName)

        assertEquals(fieldName, cipher.getFieldName())
    }

    @Test
    fun test_check_code_after_save() {
        val fieldName = "name"
        val code = cipher.save(fieldName)

        assertEquals(code, cipher.getCode())
    }

    private fun <T> any(): T = Mockito.any<T>()

    @Test
    fun test_retrieve() {
        val fieldName = "name"
        val uri = "file:///tmp/user.txt"

        val c = spy( activity.contentResolver)
        val cur = Mockito.mock(Cursor::class.java)
        Mockito.doReturn(cur).`when`(c).query(any(), any() , any() , any() , any() )
        Mockito.doReturn(true).`when`(cur).moveToFirst()

        cipher.setContentResolver(c)
        val code = cipher.save(fieldName)
        val map = HashMap<String, Any?>()
        map[code.toString()] = uri

        val p = cipher.retrieve(map)

        assertEquals(fieldName, p?.first)
        assertEquals(uri, p?.second)
    }
}