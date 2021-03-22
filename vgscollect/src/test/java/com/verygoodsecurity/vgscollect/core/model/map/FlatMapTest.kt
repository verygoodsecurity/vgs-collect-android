package com.verygoodsecurity.vgscollect.core.model.map

import org.junit.Assert
import org.junit.Test

class FlatMapTest {

    @Test
    fun set_nestedObjects_validStructureReturned() {
        // Arrange
        val map = FlatMap()
        val expectedResult = "{data={user={name=John Doe, age=30}}}"

        // Act
        map.set("data.user.name", "John Doe")
        map.set("data.user.age", 30)

        // Assert
        Assert.assertEquals(expectedResult, map.toString())
    }

    @Test
    fun set_arrayAdded_validStructureReturned() {
        // Arrange
        val map = FlatMap()
        val expectedResult = "{data={user={name=John Doe, age=30, pets=[card, dog]}}}"

        // Act
        map.set("data.user.name", "John Doe")
        map.set("data.user.age", 30)
        map.set("data.user.pets[0]", "card")
        map.set("data.user.pets[1]", "dog")

        // Assert
        Assert.assertEquals(expectedResult, map.toString())
    }

    @Test
    fun set_arraysAddedWithNulls_validStructureReturned() {
        // Arrange
        val map = FlatMap()
        val expectedResult =
            "{data={user={name=John Doe, age=30, pets=[card, dog, null, null, null, null, null, null, null, null, bird]}}}"

        // Act
        map.set("data.user.name", "John Doe")
        map.set("data.user.age", 30)
        map.set("data.user.pets[0]", "card")
        map.set("data.user.pets[1]", "dog")
        map.set("data.user.pets[10]", "bird")

        // Assert
        Assert.assertEquals(expectedResult, map.toString())
    }

    @Test
    fun set_objectInsideArray_validStructureReturned() {
        // Arrange
        val map = FlatMap()
        val expectedResult =
            "{data={user={name=John Doe, age=30, pets=[{name=Ralph, type=dog, age=3}]}}}"

        // Act
        map.set("data.user.name", "John Doe")
        map.set("data.user.age", 30)
        map.set("data.user.pets[0].name", "Ralph")
        map.set("data.user.pets[0].type", "dog")
        map.set("data.user.pets[0].age", "3")

        // Assert
        Assert.assertEquals(expectedResult, map.toString())
    }

    @Test
    fun set_nestedArrays_validStructureReturned() {
        // Arrange
        val map = FlatMap()
        val expectedResult =
            "{data={user={name=John Doe, age=30, pets=[{fish=[{name=Gold Fish, type=gold, age=1}]}]}}}"

        // Act
        map.set("data.user.name", "John Doe")
        map.set("data.user.age", 30)
        map.set("data.user.pets[0].fish[0].name", "Gold Fish")
        map.set("data.user.pets[0].fish[0].type", "gold")
        map.set("data.user.pets[0].fish[0].age", "1")

        // Assert
        Assert.assertEquals(expectedResult, map.toString())
    }

    @Test
    fun set_invalidParamsNotAdded_validStructureReturned() {
        // Arrange
        val map = FlatMap()
        val expectedResult = "{data={user={name=John Doe, age=30, pets=[{fish=[{type=gold}]}]}}}"

        // Act
        map.set("data.user.name", "John Doe")
        map.set("data.user.age", 30)
        map.set("data.user.pets[0].fish[0].", "Gold Fish")
        map.set("data.user.pets[0].fish[0].type", "gold")
        map.set("data.user.pets[0].[0].age", "1")

        // Assert
        Assert.assertEquals(expectedResult, map.toString())
    }

    @Test
    fun set_twoObjectsWithArrays_validStructureReturned() {
        // Arrange
        val map = FlatMap()
        val expectedResult =
            "{data={user={name=John Doe, age=30, pets=[card, dog]}}, extraData={tokens=[test_token_1, test_token_2]}}"

        // Act
        map.set("data.user.name", "John Doe")
        map.set("data.user.age", 30)
        map.set("data.user.pets[0]", "card")
        map.set("data.user.pets[1]", "dog")

        map.set("extraData.tokens[0]", "test_token_1")
        map.set("extraData.tokens[1]", "test_token_2")

        // Assert
        Assert.assertEquals(expectedResult, map.toString())
    }
}