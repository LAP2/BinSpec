package org.karch.parsing.specification.dsl.implementation

import org.karch.parsing.specification.dsl.domain.BinaryField
import org.karch.parsing.specification.dsl.BinaryEntrySpecificationContext
import org.karch.parsing.specification.dsl.FullBinaryFieldSpecificationContext

internal interface StateMachine {
    fun nextState(fieldID: String): State
}

internal class SM(
        private val fields: MutableMap<String, BinaryField>
) : StateMachine {

    private lateinit var currentState: State

    override fun nextState(fieldID: String): State {
        if (!this::currentState.isInitialized) {
            currentState = ZeroOffsetFullBinaryFieldSpecificationContext(
                    fields,
                    fieldID
            )
        } else {
            currentState = currentState.nextState(fieldID)
        }
        return currentState
    }
}

class BinaryEntrySpecificationContextImpl(
        fields: MutableMap<String, BinaryField>
) : BinaryEntrySpecificationContext {

    private val stateMachine: StateMachine = SM(fields)

    override fun field(id: String): FullBinaryFieldSpecificationContext {
        return stateMachine.nextState(id)
    }

}