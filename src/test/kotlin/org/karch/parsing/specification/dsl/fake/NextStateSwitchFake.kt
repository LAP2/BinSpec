package org.karch.parsing.specification.dsl.fake

import org.junit.jupiter.api.Assertions
import org.karch.parsing.specification.dsl.FullBinaryFieldSpecificationContext
import kotlin.reflect.KClass

class NextStateSwitchFake<NextStateType : FullBinaryFieldSpecificationContext>(
        private val expectedNextStateClass: KClass<NextStateType>
) : (FullBinaryFieldSpecificationContext) -> Unit {
    override fun invoke(nextState: FullBinaryFieldSpecificationContext) {
        Assertions.assertEquals(expectedNextStateClass, nextState::class)

    }
}

inline fun <reified NextStateType : FullBinaryFieldSpecificationContext> expectedNextStateFake()
        : (FullBinaryFieldSpecificationContext)->Unit = NextStateSwitchFake(NextStateType::class)