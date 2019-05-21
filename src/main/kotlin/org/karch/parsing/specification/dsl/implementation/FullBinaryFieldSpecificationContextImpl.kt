package org.karch.parsing.specification.dsl.implementation

import org.karch.parsing.specification.dsl.FullBinaryFieldSpecificationContext
import org.karch.parsing.specification.dsl.domain.BinaryField
import org.karch.parsing.specification.dsl.domain.BinaryFieldSizeCalcuator
import org.karch.parsing.specification.dsl.domain.ConstantSizeBinaryField
import org.karch.parsing.specification.dsl.domain.VariableSizeBinaryField

internal sealed class State : FullBinaryFieldSpecificationContext {
    abstract fun nextState(fieldID: String): State
}

internal class ZeroOffsetFullBinaryFieldSpecificationContext(
        private val fields: MutableMap<String, BinaryField>,
        private val fieldID: String
) : State() {

    companion object {

        @JvmStatic
        val FIRST_FIELD_OFFSET = 0

    }

    private lateinit var currentField: ConstantSizeBinaryField

    private val fieldCallbackWrapper = {
        binaryField: ConstantSizeBinaryField ->
        fields[fieldID] = binaryField
        currentField = binaryField
    }

    override fun size(fieldSize: Int) {
        IndependentOffsetConstantSizeFieldSpecificationContext(
                fieldCallbackWrapper,
                FIRST_FIELD_OFFSET.toLong()
        ).size(fieldSize)
    }

    override fun calculateSize(sizeCalculator: BinaryFieldSizeCalcuator) =
            DummyVariableSizeBinaryFieldSpecificationContextDelegate.calculateSize(sizeCalculator)

    override fun nextState(fieldID: String): State {
        return HasPreviousFieldFullBinaryFieldSpecificationContext(
                fields,
                fieldID,
                currentField
        )
    }
}

internal class HasPreviousFieldFullBinaryFieldSpecificationContext(
        private val fields: MutableMap<String, BinaryField>,
        private val fieldId: String,
        private val lastField: ConstantSizeBinaryField
) : State() {

    private lateinit var currentField: BinaryField

    private val constantSizeFieldSetter = {
        binaryfield: ConstantSizeBinaryField ->
        fields[fieldId] = binaryfield
        currentField = binaryfield
    }

    private val variableSizeFieldSetter = {
        binaryField: VariableSizeBinaryField ->
        fields[fieldId] = binaryField
        currentField = binaryField
    }

    private fun ConstantSizeBinaryField.nextFieldOffset() = this.offset + this.size

    override fun size(fieldSize: Int) =
            IndependentOffsetConstantSizeFieldSpecificationContext(
                    constantSizeFieldSetter,
                    lastField.nextFieldOffset()
            ).size(fieldSize)

    override fun calculateSize(sizeCalculator: BinaryFieldSizeCalcuator) =
            VariableSizeBinaryFieldSpecificationContextImpl(
                    variableSizeFieldSetter,
                    lastField.nextFieldOffset()
            ).calculateSize(sizeCalculator)

    override fun nextState(fieldID: String): State {
        return when(currentField) {
            is VariableSizeBinaryField -> {
                UnknownOffsetFullBinaryFieldSpecificationContext(
                        fields,
                        fieldID,
                        currentField
                )
            }
            is ConstantSizeBinaryField -> {
                HasPreviousFieldFullBinaryFieldSpecificationContext(
                        fields,
                        fieldID,
                        currentField as ConstantSizeBinaryField
                )
            }
            else -> {
                throw RuntimeException("Unexpected state type ${currentField::class.simpleName}")
            }
        }
    }
}

internal class UnknownOffsetFullBinaryFieldSpecificationContext(
        private val fields: MutableMap<String, BinaryField>,
        private val fieldId: String,
        private val lastField: BinaryField
) : State() {

    private lateinit var currentField: BinaryField

    private val fieldCallbackWrapper = {
        binaryField: BinaryField ->
        fields[fieldId] = binaryField
        currentField = binaryField
    }

    override fun size(fieldSize: Int) =
            ConstantSizeDependentOffsetBinaryFieldSpecificationContextImpl(
                    fieldCallbackWrapper,
                    lastField
            ).size(fieldSize)

    override fun calculateSize(sizeCalculator: BinaryFieldSizeCalcuator) =
            DependentOffsetVariableSizeBinaryFieldSpecificationContextImpl(
                    fieldCallbackWrapper,
                    lastField
            ).calculateSize(sizeCalculator)

    override fun nextState(fieldID: String): State {
        return UnknownOffsetFullBinaryFieldSpecificationContext(
                fields,
                fieldID,
                currentField
        )
    }
}