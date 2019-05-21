package org.karch.parsing.specification.dsl

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.karch.parsing.specification.dsl.domain.BinaryField
import org.karch.parsing.specification.dsl.domain.ConstantSizeBinaryField
import org.karch.parsing.specification.dsl.domain.DependentOffsetConstantSizeBinaryField
import org.karch.parsing.specification.dsl.domain.DependentOffsetVariableSizeBinaryField
import org.karch.parsing.specification.dsl.domain.VariableSizeBinaryField
import org.karch.parsing.specification.dsl.domain.offsetCalculator
import org.karch.parsing.specification.dsl.fake.DummyBinaryEntry
import org.karch.parsing.specification.dsl.implementation.HasPreviousFieldFullBinaryFieldSpecificationContext
import org.karch.parsing.specification.dsl.implementation.UnknownOffsetFullBinaryFieldSpecificationContext
import org.karch.parsing.specification.dsl.implementation.ZeroOffsetFullBinaryFieldSpecificationContext

class ZeroOffsetFullBinaryFieldSpecificationContextTest {

    private val expectedFieldOffset = 0
    private val expectedFieldSize = 4
    private val expectedFieldID = "Test field"

    private val expectedConstantSizeBinaryField = ConstantSizeBinaryField(
            expectedFieldOffset.toLong(),
            expectedFieldSize.toLong()
    )

    private val expectedMap = mapOf(expectedFieldID to expectedConstantSizeBinaryField)

    @Test
    @DisplayName("Test constant size field creation from ZeroOffsetFullBinaryFieldSpecificationContext")
    fun constantSizeFieldSpecificationTest() {
        val actualMap: MutableMap<String, BinaryField> = HashMap()
        val testedContext = ZeroOffsetFullBinaryFieldSpecificationContext(
                actualMap,
                expectedFieldID
        )
        testedContext size expectedFieldSize
        assertEquals(expectedMap[expectedFieldID],actualMap[expectedFieldID])
        assertEquals(
                HasPreviousFieldFullBinaryFieldSpecificationContext::class,
                testedContext.nextState(expectedFieldID)::class
        )
    }



}

class HasPreviousFieldFullBinaryFieldSpecificationContextTest {

    private val expectedFieldOffset = 4
    private val expectedFieldSize = 4
    private val expectedFieldID = "Test field"

    private val expectedConstantSizeBinaryField = ConstantSizeBinaryField(
            expectedFieldOffset.toLong(),
            expectedFieldSize.toLong()
    )

    private val expectedVariableSizeBinaryField = VariableSizeBinaryField(
            expectedFieldOffset.toLong() + expectedFieldSize
    ) {expectedFieldSize.toLong()}

    private val expectedConstantBinaryFieldMap = mapOf(expectedFieldID to expectedConstantSizeBinaryField)
    private val expectedVariableBinaryFieldMap = mapOf(expectedFieldID to expectedVariableSizeBinaryField)

    @Test
    fun constantSizeFieldSpecificationTest() {

        val actualFieldsMap: MutableMap<String, BinaryField> = HashMap()
        val testedContext = HasPreviousFieldFullBinaryFieldSpecificationContext(
                actualFieldsMap,
                expectedFieldID,
                ConstantSizeBinaryField(0,expectedFieldSize.toLong())
        )
        testedContext size expectedFieldSize
        assertEquals(expectedConstantBinaryFieldMap[expectedFieldID],actualFieldsMap[expectedFieldID])
        assertEquals(
                HasPreviousFieldFullBinaryFieldSpecificationContext::class,
                testedContext.nextState(expectedFieldID)::class
        )
    }

