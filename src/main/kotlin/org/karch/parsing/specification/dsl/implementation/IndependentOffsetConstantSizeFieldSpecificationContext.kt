package org.karch.parsing.specification.dsl.implementation

import org.karch.parsing.specification.dsl.domain.ConstantSizeBinaryField
import org.karch.parsing.specification.dsl.ConstantSizeBinaryFieldSpecificationContext
import org.karch.parsing.specification.dsl.domain.BinaryField

class IndependentOffsetConstantSizeFieldSpecificationContext(
        private val setField: (binaryField: ConstantSizeBinaryField) -> Unit,
        private val fieldOffset: Long
) : ConstantSizeBinaryFieldSpecificationContext {

    override fun size(fieldSize: Int): Unit = setField(ConstantSizeBinaryField(fieldOffset,fieldSize.toLong()))

}