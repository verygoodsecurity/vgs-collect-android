package com.verygoodsecurity.vgscollect.utils.extension

import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultAliasFormat
import com.verygoodsecurity.vgscollect.core.model.state.tokenization.VGSVaultStorageType
import com.verygoodsecurity.vgscollect.util.extension.toTokenizationData
import com.verygoodsecurity.vgscollect.view.core.serializers.VGSExpDateSeparateSerializer
import org.junit.Assert
import org.junit.Test

class TokenizationKtTest {

    @Test
    fun toTokenizationMap_nullFieldContent_correctMapReturned() {
        // Arrange
        val expected = listOf(
            mapOf<String, Any>(
                "is_required_tokenization" to false,
                "value" to "",
                "format" to "",
                "storage" to "",
                "fieldName" to "",
            )
        )
        val fieldState = VGSFieldState()
        // Act
        val result = fieldState.toTokenizationData()
        // Assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun toTokenizationMap_defaultFieldContent_correctMapReturned() {
        // Arrange
        val fieldName = "test"
        val expected = listOf(
            mapOf<String, Any>(
                "is_required_tokenization" to true,
                "value" to "",
                "format" to "UUID",
                "storage" to "PERSISTENT",
                "fieldName" to fieldName,
            )
        )
        val fieldState = VGSFieldState(
            content = FieldContent.InfoContent(), fieldName = fieldName
        )
        // Act
        val result = fieldState.toTokenizationData()
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
        val expected = listOf(
            mapOf<String, Any>(
                "is_required_tokenization" to fieldContent.isEnabledTokenization,
                "value" to (fieldContent.data ?: ""),
                "format" to fieldContent.vaultAliasFormat.name,
                "storage" to fieldContent.vaultStorage.name,
                "fieldName" to fieldName,
            )
        )
        val fieldState = VGSFieldState(
            content = fieldContent, fieldName = fieldName
        )
        // Act
        val result = fieldState.toTokenizationData()
        // Assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun toTokenizationMap_serialization() {
        // Arrange
        val fieldName = "test"
        val monthFieldName = "month_test"
        val yearFieldName = "year_test"
        val fieldContent = FieldContent.CreditCardExpDateContent().apply {
            this.isEnabledTokenization = false
            this.vaultAliasFormat = VGSVaultAliasFormat.NUM_LENGTH_PRESERVING
            this.vaultStorage = VGSVaultStorageType.VOLATILE
            this.data = "10/30"
            this.dateFormat = "MM/yy"
            this.serializers = listOf(VGSExpDateSeparateSerializer(monthFieldName, yearFieldName))
        }
        val fieldState = VGSFieldState(
            content = fieldContent, fieldName = fieldName
        )
        val expected = listOf(
            mapOf<String, Any>(
                "is_required_tokenization" to fieldContent.isEnabledTokenization,
                "value" to "10",
                "format" to fieldContent.vaultAliasFormat.name,
                "storage" to fieldContent.vaultStorage.name,
                "fieldName" to monthFieldName,
            ), mapOf<String, Any>(
                "is_required_tokenization" to fieldContent.isEnabledTokenization,
                "value" to "30",
                "format" to fieldContent.vaultAliasFormat.name,
                "storage" to fieldContent.vaultStorage.name,
                "fieldName" to yearFieldName,
            )
        )
        // Act
        val result = fieldState.toTokenizationData()
        // Assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun toTokenizationMap_cardNumber() {
        // Arrange
        val fieldContent = FieldContent.CardNumberContent().apply {
            this.isEnabledTokenization = false
            this.vaultAliasFormat = VGSVaultAliasFormat.NUM_LENGTH_PRESERVING
            this.vaultStorage = VGSVaultStorageType.VOLATILE
            this.data = "4111 1111 1111 1111"
            this.rawData = "4111111111111111"
        }
        val fieldName = "test"
        val expected = listOf(
            mapOf<String, Any>(
                "is_required_tokenization" to fieldContent.isEnabledTokenization,
                "value" to (fieldContent.rawData ?: ""),
                "format" to fieldContent.vaultAliasFormat.name,
                "storage" to fieldContent.vaultStorage.name,
                "fieldName" to fieldName,
            )
        )
        val fieldState = VGSFieldState(
            content = fieldContent, fieldName = fieldName
        )
        // Act
        val result = fieldState.toTokenizationData()
        // Assert
        Assert.assertEquals(expected, result)
    }

    @Test
    fun toTokenizationMap_ssn() {
        // Arrange
        val fieldContent = FieldContent.SSNContent().apply {
            this.isEnabledTokenization = false
            this.vaultAliasFormat = VGSVaultAliasFormat.NUM_LENGTH_PRESERVING
            this.vaultStorage = VGSVaultStorageType.VOLATILE
            this.data = "55 555"
            this.rawData = "55555"
        }
        val fieldName = "test"
        val expected = listOf(
            mapOf<String, Any>(
                "is_required_tokenization" to fieldContent.isEnabledTokenization,
                "value" to (fieldContent.rawData ?: ""),
                "format" to fieldContent.vaultAliasFormat.name,
                "storage" to fieldContent.vaultStorage.name,
                "fieldName" to fieldName,
            )
        )
        val fieldState = VGSFieldState(
            content = fieldContent, fieldName = fieldName
        )
        // Act
        val result = fieldState.toTokenizationData()
        // Assert
        Assert.assertEquals(expected, result)
    }
}