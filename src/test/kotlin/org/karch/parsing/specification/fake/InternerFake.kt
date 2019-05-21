package org.karch.parsing.specification.fake

import org.junit.jupiter.api.Assertions.assertEquals
import org.karch.parsing.specification.Interner

class InternerFake<InternedObjectType>(
        private val expectedObject: InternedObjectType
) : Interner<InternedObjectType> {
    override fun <ReturnedObjectType : InternedObjectType> get(
            internedObject: ReturnedObjectType
    ): ReturnedObjectType {
        assertEquals(expectedObject, internedObject)
        return internedObject
    }
}

fun <InternedObjectType> InternedObjectType.fakeInterner(): Interner<InternedObjectType> =
        InternerFake(this)