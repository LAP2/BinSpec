package org.karch.parsing.specification.dsl.domain

import org.karch.nio.aRead
import java.nio.ByteBuffer
import java.nio.ByteBuffer.allocate
import java.nio.ByteOrder.LITTLE_ENDIAN
import java.nio.channels.AsynchronousFileChannel
import java.util.*

sealed class BinaryField

//======================================================================================================================

interface ConstantSize {
    val size: Long
}

interface KnownOffset {
    val offset: Long
}

interface VariableSize {
    val sizeCalcuator: BinaryFieldSizeCalcuator
}

interface DependentOfsset {
    val offsetCalcuator: BinaryFieldOffsetCalcuator
}

data class ConstantSizeBinaryField(
        override val offset: Long,
        override val size: Long
) : BinaryField(), ConstantSize, KnownOffset

typealias BinaryFieldSizeCalcuator = suspend BinaryEntry.()->Long
typealias BinaryFieldOffsetCalcuator = suspend BinaryEntry.()->Long

class VariableSizeBinaryField(
        override val offset: Long,
        override val sizeCalcuator: BinaryFieldSizeCalcuator
) : BinaryField(), KnownOffset, VariableSize

class DependentOffsetConstantSizeBinaryField(
        override val offsetCalcuator: BinaryFieldOffsetCalcuator,
        override val size: Long
) : BinaryField(), DependentOfsset, ConstantSize

class DependentOffsetVariableSizeBinaryField(
        override val offsetCalcuator: BinaryFieldOffsetCalcuator,
        override val sizeCalcuator: BinaryFieldSizeCalcuator
) : BinaryField(), DependentOfsset, VariableSize

//======================================================================================================================

fun BinaryField.offsetCalculator(): BinaryFieldOffsetCalcuator {
    return when(this) {
        is ConstantSizeBinaryField -> {
            {this@offsetCalculator.offset + this@offsetCalculator.size}
        }
        is VariableSizeBinaryField -> {
            {this@offsetCalculator.offset + this.(this@offsetCalculator.sizeCalcuator)()}
        }
        is DependentOffsetConstantSizeBinaryField -> {
            {this.(this@offsetCalculator.offsetCalcuator)() + this@offsetCalculator.size}
        }
        is DependentOffsetVariableSizeBinaryField -> {
            {this.(this@offsetCalculator.offsetCalcuator)() + this.(this@offsetCalculator.sizeCalcuator)()}
        }
    }
}

//======================================================================================================================

interface BinaryEntrySpecification {
    val minimalSize: Long
    fun binaryEntry(
            channel: AsynchronousFileChannel, offset: Long
    ): BinaryEntry
}

class BinaryEntrySpecificationImpl(
        override val minimalSize: Long,
        private val fields: Map<String, BinaryField>
) : BinaryEntrySpecification {

    override fun binaryEntry(channel: AsynchronousFileChannel, offset: Long): BinaryEntry {
        return BinaryEntryImpl(channel,offset,fields)
    }

}

//======================================================================================================================

typealias BinaryFieldMapping<ResultObjectType> = suspend BinaryEntry.(
        channel: AsynchronousFileChannel,
        binaryFieldId: String
)-> ResultObjectType

