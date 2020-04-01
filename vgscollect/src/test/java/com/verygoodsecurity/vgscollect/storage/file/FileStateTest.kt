package com.verygoodsecurity.vgscollect.storage.file

import com.verygoodsecurity.vgscollect.core.model.state.FileState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class FileStateTest {

    @Test
    fun test_file_state_Equals_true() {
        val s1 = FileState(300L, "someFile", "mime/txt", "file.name")
        val s2 = FileState(302L, "someFile1", "mime/txt1", "file.name")

        assertEquals(s1, s2)
    }

    @Test
    fun test_file_state_Equals_false() {
        val s1 = FileState(300L, "someFile", "mime/txt", "file.name")
        val s2 = FileState(302L, "someFile1", "mime/txt1", "file.name1")

        assertNotEquals(s1, s2)
    }
}