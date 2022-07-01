package com.verygoodsecurity.vgscollect.utils.extension

import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType
import com.verygoodsecurity.vgscollect.util.extension.toTokenizationMap
import org.junit.Assert
import org.junit.Test

class TokenizationKtTest {

    @Test
    fun toTokenizationMap_nullFieldContent_correctMapReturned() {
        // Arrange
        val expected = mapOf<String, Any>(
            "is_required_tokenization" to false,
            "value" to "",
            "format" to "",
            "storage" to "",
            "fieldName" to "",
        )
        val fieldState = VGSFieldState()
        // Act
        val result = fieldState.toTokenizationMap()
        // Assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun toTokenizationMap_defaultFieldContent_correctMapReturned() {
        // Arrange
        val fieldName = "test"
        val expected = mapOf<String, Any>(
            "is_required_tokenization" to true,
            "value" to "",
            "format" to "UUID",
            "storage" to "PERSISTENT",
            "fieldName" to fieldName,
        )
        val fieldState = VGSFieldState(
            content = FieldContent.InfoContent(),
            fieldName = fieldName
        )
        // Act
        val result = fieldState.toTokenizationMap()
        // Assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun toTokenizationMap_customFieldContent_correctMapReturned() {
        // Arrange
        val fieldContent = FieldContent.InfoContent().apply {
            this.isEnabledTokenization = false
            this.vaultAliasFormat = VGSVaultAliasFormat.NUM_LENGTH_PRESERVING
            this.vaultStorage = VGSVaultStorageType.VOLATILE
            this.data = "test"
        }
        val fieldName = "test"
        val expected = mapOf<String, Any>(
            "is_required_tokenization" to fieldContent.isEnabledTokenization,
            "value" to (fieldContent.data?: ""),
            "format" to fieldContent.vaultAliasFormat.name,
            "storage" to fieldContent.vaultStorage.name,
            "fieldName" to fieldName,
        )
        val fieldState = VGSFieldState(
            content = fieldContent,
            fieldName = fieldName
        )
        // Act
        val result = fieldState.toTokenizationMap()
        // Assert
        Assert.assertEquals(expected, result)
    }
}