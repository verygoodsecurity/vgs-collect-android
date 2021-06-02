package com.verygoodsecurity.vgscollect.storage.file

import android.app.Activity
import android.net.Uri
import com.verygoodsecurity.vgscollect.core.storage.VgsStore
import com.verygoodsecurity.vgscollect.core.storage.content.file.FileStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.TemporaryFileStorage
import com.verygoodsecurity.vgscollect.core.storage.content.file.VGSFileProvider
import com.verygoodsecurity.vgscollect.core.storage.content.file.VgsFileCipher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController

@RunWith(RobolectricTestRunner::class)
class FileStorageTest {
    private lateinit var activityController: ActivityController<Activity>
    private lateinit var activity: Activity

    private val encodedFileMaxSize = Runtime.getRuntime().maxMemory() / 8

    @Before
    fun setUp() {
        activityController = Robolectric.buildActivity(Activity::class.java)
        activity = activityController.get()
    }

    @Test
    fun test_add_item() {
        val store: VgsStore<String, String> = TemporaryFileStorage(activity)

        store.addItem("userData.response", "file:///tmp/user.txt")
        assertEquals(1, store.getItems().size)

        store.addItem("userData.response1", "file:///tmp/activity.txt")
        assertEquals(1, store.getItems().size)

        store.addItem("userData.response2", "file:///tmp/test.txt")
        assertEquals(1, store.getItems().size)

        store.addItem("userData.response3", "file:///tmp/user.txt")
        assertEquals(1, store.getItems().size)
    }

    @Test
    fun test_remove_item() {
        val KEY = "file"
        val store: VgsStore<String, String> = TemporaryFileStorage(activity)

        store.addItem(KEY, "file:///tmp/user.txt")

        assertEquals(1, store.getItems().size)

        store.remove(KEY)

        assertEquals(0, store.getItems().size)
    }

    @Test
    fun test_clear() {
        val store: VgsStore<String, String> = TemporaryFileStorage(activity)
        store.addItem("userData.response", "file:///tmp/user.txt")

        assertEquals(1, store.getItems().size)

        store.clear()
        assertEquals(0, store.getItems().size)
    }

    @Test
    fun test_get_items() {
        val store: VgsStore<String, String> = TemporaryFileStorage(activity)

        store.addItem("userData.response", "file:///tmp/user.txt")

        assertEquals(1, store.getItems().size)
    }

    @Test
    fun test_get_associated_list() {
        val c = mock(VgsFileCipher::class.java)

        val base64Str = "base64Str"
        val fieldName = "userData.response"
        val filePath = "file:///tmp/user.txt"
        val retMap = HashMap<String, Any?>()
        retMap[fieldName] = filePath

        val uri = Uri.parse(filePath)

        doReturn(base64Str)
            .`when`(c).getBase64(uri, encodedFileMaxSize)


        val response = fieldName to filePath

        doReturn(response)
            .`when`(c).retrieve(retMap)

        val store: FileStorage = with(TemporaryFileStorage(activity)) {
            this.setCipher(c)

            this
        }

        val map = HashMap<String, Any?>()
        map[fieldName] = filePath
        store.dispatch(map)


        val p = store.getAssociatedList().toMutableList()[0]
        assertEquals(fieldName, p.first)
        assertEquals(base64Str, p.second)
    }

    @Test
    fun test_dispatch() {
        val c = mock(VgsFileCipher::class.java)

        val base64Str = "base64Str"
        val fieldName = "userData.response"
        val filePath = "file:///tmp/user.txt"
        val retMap = HashMap<String, Any?>()
        retMap[fieldName] = filePath

        val uri = Uri.parse(filePath)

        doReturn(base64Str)
            .`when`(c).getBase64(uri, encodedFileMaxSize)


        val response = fieldName to filePath

        doReturn(response)
            .`when`(c).retrieve(retMap)

        val store: FileStorage = with(TemporaryFileStorage(activity)) {
            this.setCipher(c)

            this
        }

        val map = HashMap<String, Any?>()
        map[fieldName] = filePath
        store.dispatch(map)


        verify(c).getBase64(uri, encodedFileMaxSize)

        assertEquals(1, store.getItems().size)
    }

    @Test
    fun test_attachFile() {
        val c = mock(VgsFileCipher::class.java)

        val fieldName = "store.file"

        val provider: VGSFileProvider = with(TemporaryFileStorage(activity)) {
            this.setCipher(c)

            this
        }

        provider.attachFile(fieldName)

        verify(c).save(fieldName)
    }

    @Test
    fun test_get_attached_files() {
        val c = mock(VgsFileCipher::class.java)

        val fieldName = "store.file"
        val uri = "file:///tmp/user.txt"

        val provider: VGSFileProvider = with(TemporaryFileStorage(activity)) {
            this.setCipher(c)

            addItem(fieldName, uri)

            this
        }

        val list = provider.getAttachedFiles()
        assertEquals(1, list.size)
        assertEquals(fieldName, list[0].fieldName)
    }

    @Test
    fun test_detach_all() {
        val c = mock(VgsFileCipher::class.java)

        val fieldName = "store.file"
        val uri = "file:///tmp/user.txt"

        val provider: VGSFileProvider = with(TemporaryFileStorage(activity)) {
            this.setCipher(c)

            addItem(fieldName, uri)

            this
        }

        assertEquals(1, provider.getAttachedFiles().size)

        provider.detachAll()
        assertEquals(0, provider.getAttachedFiles().size)
    }

    @Test
    fun test_detach_file() {
        val c = mock(VgsFileCipher::class.java)

        val fieldName = "store.file"
        val uri = "file:///tmp/user.txt"

        val provider: VGSFileProvider = with(TemporaryFileStorage(activity)) {
            this.setCipher(c)

            addItem(fieldName, uri)

            this
        }

        assertEquals(1, provider.getAttachedFiles().size)

        val fileData = provider.getAttachedFiles()[0]
        provider.detachFile(fileData)

        assertEquals(0, provider.getAttachedFiles().size)
    }
}