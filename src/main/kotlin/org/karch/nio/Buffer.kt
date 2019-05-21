package org.karch.nio

import java.nio.ByteBuffer

interface BufferBase {
    var position: Int
    var limit: Int
    val capacity: Int
    val hasRemaining: Boolean
    val remaining: Int
}

interface ImmutableByteBuffer : BufferBase {
    operator fun get(index: Int = position): Byte
    fun getChar(index: Int = position): Char
    fun getDouble(index: Int = position): Double
    fun getFloat(index: Int = position): Float
    fun getInt(index: Int = position): Int
    fun getLong(index: Int = position): Long
    fun getShort(index: Int = position): Short
    fun slice(range: IntRange): ImmutableByteBuffer
}

interface MutableByteBuffer : ImmutableByteBuffer {
    operator fun set(index: Int = position, value: Byte)
    fun setChar(index: Int = position, value: Char)
    override fun slice(range: IntRange): MutableByteBuffer
    // TODO 10.12.2018 add setters implementation
}

inline class MutableByteBufferImpl(
        private val bufferDelegate: ByteBuffer
) : MutableByteBuffer {

    override val capacity: Int
        get() = bufferDelegate.capacity()

    override val hasRemaining: Boolean
        get() = bufferDelegate.hasRemaining()

    override val remaining: Int
        get() = bufferDelegate.remaining()

    override var position: Int
        get() = bufferDelegate.position()
        set(value) {
            bufferDelegate.position(value)
        }

    override var limit: Int
        get() = bufferDelegate.limit()
        set(value) {
            bufferDelegate.limit(value)
        }

    override fun getDouble(index: Int): Double = bufferDelegate.getDouble(index)

    override fun getFloat(index: Int): Float = bufferDelegate.getFloat(index)

    override fun getInt(index: Int): Int = bufferDelegate.getInt(index)

    override fun getLong(index: Int): Long = bufferDelegate.getLong(index)

    override fun getShort(index: Int): Short = bufferDelegate.getShort(index)

    override fun get(index: Int): Byte = bufferDelegate[index]

    override fun getChar(index: Int): Char = bufferDelegate.getChar(index)

    override fun set(index: Int, value: Byte) {
        bufferDelegate.put(index, value)
    }

    override fun setChar(index: Int, value: Char) {
        bufferDelegate.putChar(index, value)
    }

    override fun slice(range: IntRange): MutableByteBuffer {
        val currentPosition = position
        val currentLimit = limit
        position = range.start
        limit = range.endInclusive
        val ret = MutableByteBufferImpl(bufferDelegate.slice())
        position = currentPosition
        limit = currentLimit
        return ret
    }
}