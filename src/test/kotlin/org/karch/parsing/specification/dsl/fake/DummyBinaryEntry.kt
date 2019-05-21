package org.karch.parsing.specification.dsl.fake

import org.karch.parsing.specification.dsl.domain.BinaryEntry
import org.karch.parsing.specification.dsl.domain.MappingApplyer

class DummyBinaryEntry : BinaryEntry {
    override suspend fun offsetOf(fieldId: String): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun sizeOf(fieldId: String): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun map(fieldId: String): MappingApplyer {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}