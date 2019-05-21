package org.karch.parsing.specification

import java.util.concurrent.ConcurrentHashMap

interface Interner<InternedObjectType> {
    operator fun <ReturnedObjectType : InternedObjectType>get(internedObject: ReturnedObjectType): ReturnedObjectType
}

class MapInterner<InternedObjectType>(
        private val delegate: MutableMap<InternedObjectType, InternedObjectType> = ConcurrentHashMap()
) : Interner<InternedObjectType> {

    @Suppress("UNCHECKED_CAST")
    override fun <ReturnedObjectType : InternedObjectType> get(
            internedObject: ReturnedObjectType
    ): ReturnedObjectType {
        return delegate.putIfAbsent(internedObject,internedObject) as ReturnedObjectType? ?: internedObject
    }
}

fun <InternedObjectType> mapInterner(
        delegate: MutableMap<InternedObjectType, InternedObjectType> = HashMap()
): Interner<InternedObjectType> = MapInterner(delegate)


//TODO 28.11.2018 HACK check to remove
internal fun Sequence<ULong>.sum(): ULong {
    var sum = 0UL
    for (element in this) {
        sum += element
    }
    return sum
}