    @Test
    fun variableSizeFieldSpecificationTest() {
        val actualFieldsMap: MutableMap<String, BinaryField> = HashMap()
        val testedContext = HasPreviousFieldFullBinaryFieldSpecificationContext(
                actualFieldsMap,
                expectedFieldID,
                expectedConstantSizeBinaryField
        )
        testedContext calculateSize {expectedFieldSize.toLong()}
        assertEquals(
                expectedVariableBinaryFieldMap[expectedFieldID]?.offset,
                (actualFieldsMap[expectedFieldID] as? VariableSizeBinaryField)?.offset
        )
        runBlocking {
            val dummyEntry = DummyBinaryEntry()
            val expectedOffsetCalcuator = (actualFieldsMap[expectedFieldID] as? VariableSizeBinaryField)?.sizeCalcuator
                    ?: fail("No such field $expectedFieldID in map $actualFieldsMap")
            assertEquals(expectedFieldSize.toLong(),dummyEntry.expectedOffsetCalcuator())
        }
        assertEquals(
                UnknownOffsetFullBinaryFieldSpecificationContext::class,
                testedContext.nextState(expectedFieldID)::class
        )
    }
}

class UnknownOffsetFullBinaryFieldSpecificationContextTest {

    private val expectedFieldOffset = 4
    private val expectedFieldSize = 4
    private val expectedFieldID = "Test field"

    private val expectedVariableSizeBinaryField = VariableSizeBinaryField(
            expectedFieldOffset.toLong() + expectedFieldSize
    ) { expectedFieldSize.toLong() }

    private val expectedDependentOffsetVariableSizeBinaryField = DependentOffsetVariableSizeBinaryField(
            expectedVariableSizeBinaryField.offsetCalculator(),
            { expectedFieldSize.toLong() }
    )


    @Test
    fun constantSizeFieldSpecificationTest() {
        val actualFieldsMap: MutableMap<String, BinaryField> = HashMap()
        val testedContext = UnknownOffsetFullBinaryFieldSpecificationContext(
                actualFieldsMap,
                expectedFieldID,
                expectedVariableSizeBinaryField
        )
        testedContext size expectedFieldSize

        assertEquals(
                expectedFieldSize.toLong(),
                (actualFieldsMap[expectedFieldID] as? DependentOffsetConstantSizeBinaryField)?.size
        )

        runBlocking {
            val dummyEntry = DummyBinaryEntry()
            val fsc = expectedVariableSizeBinaryField.sizeCalcuator
            val foc = (actualFieldsMap[expectedFieldID] as? DependentOffsetConstantSizeBinaryField)?.offsetCalcuator
                    ?: fail("no such field $expectedFieldID in map $actualFieldsMap")
            assertEquals(
                    expectedVariableSizeBinaryField.offset + dummyEntry.fsc(),
                    dummyEntry.foc()
            )
        }

        assertEquals(
                UnknownOffsetFullBinaryFieldSpecificationContext::class,
                testedContext.nextState(expectedFieldID)::class
        )
    }

    @Test
    fun variableSizeFieldSpecificationTest() {
        val actualFieldsMap: MutableMap<String, BinaryField> = HashMap()
        val testedContext = UnknownOffsetFullBinaryFieldSpecificationContext(
                actualFieldsMap,
                expectedFieldID,
                expectedVariableSizeBinaryField
        )
        testedContext calculateSize { expectedFieldSize.toLong() }
        runBlocking {
            val dummyBinaryEntry = DummyBinaryEntry()
            val actualField = (actualFieldsMap[expectedFieldID] as? DependentOffsetVariableSizeBinaryField)
                    ?: fail("no such field $expectedFieldID in map $actualFieldsMap")
            assertEquals(
                    dummyBinaryEntry.(expectedDependentOffsetVariableSizeBinaryField.sizeCalcuator)(),
                    dummyBinaryEntry.(actualField.sizeCalcuator)()
            )
            assertEquals(
                    dummyBinaryEntry.(expectedDependentOffsetVariableSizeBinaryField.offsetCalcuator)(),
                    dummyBinaryEntry.(actualField.offsetCalcuator)()
            )
        }
        assertEquals(
                UnknownOffsetFullBinaryFieldSpecificationContext::class,
                testedContext.nextState(expectedFieldID)::class
        )
    }

}