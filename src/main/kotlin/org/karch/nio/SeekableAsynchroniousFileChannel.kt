package org.karch.nio

import java.nio.ByteBuffer
import java.nio.channels.AsynchronousChannel
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.OpenOption
import java.nio.file.Path

interface SeekableFileChannel {
    var position: Long
}

class SeekableAsynchroniousFileChannel private constructor(
        private val delegate: AsynchronousFileChannel
) : AsynchronousChannel by delegate, SeekableFileChannel {

    @Volatile override var position: Long = 0L
    set(value) {
        field = if (value < 0) 0 else value
    }

    companion object {
        @JvmStatic fun open(
                path: Path,
                vararg openOptions: OpenOption
        ): SeekableAsynchroniousFileChannel {
            val delegate = AsynchronousFileChannel.open(path, *openOptions)
            return SeekableAsynchroniousFileChannel(delegate)
        }
    }

    suspend fun read(buffer: ByteBuffer): Int {
        val ret = delegate.aRead(buffer,position)
        if (ret >= 0) position += ret
        return ret
    }

    fun size(): Long = delegate.size()

}