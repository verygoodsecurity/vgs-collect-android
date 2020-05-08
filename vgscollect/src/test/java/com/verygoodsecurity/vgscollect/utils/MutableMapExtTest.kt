package com.verygoodsecurity.vgscollect.utils

import com.verygoodsecurity.vgscollect.util.deepMerge
import com.verygoodsecurity.vgscollect.util.mapToMap
import org.junit.Assert.assertTrue
import org.junit.Test

class MutableMapExtTest  {

    @Test
    fun test_point_json_divider_1_level() {
        val DATA = "data"
        val INNER_DATA = "innerData"

        val VALUE = "value"

        val TEST_DATA = "$DATA.$INNER_DATA"

        val map = mutableMapOf<String, Any>()
        map[TEST_DATA] = VALUE

        val resultMap = map.mapToMap()

        assertTrue(resultMap.containsKey(DATA))
        val data = resultMap[DATA]
        assertTrue(data is Map<*,*> && data.containsKey(INNER_DATA))
        val value = (data as Map<*,*>)[INNER_DATA]
        assertTrue(value == VALUE)
    }

    @Test
    fun test_point_json_divider_2_level() {
        val DATA = "data"
        val INNER_MAP = "innerMap"
        val INNER_DATA = "innerData"

        val VALUE = "value"

        val TEST_DATA = "$DATA.$INNER_DATA"

        val innerMap = mutableMapOf<String, Any>()
        innerMap[TEST_DATA] = VALUE

        val map = mutableMapOf<String, Any>()
        map[INNER_MAP] = innerMap

        val resultMap = map.mapToMap()

        assertTrue(resultMap.containsKey(INNER_MAP))
        val iMap = resultMap[INNER_MAP]
        assertTrue(iMap is Map<*,*> && iMap.containsKey(DATA))
        val data = (iMap as Map<*,*>)[DATA]
        assertTrue(data is Map<*,*> && data.containsKey(INNER_DATA))
        val value = (data as Map<*,*>)[INNER_DATA]
        assertTrue(value == VALUE)
    }

    @Test
    fun test_deep_merge() {
        val dynamicData = getDynamicData()
        val staticData = getStaticData()
        val mergedMap = staticData.deepMerge(dynamicData)


        assertTrue(mergedMap.containsKey("static.data"))
        val static_data = mergedMap["static.data"]
        assertTrue(static_data == "static.data")

        assertTrue(mergedMap.containsKey("nickname"))
        val dyn_taras = mergedMap["nickname"]
        assertTrue(dyn_taras == "dynamic Taras")

        assertTrue(mergedMap.containsKey("card_data.personal_data.stat_data"))
        val card_data_p = mergedMap["card_data.personal_data.stat_data"]
        assertTrue(card_data_p == "static card_data.personal_data")

        assertTrue(mergedMap.containsKey("dynamic.data"))
        val dynamic_data = mergedMap["dynamic.data"]
        assertTrue(dynamic_data == "dynamic setCustomData")

        assertTrue(mergedMap.containsKey("card_data.personal_data.a"))
        val card_data_p_a = mergedMap["card_data.personal_data.a"]
        assertTrue(card_data_p_a == "dynamic data with .")

        assertTrue(mergedMap.containsKey("card_data"))
        val card_data = mergedMap["card_data"]

        assertTrue(card_data is Map<*,*> &&
                card_data.containsKey("personal_data.stat_map"))
        val personal_data_stat_map = (card_data as Map<*,*>)["personal_data.stat_map"]
        assertTrue(personal_data_stat_map == "static inner map")

        assertTrue(card_data.containsKey("personal_data.nothing_interesting"))
        val personal_data_nothing_interesting = (card_data as Map<*,*>)["personal_data.nothing_interesting"]
        assertTrue(personal_data_nothing_interesting == "dynamic string")

        assertTrue(card_data.containsKey("personal_data.something_interesting"))
        val personal_data_something_interesting = (card_data as Map<*,*>)["personal_data.something_interesting"]

        assertTrue(personal_data_something_interesting is Map<*,*> &&
                personal_data_something_interesting.containsKey("new_come"))
        val new_come = (personal_data_something_interesting as Map<*,*>)["new_come"]
        assertTrue(new_come == "dynamic updated item")
    }


    private fun getDynamicData(): MutableMap<String, Any> {
        val innerMap_2 = mutableMapOf<String, String>()
        innerMap_2.put("new_come", "dynamic updated item")

        val innerMap = mutableMapOf<String, Any>()
        innerMap["personal_data.something_interesting"] = innerMap_2
        innerMap["personal_data.nothing_interesting"] = "dynamic string"

        val cd = mutableMapOf<String, Any>()
        cd["dynamic.data"] = "dynamic setCustomData"
        cd["nickname"] = "dynamic Taras"
        cd["card_data"] = innerMap
        cd["card_data.personal_data.a"] = "dynamic data with ."

        return cd
    }

    private fun getStaticData(): MutableMap<String, Any> {
        val innerMap_2 = mutableMapOf<String, String>()
        innerMap_2["new_come"] = "static new Value"

        val innerMap = mutableMapOf<String, Any>()
        innerMap["personal_data.something_interesting"] = innerMap_2
        innerMap["personal_data.stat_map"] = "static inner map"

        val cd = mutableMapOf<String, Any>()
        cd["static.data"] = "static.data"
        cd["nickname"] = "static GP1"
        cd["card_data"] = innerMap
        cd["card_data.personal_data.stat_data"] = "static card_data.personal_data"

        return cd
    }

}