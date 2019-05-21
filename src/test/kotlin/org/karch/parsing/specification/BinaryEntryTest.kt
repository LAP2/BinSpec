package org.karch.parsing.specification

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.karch.parsing.specification.dsl.binaryEntrySpecification
import org.karch.parsing.specification.dsl.domain.BinaryEntrySpecification

val TEST_SPECIFICATION: BinaryEntrySpecification = binaryEntrySpecification {
    field("Test field 1") size expectedFieldSize
    field("Test field 2") size expectedFieldSize
    field("Test field 3") size expectedFieldSize
    field("Test field 4") size expectedFieldSize
}

const val expectedSpecificationSize: Long = 16
const val expectedFieldSize: Int = 4

class BinaryEntrySpecificationTest {

    @Test
    fun minimalSizeTest() {
        assertEquals(expectedSpecificationSize, TEST_SPECIFICATION.minimalSize)
    }


}

class BinaryEntryTest {

}