class DefaultMappings private constructor() {
    companion object {

        @JvmStatic
        val defaultLongMapping: BinaryFieldMapping<Long> = {
            channel, binaryFieldId ->
            val size = sizeOf(binaryFieldId).toInt()
            if (size == Long.SIZE_BYTES) {
                val buffer = allocate(size).order(LITTLE_ENDIAN)
                channel.aRead(buffer,offsetOf(binaryFieldId))
                (buffer.rewind() as ByteBuffer).long
            } else {
                throw RuntimeException("field size $size is differs of Long size ${Long.SIZE_BYTES}")
            }
        }

        @JvmStatic
        val defaultIntMapping : BinaryFieldMapping<Int> = {
            channel, binaryFieldId ->
            val size = sizeOf(binaryFieldId).toInt()
            if (size == Int.SIZE_BYTES) {
                val buffer = allocate(size).order(LITTLE_ENDIAN)
                channel.aRead(buffer,offsetOf(binaryFieldId))
                (buffer.rewind() as ByteBuffer).int
            } else {
                throw RuntimeException("field size $size is differs of Long size ${Int.SIZE_BYTES}")
            }
        }

        @JvmStatic
        val defaultShortMapping : BinaryFieldMapping<Short> = {
            channel, binaryFieldId ->
            val size = sizeOf(binaryFieldId).toInt()
            if (size == Short.SIZE_BYTES) {
                val buffer = allocate(size).order(LITTLE_ENDIAN)
                channel.aRead(buffer,offsetOf(binaryFieldId))
                (buffer.rewind() as ByteBuffer).short
            } else {
                throw RuntimeException("field size $size is differs of Short size ${Short.SIZE_BYTES}")
            }
        }

        @JvmStatic
        val defaultStringMapping : BinaryFieldMapping<String> = {
            channel, binaryFieldId ->
            val size = sizeOf(binaryFieldId).toInt()
            val buffer = allocate(size).order(LITTLE_ENDIAN)
            channel.aRead(buffer, offsetOf(binaryFieldId))
            String(buffer.rewind().array())
        }

        @JvmStatic
        val defaultBitSetMapping : BinaryFieldMapping<BitSet> = {
            channel, binaryFieldId ->
            val size = sizeOf(binaryFieldId).toInt()
            val buffer = allocate(size).order(LITTLE_ENDIAN)
            channel.aRead(buffer, offsetOf(binaryFieldId))
            BitSet.valueOf(buffer)
        }

    }
}

interface MappingApplyer {
    suspend infix fun <ResultObjectType> using(mapping: BinaryFieldMapping<ResultObjectType>): ResultObjectType
}

class MapingApplyerImpl(
        private val binaryEntry: BinaryEntry,
        private val channel: AsynchronousFileChannel,
        private val binaryFieldID: String
) : MappingApplyer {

    override suspend infix fun <ResultObjectType> using(mapping: BinaryFieldMapping<ResultObjectType>): ResultObjectType {
        return binaryEntry.mapping(channel, binaryFieldID)
    }

}

interface BinaryEntry {
    suspend infix fun offsetOf(fieldId: String): Long
    suspend infix fun sizeOf(fieldId: String): Long
    suspend infix fun map(fieldId: String): MappingApplyer
}

class BinaryEntryImpl(
        private val channel: AsynchronousFileChannel,
        private val entryOffset: Long,
        private val binaryFields: Map<String,BinaryField>
) : BinaryEntry {

    override suspend fun map(fieldId: String): MappingApplyer = MapingApplyerImpl(this, channel, fieldId)


    override suspend fun offsetOf(fieldId: String): Long {
        val field = tryResolveField(fieldId)
        return when(field) {
            is ConstantSizeBinaryField -> field.offset
            is VariableSizeBinaryField -> field.offset
            is DependentOffsetConstantSizeBinaryField -> {
                val calculateOffset = field.offsetCalcuator
                this@BinaryEntryImpl.calculateOffset()
            }
            is DependentOffsetVariableSizeBinaryField -> {
                val calculateOffset = field.offsetCalcuator
                this@BinaryEntryImpl.calculateOffset()
            }
        } + entryOffset
    }

    override suspend fun sizeOf(fieldId: String): Long {
        val field = tryResolveField(fieldId)
        return when(field) {
            is ConstantSizeBinaryField -> field.size
            is DependentOffsetConstantSizeBinaryField -> field.size
            is VariableSizeBinaryField -> {
                val calculateSize = field.sizeCalcuator
                this@BinaryEntryImpl.calculateSize()
            }
            is DependentOffsetVariableSizeBinaryField -> {
                val calculateSize = field.sizeCalcuator
                this@BinaryEntryImpl.calculateSize()
            }
        }
    }

    private fun tryResolveField(id: String): BinaryField {
        return binaryFields[id] ?: throw RuntimeException("Field with id $id not found")
    }

}