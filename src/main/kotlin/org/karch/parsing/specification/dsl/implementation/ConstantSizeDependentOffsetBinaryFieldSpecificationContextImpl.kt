package org.karch.parsing.specification.dsl.implementation

import org.karch.parsing.specification.dsl.domain.BinaryField
import org.karch.parsing.specification.dsl.domain.DependentOffsetConstantSizeBinaryField
import org.karch.parsing.specification.Interner
import org.karch.parsing.specification.dsl.ConstantSizeBinaryFieldSpecificationContext
import org.karch.parsing.specification.dsl.domain.offsetCalculator

// ======================================= Constant size dependent offset ==============================================
class ConstantSizeDependentOffsetBinaryFieldSpecificationContextImpl(
        private val addFieldCallback: (binaryField: BinaryField) -> Unit,
        private val previousField: BinaryField
) : ConstantSizeBinaryFieldSpecificationContext {

    override fun size(fieldSize: Int) {
        addFieldCallback(
                DependentOffsetConstantSizeBinaryField(
                        previousField.offsetCalculator(),
                        fieldSize.toLong()
                )
        )
    }
}