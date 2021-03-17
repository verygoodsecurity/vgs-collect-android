package com.verygoodsecurity.vgscollect.utils.extension

import com.verygoodsecurity.vgscollect.util.extension.ArrayMergePolicy
import com.verygoodsecurity.vgscollect.util.extension.deepMerge
import com.verygoodsecurity.vgscollect.util.extension.putIfAbsentCompat
import com.verygoodsecurity.vgscollect.util.extension.toJSON
import org.junit.Assert.*
import org.junit.Test

class MapTest {

    @Test
    fun putIfAbsentSafe_itemAbsent_addedSuccessfully() {
        //Arrange
        val date = mutableMapOf("test_test" to "test")
        // Act
        val result = date.putIfAbsentCompat("test", "test")
        // Arrange
        assertNotNull(result)
    }

    @Test
    fun putIfAbsentSafe_itemExist_notAdded() {
        //Arrange
        val date = mutableMapOf("test" to "test")
        // Act
        val result = date.putIfAbsentCompat("test", "test")
        // Arrange
        assertNull(result)
    }

    @Test
    fun deepMerge_withoutNestedNoSameItems_mergedSuccessfully() {
        //Arrange
        val target = mutableMapOf<String, Any>("test" to "test")
        val source = mutableMapOf<String, Any>("test_test" to "test")
        val expectedResult = mutableMapOf<String, Any>("test" to "test", "test_test" to "test")
        // Act
        target.deepMerge(source, ArrayMergePolicy.OVERWRITE)
        // Arrange
        assertEquals(target, expectedResult)
    }

    @Test
    fun deepMerge_withNestedNoSameItems_mergedSuccessfully() {
        //Arrange
        val target = mutableMapOf<String, Any>(
            "test" to mutableMapOf<String, Any>(
                "test_1" to "test"
            )
        )
        val source = mutableMapOf<String, Any>(
            "test" to mutableMapOf<String, Any>(
                "test_2" to 1
            )
        )
        val expectedResult = mutableMapOf<String, Any>(
            "test" to mutableMapOf<String, Any>(
                "test_1" to "test",
                "test_2" to 1
            )
        )
        // Act
        target.deepMerge(source, ArrayMergePolicy.OVERWRITE)
        // Arrange
        assertEquals(target, expectedResult)
    }

    @Test
    fun deepMerge_withNestedAndSameItems_mergedSuccessfully() {
        //Arrange
        val target = mutableMapOf<String, Any>(
            "test" to mutableMapOf<String, Any>(
                "test_1" to "test"
            )
        )
        val source = mutableMapOf<String, Any>(
            "test" to mutableMapOf<String, Any>(
                "test_1" to 1
            )
        )
        val expectedResult = mutableMapOf<String, Any>(
            "test" to mutableMapOf<String, Any>(
                "test_1" to 1
            )
        )
        // Act
        target.deepMerge(source, ArrayMergePolicy.OVERWRITE)
        // Arrange
        assertEquals(target, expectedResult)
    }

    @Test
    fun deepMerge_withNestedItems_mergedSuccessfully() {
        //Arrange
        val target = mutableMapOf<String, Any>(
            "test" to mutableMapOf(
                "test_1" to "test",
                "test_2" to "test",
                "test_3" to mutableMapOf<String, Any>(
                    "test_1" to "test"
                )
            )
        )
        val source = mutableMapOf<String, Any>(
            "test" to mutableMapOf(
                "test_1" to "test",
                "test_2" to "test",
                "test_3" to mutableMapOf<String, Any>(
                    "test_2" to "test"
                )
            )
        )
        val expectedResult = mutableMapOf<String, Any>(
            "test" to mutableMapOf(
                "test_1" to "test",
                "test_2" to "test",
                "test_3" to mutableMapOf<String, Any>(
                    "test_1" to "test",
                    "test_2" to "test"
                )
            )
        )
        // Act
        target.deepMerge(source, ArrayMergePolicy.OVERWRITE)
        // Arrange
        assertEquals(target, expectedResult)
    }

    @Test
    fun deepMerge_withNestedArraysMergeArrays_mergedSuccessfully() {
        //Arrange
        val target = mutableMapOf<String, Any>(
            "test" to mutableMapOf(
                "test_1" to mutableMapOf<String, Any>(
                    "test_2" to arrayListOf<Any>(1, 2)
                )
            )
        )
        val source = mutableMapOf<String, Any>(
            "test" to mutableMapOf(
                "test_1" to mutableMapOf<String, Any>(
                    "test_2" to arrayListOf<Any>(3, 4)
                )
            )
        )
        val expectedResult = mutableMapOf<String, Any>(
            "test" to mutableMapOf(
                "test_1" to mutableMapOf<String, Any>(
                    "test_2" to arrayListOf<Any>(1, 2, 3, 4)
                )
            )
        )
        // Act
        target.deepMerge(source, ArrayMergePolicy.MERGE)
        // Arrange
        assertEquals(target, expectedResult)
    }

