package com.verygoodsecurity.vgscollect.card

import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.util.mapToJSON
import com.verygoodsecurity.vgscollect.util.mapUsefulPayloads
import com.verygoodsecurity.vgscollect.view.card.FieldType
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MapToJsonExtensionTest {

    private val userFieldStorageImpl:MutableCollection<VGSFieldState> = mutableListOf()

    @Before
    fun initUserData() {
        userFieldStorageImpl.clear()

        val stateCardNumber_content = FieldContent.CardNumberContent()
        stateCardNumber_content.data = "4111111111111111"
        val stateCardNumber = VGSFieldState(type = FieldType.CARD_NUMBER, content = stateCardNumber_content, fieldName = "card_data.cardNumber")
        userFieldStorageImpl.add(stateCardNumber)

        val stateCVC_content = FieldContent.InfoContent()
        stateCVC_content.data = "123"
        val stateCVC = VGSFieldState(type = FieldType.CVC, content = stateCVC_content, fieldName = "card_data.cardCvc")
        userFieldStorageImpl.add(stateCVC)

        val stateHolder_content = FieldContent.InfoContent()
        stateHolder_content.data = "John Galt"
        val stateHolder = VGSFieldState(type = FieldType.CARD_HOLDER_NAME, content = stateHolder_content, fieldName = "card_data.personal_data.cardHolder")
        userFieldStorageImpl.add(stateHolder)

        val stateExpDate_content = FieldContent.InfoContent()
        stateExpDate_content.data = "05/25"
        val stateExpDate = VGSFieldState(type = FieldType.CARD_EXPIRATION_DATE, content = stateExpDate_content, fieldName = "card_data.personal_data.secret.expDate")
        userFieldStorageImpl.add(stateExpDate)
    }

    @Test
    fun test_userdata() {
        val resultJSON = "{\"card_data\":{\"cardCvc\":\"123\",\"personal_data\":{\"cardHolder\":\"John Galt\",\"secret\":{\"expDate\":\"05/25\"}},\"cardNumber\":\"4111111111111111\"}}"

        val json = userFieldStorageImpl.
            mapUsefulPayloads()?.
            mapToJSON()
        val result = json.toString()

        assertEquals(resultJSON, result)
    }

    @Test
    fun test_primitives_without_userdata() {
        userFieldStorageImpl.clear()
        val resultJSON = "{\"str\":\"some_additi\",\"primitive_float\":123.0123,\"primitive_char\":\"c\",\"primitive_long\":97833333334343443,\"primitive_int\":-123,\"primitive_double\":123.0223,\"primitive_list\":[1,\"DataStr\",1.23,777777777,-7.32,\"d\"],\"primitive_arr\":[1.23,777,-7.32,\"d\"]}"

        val customData = generateBasicData()
        customData["primitive_list"] = arrayListOf(1,"DataStr", 1.23f, 777777777L, -7.32, 'd')
        customData["primitive_arr"] = arrayOf(1.23f, 777L, -7.32, 'd')

        val result = userFieldStorageImpl.
            mapUsefulPayloads(customData)?.
            mapToJSON().toString()

        assertEquals(resultJSON, result)
    }

    @Test
    fun test_primitives_with_userdata() {
        val resultJSON = "{\"str\":\"some_additi\",\"primitive_float\":123.0123,\"primitive_char\":\"c\",\"card_data\":{\"cardCvc\":\"123\",\"personal_data\":{\"cardHolder\":\"John Galt\",\"secret\":{\"expDate\":\"05/25\"}},\"cardNumber\":\"4111111111111111\"},\"primitive_long\":97833333334343443,\"primitive_int\":-123,\"primitive_double\":123.0223,\"primitive_list\":[1,\"DataStr\",1.23,777777777,-7.32,\"d\"],\"primitive_arr\":[1.23,777,-7.32,\"d\"]}"

        val customData = generateBasicData()
        customData["primitive_list"] = arrayListOf(1,"DataStr", 1.23f, 777777777L, -7.32, 'd')
        customData["primitive_arr"] = arrayOf(1.23f, 777L, -7.32, 'd')

        val result = userFieldStorageImpl.
            mapUsefulPayloads(customData)?.
            mapToJSON().toString()

        assertEquals(resultJSON, result)
    }

    @Test
    fun test_userdata_overwrite_string() {
        val resultJSON = "{\"card_data\":{\"cardCvc\":\"123\",\"personal_data\":{\"cardHolder\":\"John Galt\",\"secret\":{\"expDate\":\"05/25\"}},\"cardNumber\":\"4111111111111111\"},\"user_key\":7834002}"

        val customData = HashMap<String, Any>()
        customData["card_data"] = "4111111111117684"
        customData["user_key"] = 0x778992

        val result = userFieldStorageImpl.
            mapUsefulPayloads(customData)?.
            mapToJSON().toString()

        assertEquals(resultJSON, result)
    }

    @Test
    fun test_userdata_overwrite_primitives() {
        val resultJSON = "{\"card_data\":{\"cardCvc\":\"123\",\"personal_data\":{\"cardHolder\":\"John Galt\",\"secret\":{\"expDate\":\"05/25\"}},\"cardNumber\":\"4111111111111111\"},\"user_key\":7834002}"

        val customData = HashMap<String, Any>()
        customData["card_data"] = 0x14
        customData["user_key"] = 0x778992

        val result = userFieldStorageImpl.
            mapUsefulPayloads(customData)?.
            mapToJSON().toString()

        assertEquals(resultJSON, result)
    }

    @Test
    fun test_userdata_overwrite_map() {
        val resultJSON = "{\"card_data\":{\"cardCvc\":\"123\",\"personal_data\":{\"cardHolder\":\"John Galt\",\"secret\":{\"expDate\":\"05/25\"}},\"cardNumber\":\"4111111111111111\"}}"

        val customData = HashMap<String, Any>()

        val secret = HashMap<String, Any>()
        secret["expDate"] = "12.07.1998"

        val personal_data = HashMap<String, Any>()
        personal_data["cardHolder"] = "alf"
        personal_data["secret"] = secret

        val cardData = HashMap<String, Any>()
        cardData["cardNumber"] = "5555555555555555"
        cardData["cardCvc"] = 0x77
        cardData["personal_data"] = personal_data

        customData["card_data"] = cardData

        val result = userFieldStorageImpl.
            mapUsefulPayloads(customData)?.
            mapToJSON().toString()

        assertEquals(resultJSON, result)
    }

    @Test
    fun test_userdata_overwrite_collection() {
        val resultJSON = "{\"card_data\":[\"5555555555555555\",119,{\"personal_data\":{\"cardHolder\":\"John Galt\",\"secret\":{\"expDate\":\"05/25\"}},\"key\":\"nkn77793\"},{\"cardNumber\":\"4111111111111111\"},{\"cardCvc\":\"123\"}]}"

        val customData = HashMap<String, Any>()

        val secret = HashMap<String, Any>()
        secret["expDate"] = "12.07.1998"

        val personal_data = HashMap<String, Any>()
        personal_data["cardHolder"] = "alf"
        personal_data["secret"] = secret

        val data = HashMap<String, Any>()
        data["personal_data"] = personal_data
        data["key"] = "nkn77793"

        val cardData = listOf("5555555555555555", 0x77, data)

        customData["card_data"] = cardData

        val result = userFieldStorageImpl.
            mapUsefulPayloads(customData)

        val result1 =            result?.mapToJSON().toString()


        assertEquals(resultJSON, result1)
    }

    @Test
    fun test_userdata_overwrite_array() {
        val resultJSON = "{\"card_data\":[\"5555555555555555\",119,{\"personal_data\":{\"cardHolder\":\"John Galt\",\"secret\":{\"expDate\":\"05/25\"}},\"key\":\"nkn77793\"},{\"cardNumber\":\"4111111111111111\"},{\"cardCvc\":\"123\"}]}"

        val customData = HashMap<String, Any>()

        val secret = HashMap<String, Any>()
        secret["expDate"] = "12.07.1998"

        val personal_data = HashMap<String, Any>()
        personal_data["cardHolder"] = "alf"
        personal_data["secret"] = secret

        val data = HashMap<String, Any>()
        data["personal_data"] = personal_data
        data["key"] = "nkn77793"

        val cardData = arrayOf("5555555555555555", 0x77, data)

        customData["card_data"] = cardData

        val result = userFieldStorageImpl.
            mapUsefulPayloads(customData)?.
            mapToJSON().toString()

        assertEquals(resultJSON, result)
    }

    @Test
    fun test_collection_inside_map() {
        val resultJSON = "{\"card_data\":{\"cardCvc\":\"123\",\"personal_data\":{\"cardHolder\":\"John Galt\",\"secret\":{\"expDate\":\"05/25\"}},\"cardNumber\":\"4111111111111111\"},\"user_data\":{\"data\":[1.23,777,-7.32,\"d\"],\"user_name\":\"Vasia\"}}"

        val customData = HashMap<String, Any>()

        val user_data = HashMap<String, Any>()
        user_data["user_name"] = "Vasia"
        user_data["data"] = listOf(1.23f, 777L, -7.32, 'd')
        customData["user_data"] = user_data

        val result = userFieldStorageImpl.
            mapUsefulPayloads(customData)?.
            mapToJSON().toString()

        assertEquals(resultJSON, result)
    }


    @Test
    fun test_array_inside_map() {
        val resultJSON = "{\"card_data\":{\"cardCvc\":\"123\",\"personal_data\":{\"cardHolder\":\"John Galt\",\"secret\":{\"expDate\":\"05/25\"}},\"cardNumber\":\"4111111111111111\"},\"user_data\":{\"data\":[1.23,777,-7.32,\"d\"],\"user_name\":\"Vasia\"}}"

        val customData = HashMap<String, Any>()

        val user_data = HashMap<String, Any>()
        user_data["user_name"] = "Vasia"
        user_data["data"] = arrayOf(1.23f, 777L, -7.32, 'd')
        customData["user_data"] = user_data

        val result = userFieldStorageImpl.
            mapUsefulPayloads(customData)?.
            mapToJSON().toString()

        assertEquals(resultJSON, result)
    }

    @Test
    fun test_map_inside_collection() {
        val resultJSON = "{\"card_data\":{\"cardCvc\":\"123\",\"personal_data\":{\"cardHolder\":\"John Galt\",\"secret\":{\"expDate\":\"05/25\"}},\"cardNumber\":\"4111111111111111\"},\"user_data\":[1.23,777,{\"user_name\":\"Vasia\"},\"d\"]}"

        val customData = HashMap<String, Any>()

        val map = HashMap<String, Any>()
        map["user_name"] = "Vasia"

        val user_data = arrayOf(1.23f, 777L, map, 'd')
        customData["user_data"] = user_data

        val result = userFieldStorageImpl.
            mapUsefulPayloads(customData)?.
            mapToJSON().toString()

        assertEquals(resultJSON, result)
    }

    @Test
    fun test_array_inside_collection() {
        val resultJSON = "{\"card_data\":{\"cardCvc\":\"123\",\"personal_data\":{\"cardHolder\":\"John Galt\",\"secret\":{\"expDate\":\"05/25\"}},\"cardNumber\":\"4111111111111111\"},\"user_data\":[1.23,777,[\"val\",\"some\",\"d\"]]}"

        val customData = HashMap<String, Any>()

        val user_arr = arrayOf("val", "some", 'd')

        val user_data = arrayListOf(1.23f, 777L, user_arr)
        customData["user_data"] = user_data

        val result = userFieldStorageImpl.
            mapUsefulPayloads(customData)?.
            mapToJSON().toString()

        assertEquals(resultJSON, result)
    }


    private fun generateBasicData():HashMap<String, Any> {
        val data = HashMap<String, Any>()
        data["str"] = "some_additi"
        data["primitive_long"] = 97833333334343443L
        data["primitive_int"] = -123
        data["primitive_float"] = 123.0123f
        data["primitive_double"] = 123.0223
        data["primitive_char"] = 'c'

        return data
    }
}