package org.karch.parsing.specification.dsl.implementation

import org.karch.parsing.specification.dsl.VariableSizeBinaryFieldSpecificationContext
import org.karch.parsing.specification.dsl.domain.BinaryFieldSizeCalcuator
import org.karch.parsing.specification.dsl.domain.VariableSizeBinaryField

class VariableSizeBinaryFieldSpecificationContextImpl(
        private val setField: BinaryFieldSetter<VariableSizeBinaryField>,
        private val fieldOffset: Long
        ) : VariableSizeBinaryFieldSpecificationContext {

    override fun calculateSize(sizeCalculator: BinaryFieldSizeCalcuator) {
        setField(VariableSizeBinaryField(fieldOffset,sizeCalculator))
    }

}