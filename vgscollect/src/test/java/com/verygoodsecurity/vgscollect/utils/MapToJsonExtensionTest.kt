package com.verygoodsecurity.vgscollect.utils

import com.verygoodsecurity.vgscollect.util.mapToJSON
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MapToJsonExtensionTest {

    private val userFieldStorageImpl: MutableMap<String, Any> = mutableMapOf()

    @Before
    fun initUserData() {
        userFieldStorageImpl.clear()

        userFieldStorageImpl["cardNumber"] = "4111111111111111"
        userFieldStorageImpl["cardCvc"] = "123"
        userFieldStorageImpl["cardHolder"] = "John Galt"
        userFieldStorageImpl["expDate"] = "05/25"
        userFieldStorageImpl["addition"] = true
    }

    @Test
    fun test_userdata() {
        val userData = "{" +
                "\"cardCvc\":\"123\"," +
                "\"cardHolder\":\"John Galt\"," +
                "\"cardNumber\":\"4111111111111111\"," +
                "\"expDate\":\"05/25\"," +
                "\"addition\":true" +
                "}"

        assertEquals(userData, userFieldStorageImpl.mapToJSON().toString())
    }

    @Test
    fun test_1_level_nested_map() {
        val userData = "{" +
                "\"addition_data\":{" +
                    "\"age\":32," +
                    "\"height\":1.9" +
                "}," +
                "\"cardCvc\":\"123\"," +
                "\"cardHolder\":\"John Galt\"," +
                "\"cardNumber\":\"4111111111111111\"," +
                "\"expDate\":\"05/25\"," +
                "\"addition\":true" +
                "}"

        val additionData = with(mutableMapOf<String, Any>()) {
            put("age", 32)
            put("height", 1.9)
            this
        }

        userFieldStorageImpl["addition_data"] = additionData
        assertEquals(userData, userFieldStorageImpl.mapToJSON().toString())
    }

    @Test
    fun test_2_level_nested_map() {
        val userData = "{" +
                "\"addition_data\":{" +
                    "\"allow\":false," +
                    "\"char\":\"#\"," +
                    "\"age\":32," +
                    "\"height\":1.9," +
                    "\"level_2\":{" +
                        "\"allow\":false," +
                        "\"char\":\"#\"," +
                        "\"age\":32," +
                        "\"height\":1.9" +
                    "}" +
                "}," +
                "\"cardCvc\":\"123\"," +
                "\"cardHolder\":\"John Galt\"," +
                "\"cardNumber\":\"4111111111111111\"," +
                "\"expDate\":\"05/25\"," +
                "\"addition\":true" +
                "}"

        val level_2 = with(mutableMapOf<String, Any>()) {
            put("allow", false)
            put("age", 32)
            put("height", 1.9)
            put("char", '#')
            this
        }

        val level_1 = with(mutableMapOf<String, Any>()) {
            put("allow", false)
            put("age", 32)
            put("height", 1.9)
            put("char", '#')
            put("level_2", level_2)
            this
        }

        userFieldStorageImpl["addition_data"] = level_1
        assertEquals(userData, userFieldStorageImpl.mapToJSON().toString())
    }

    @Test
    fun test_1_level_nested_collection() {
        val userData = "{" +
                "\"addition_data\":[" +
                    "\"test\",true,12,1.9,\"&\"" +
                "]," +
                "\"cardCvc\":\"123\"," +
                "\"cardHolder\":\"John Galt\"," +
                "\"cardNumber\":\"4111111111111111\"," +
                "\"expDate\":\"05/25\"," +
                "\"addition\":true" +
                "}"

        val additionData = with(mutableListOf<Any>()) {
            add("test")
            add(true)
            add(12)
            add(1.9)
            add('&')
            this
        }

        userFieldStorageImpl["addition_data"] = additionData
        assertEquals(userData, userFieldStorageImpl.mapToJSON().toString())
    }


    @Test
    fun test_2_level_nested_collection() {
        val userData = "{" +
                "\"addition_data\":[" +
                    "\"test\"," +
                    "true," +
                    "12," +
                    "1.9," +
                    "[\"test\",true,12,1.9]" +
                "]," +
                "\"cardCvc\":\"123\"," +
                "\"cardHolder\":\"John Galt\"," +
                "\"cardNumber\":\"4111111111111111\"," +
                "\"expDate\":\"05/25\"," +
                "\"addition\":true" +
                "}"

        val level_2 = with(mutableListOf<Any>()) {
            add("test")
            add(true)
            add(12)
            add(1.9)
            this
        }
        val level_1 = with(mutableListOf<Any>()) {
            add("test")
            add(true)
            add(12)
            add(1.9)
            add(level_2)
            this
        }

        userFieldStorageImpl["addition_data"] = level_1
        assertEquals(userData, userFieldStorageImpl.mapToJSON().toString())
    }


    @Test
    fun test_2_level_nested_map_collection() {
        val userData = "{" +
                "\"addition_data\":{" +
                "\"allow\":false," +
                "\"char\":\"#\"," +
                "\"age\":32," +
                "\"height\":1.9," +
                "\"level_2\":[\"test\",true,12,1.9]" +
                "}," +
                "\"cardCvc\":\"123\"," +
                "\"cardHolder\":\"John Galt\"," +
                "\"cardNumber\":\"4111111111111111\"," +
                "\"expDate\":\"05/25\"," +
                "\"addition\":true" +
                "}"

        val level_2 = with(mutableListOf<Any>()) {
            add("test")
            add(true)
            add(12)
            add(1.9)
            this
        }

        val level_1 = with(mutableMapOf<String, Any>()) {
            put("allow", false)
            put("age", 32)
            put("height", 1.9)
            put("char", '#')
            put("level_2", level_2)
            this
        }

        userFieldStorageImpl["addition_data"] = level_1
        assertEquals(userData, userFieldStorageImpl.mapToJSON().toString())
    }


    @Test
    fun test_2_level_nested_collection_map() {
        val userData = "{" +
                "\"addition_data\":[" +
                    "\"test\",true,12,1.9,{" +
                        "\"allow\":false," +
                        "\"char\":\"#\"," +
                        "\"age\":32," +
                        "\"height\":1.9" +
                    "}" +
                "]," +
                "\"cardCvc\":\"123\"," +
                "\"cardHolder\":\"John Galt\"," +
                "\"cardNumber\":\"4111111111111111\"," +
                "\"expDate\":\"05/25\"," +
                "\"addition\":true" +
                "}"


        val level_2 = with(mutableMapOf<String, Any>()) {
            put("allow", false)
            put("age", 32)
            put("height", 1.9)
            put("char", '#')
            this
        }

        val level_1 = with(mutableListOf<Any>()) {
            add("test")
            add(true)
            add(12)
            add(1.9)
            add(level_2)
            this
        }

        userFieldStorageImpl["addition_data"] = level_1
        assertEquals(userData, userFieldStorageImpl.mapToJSON().toString())
    }
}