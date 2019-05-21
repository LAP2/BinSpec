package org.karch.nio

import java.nio.ByteBuffer
import java.nio.ByteBuffer.allocate
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.util.concurrent.Future

class BufferedAsynchronousFileChannel(
        delegateFileChannel: AsynchronousFileChannel,
        private val buffer: ByteBuffer = allocate(DEFAULT_BUFFER_SIZE)
) : DelegateAsynchronousFileChannel(delegateFileChannel) {

    companion object {
        @JvmStatic val DEFAULT_BUFFER_SIZE = 8192
    }

    override fun <A> read(
            dst: ByteBuffer?,
            position: Long,
            attachment: A,
            handler: CompletionHandler<Int, in A>?
    ) {
        TODO("Not implemented")
    }

    override fun read(
            dst: ByteBuffer?,
            position: Long
    ): Future<Int> {
        TODO("Not implemented")
    }

    private fun fill() {

    }

    override fun <A> write(src: ByteBuffer?, position: Long, attachment: A, handler: CompletionHandler<Int, in A>?) {
        TODO("Not implemented")
    }

    override fun write(src: ByteBuffer?, position: Long): Future<Int> {
        TODO("Not implemented")
    }
}