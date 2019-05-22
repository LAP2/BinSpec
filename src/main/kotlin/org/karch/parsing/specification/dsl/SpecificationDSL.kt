package org.karch.parsing.specification.dsl

import org.karch.parsing.specification.dsl.domain.*
import org.karch.parsing.specification.dsl.implementation.BinaryEntrySpecificationContextImpl

//============================================== Specification =======================================================//

@DslMarker annotation class BinaryFileSpecificationDSL

//============================================== Entry Specification =================================================//

@BinaryFileSpecificationDSL
interface BinaryEntrySpecificationContext {
    infix fun field(id: String): FullBinaryFieldSpecificationContext
}

typealias BinaryEntrySpecificationContextConfiguration = BinaryEntrySpecificationContext.()->Unit

//============================================== Field Specification =================================================//

//============================================== Constant Size Field =================================================//

@BinaryFileSpecificationDSL
interface ConstantSizeBinaryFieldSpecificationContext {
    infix fun size(fieldSize: Int)
}

//============================================== Variable size field =================================================//

@BinaryFileSpecificationDSL
interface VariableSizeBinaryFieldSpecificationContext {
    infix fun calculateSize(sizeCalculator: BinaryFieldSizeCalcuator)
}

@BinaryFileSpecificationDSL
interface FullBinaryFieldSpecificationContext :
        VariableSizeBinaryFieldSpecificationContext,
        ConstantSizeBinaryFieldSpecificationContext

fun binaryEntrySpecification(
        contextConfiguration: BinaryEntrySpecificationContextConfiguration
): BinaryEntrySpecification {
    val fields = HashMap<String, BinaryField>()
    BinaryEntrySpecificationContextImpl(fields).contextConfiguration()
    return BinaryEntrySpecificationImpl(
            fields.values
                    .filter { it is ConstantSize}
                    .map { (it as ConstantSize).size }
                    .sum(),
            fields
    )
}