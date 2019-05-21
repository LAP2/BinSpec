package org.karch.parsing.specification.dsl.implementation

import org.karch.parsing.specification.Interner
import org.karch.parsing.specification.dsl.VariableSizeBinaryFieldSpecificationContext
import org.karch.parsing.specification.dsl.domain.BinaryField
import org.karch.parsing.specification.dsl.domain.BinaryFieldSizeCalcuator
import org.karch.parsing.specification.dsl.domain.DependentOffsetVariableSizeBinaryField
import org.karch.parsing.specification.dsl.domain.offsetCalculator

class DependentOffsetVariableSizeBinaryFieldSpecificationContextImpl(
        private val setField: BinaryFieldSetter<DependentOffsetVariableSizeBinaryField>,
        private val offsetDependentField: BinaryField
) : VariableSizeBinaryFieldSpecificationContext {

    override fun calculateSize(sizeCalculator: BinaryFieldSizeCalcuator): Unit =
            setField(DependentOffsetVariableSizeBinaryField(offsetDependentField.offsetCalculator(),sizeCalculator))
}