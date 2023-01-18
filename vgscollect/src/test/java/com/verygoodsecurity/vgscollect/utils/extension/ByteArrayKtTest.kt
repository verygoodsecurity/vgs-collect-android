package com.verygoodsecurity.vgscollect.utils.extension

import android.util.Base64
import com.verygoodsecurity.vgscollect.util.extension.toBase64String
import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random.Default.nextBytes

class ByteArrayKtTest {

    @Test
    fun toBase64String_randomByteArray_notEmptyString() {
        // Arrange
        val target = nextBytes(4)
        // Act
        val result = target.toBase64String(Base64.NO_WRAP)
        // Assert
        assertNotEquals("", result)
    }

    @Test
    fun toBase64String_sameReturned() {
        // Arrange
        val target = nextBytes(4)
        // Act
        val firstResult = target.toBase64String(Base64.NO_WRAP)
        val secondResult = target.toBase64String(Base64.NO_WRAP)
        // Assert
        assertEquals(firstResult, secondResult)
    }
}