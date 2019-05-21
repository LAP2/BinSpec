package org.karch.parsing.specification.fake

import org.karch.parsing.specification.dsl.domain.BinaryField
import org.karch.parsing.specification.dsl.implementation.BinaryFieldSetter
import org.junit.jupiter.api.Assertions.*

class FieldSetCallbackFake<BinaryFieldType : BinaryField>(
        private val expectedBinaryField: BinaryFieldType
) : BinaryFieldSetter<BinaryFieldType> {

    override fun invoke(actualBinaryField: BinaryFieldType) {
        assertEquals(expectedBinaryField, actualBinaryField)
    }
}

fun <BinaryFieldType : BinaryField> BinaryFieldType.fakeSetter(): BinaryFieldSetter<BinaryFieldType> =
        FieldSetCallbackFake(this)