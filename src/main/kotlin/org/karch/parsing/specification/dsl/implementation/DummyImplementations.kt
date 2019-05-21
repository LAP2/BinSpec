package org.karch.parsing.specification.dsl.implementation

import org.karch.parsing.specification.dsl.*
import org.karch.parsing.specification.dsl.domain.BinaryFieldSizeCalcuator

internal object DummyVariableSizeBinaryFieldSpecificationContextDelegate : VariableSizeBinaryFieldSpecificationContext {

    override fun calculateSize(sizeCalculator: BinaryFieldSizeCalcuator) = throw NotImplementedError(
            "You use dummy shimm for variable field context specification please use normal implementation"
    )

}

internal object DummyConstantSizeBinaryFieldSpecificationContextDelegate : ConstantSizeBinaryFieldSpecificationContext {

    override fun size(fieldSize: Int) = throw NotImplementedError(
            "You use dummy shimm for constant size field context specification please use normal implementation"
    )

}