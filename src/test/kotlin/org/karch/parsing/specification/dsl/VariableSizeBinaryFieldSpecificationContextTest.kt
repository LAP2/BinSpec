package org.karch.parsing.specification.dsl

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.karch.parsing.specification.dsl.domain.*
import org.karch.parsing.specification.dsl.fake.DummyBinaryEntry
import org.karch.parsing.specification.dsl.implementation.DependentOffsetVariableSizeBinaryFieldSpecificationContextImpl
import org.karch.parsing.specification.dsl.implementation.VariableSizeBinaryFieldSpecificationContextImpl
import org.karch.parsing.specification.fake.*


class VariableSizeBinaryFieldSpecificationContextTest {

    private val expectedOffset = 14L
    private val expectedFieldSize = 12L

    private lateinit var expectedVariableSizeField: VariableSizeBinaryField

    private lateinit var expectedDependentOffsetBinaryField: DependentOffsetVariableSizeBinaryField


    @BeforeEach
    fun setUp() {
        expectedVariableSizeField = VariableSizeBinaryField(
                expectedOffset
        ) {expectedFieldSize}

        expectedDependentOffsetBinaryField = DependentOffsetVariableSizeBinaryField(
                expectedVariableSizeField.offsetCalculator(),
                {expectedFieldSize}
        )
    }

    @Test
    fun testVariableSizeFieldCreation(): Unit = runBlocking {
        lateinit var actualCalculator: BinaryFieldSizeCalcuator
        val testedContext = VariableSizeBinaryFieldSpecificationContextImpl(
                {
                    assertEquals(expectedVariableSizeField.offset,it.offset)
                    actualCalculator = it.sizeCalcuator
                },
                expectedOffset
        )
        testedContext calculateSize {expectedFieldSize}
        assertEquals(expectedFieldSize, DummyBinaryEntry().actualCalculator())
    }

    @Test
    fun testDependentOffsetVariableSizeFieldCreation(): Unit = runBlocking {
        lateinit var actualSizeCalculator: BinaryFieldSizeCalcuator
        lateinit var actualOffsetCalculator: BinaryFieldOffsetCalcuator
        val testedContext = DependentOffsetVariableSizeBinaryFieldSpecificationContextImpl(
                {
                    actualSizeCalculator = it.sizeCalcuator
                    actualOffsetCalculator = it.offsetCalculator()
                },
                expectedVariableSizeField
        )
        testedContext calculateSize {expectedFieldSize}
        val dummyEntry = DummyBinaryEntry()
        assertEquals(dummyEntry.(expectedDependentOffsetBinaryField.sizeCalcuator)(),dummyEntry.actualSizeCalculator())
        assertEquals(expectedFieldSize,dummyEntry.actualSizeCalculator())
    }
}