    @Test
    fun deepMerge_withNestedArraysOverwriteArraysOverwrite_mergedSuccessfully() {
        //Arrange
        val target = mutableMapOf<String, Any>(
            "test" to mutableMapOf(
                "test_1" to mutableMapOf<String, Any>(
                    "test_2" to arrayListOf<Any>(1, 2)
                )
            )
        )
        val source = mutableMapOf<String, Any>(
            "test" to mutableMapOf(
                "test_1" to mutableMapOf<String, Any>(
                    "test_2" to arrayListOf<Any>(3, 4)
                )
            )
        )
        val expectedResult = mutableMapOf<String, Any>(
            "test" to mutableMapOf(
                "test_1" to mutableMapOf<String, Any>(
                    "test_2" to arrayListOf<Any>(3, 4)
                )
            )
        )
        // Act
        target.deepMerge(source, ArrayMergePolicy.OVERWRITE)
        // Arrange
        assertEquals(target, expectedResult)
    }

    @Test
    fun deepMerge_withNestedArraysWithNestedObjectsMerge_sourceOverwriteTarget() {
        //Arrange
        val target = mutableMapOf<String, Any>(
            "test" to mutableMapOf(
                "test_1" to mutableMapOf<String, Any>(
                    "test_2" to arrayListOf<Any>(1)
                )
            )
        )
        val source = mutableMapOf<String, Any>(
            "test" to mutableMapOf(
                "test_1" to mutableMapOf<String, Any>(
                    "test_2" to arrayListOf<Any>(
                        mutableMapOf<String, Any>(
                            "test_3" to "test"
                        )
                    )
                )
            )
        )
        val expectedResult = mutableMapOf<String, Any>(
            "test" to mutableMapOf(
                "test_1" to mutableMapOf<String, Any>(
                    "test_2" to arrayListOf<Any>(
                        mutableMapOf<String, Any>(
                            "test_3" to "test"
                        )
                    )
                )
            )
        )
        // Act
        target.deepMerge(source, ArrayMergePolicy.MERGE)
        // Arrange
        assertEquals(target, expectedResult)
    }

    @Test
    fun deepMerge_withNestedArraysWithNestedObjectsMerge_sourceAddedToEnd() {
        //Arrange
        val target = mutableMapOf<String, Any>(
            "test" to mutableMapOf(
                "test_1" to mutableMapOf<String, Any>(
                    "test_2" to arrayListOf<Any>(
                        mutableMapOf<String, Any>(
                            "test_3" to "test"
                        )
                    )
                )
            )
        )
        val source = mutableMapOf<String, Any>(
            "test" to mutableMapOf(
                "test_1" to mutableMapOf<String, Any>(
                    "test_2" to arrayListOf<Any>(1)
                )
            )
        )
        val expectedResult = mutableMapOf<String, Any>(
            "test" to mutableMapOf(
                "test_1" to mutableMapOf<String, Any>(
                    "test_2" to arrayListOf(
                        mutableMapOf<String, Any>(
                            "test_3" to "test"
                        ),
                        1
                    )
                )
            )
        )
        // Act
        target.deepMerge(source, ArrayMergePolicy.MERGE)
        // Arrange
        assertEquals(target, expectedResult)
    }

    @Test
    fun deepMerge_extraDataWithNulls() {
        //Arrange
        val target = mutableMapOf<String, Any>(
                "test" to arrayListOf(
                    null,
                    mutableMapOf<String, Any>(
                        "test_1" to "test_1"
                    ),
                    null
                )
        )
        val source = mutableMapOf<String, Any>(
            "test" to arrayListOf(
                null,
                null,
                "test_1"
            )
        )
        val expectedResult = mutableMapOf<String, Any>(
            "test" to arrayListOf(
                null,
                mutableMapOf<String, Any>(
                    "test_1" to "test_1"
                ),
                null,
                "test_1"
            )
        )
        // Act
        target.deepMerge(source, ArrayMergePolicy.MERGE)
        // Arrange
        assertEquals(target, expectedResult)
    }
}