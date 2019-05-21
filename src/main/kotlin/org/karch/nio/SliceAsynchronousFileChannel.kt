package org.karch.nio

//import org.karch.sio.slice
import java.nio.ByteBuffer
import java.nio.ByteBuffer.allocate
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.nio.channels.FileLock
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

class SliceAsynchronousFileChannel(
        delegateAsynchronousFileChannel: AsynchronousFileChannel,
        private val slice: LongRange
) : DelegateAsynchronousFileChannel(delegateAsynchronousFileChannel) {

    private val positionBase = slice.start

    override fun size(): Long {
        return slice.endInclusive - slice.start
    }

    private fun checkSlice() {
        if (delegateFileChannel.size() < slice.endInclusive
                || slice.start < 0) {
            throw RuntimeException("Can't use $slice as channel part slice")
        }
    }

    private fun checkInSliceBorders(vararg values: Long) {
        for (value in values) {
            if (!slice.contains(value)) {
                throw RuntimeException("value $value not in range of $slice")
            }
        }
    }

    override fun <A : Any?> lock(
            position: Long,
            size: Long,
            shared: Boolean,
            attachment: A,
            handler: CompletionHandler<FileLock, in A>?
    ) {
        checkInSliceBorders(position, size)
        delegateFileChannel.lock(positionBase + position, size, shared, attachment, handler)
    }

    override fun lock(position: Long, size: Long, shared: Boolean): Future<FileLock> {
        checkInSliceBorders(position, size)
        return delegateFileChannel.lock(positionBase + position, size, shared)
    }

    override fun tryLock(position: Long, size: Long, shared: Boolean): FileLock {
        checkInSliceBorders(position, size)
        return delegateFileChannel.tryLock(positionBase + position, size, shared)
    }


    override fun close(): Unit {
        TODO("not implemented")
    }

    override fun truncate(size: Long): AsynchronousFileChannel {
        throw UnsupportedOperationException("Not implemented for slice file")
    }

    override fun <A : Any?> write(src: ByteBuffer?, position: Long, attachment: A, handler: CompletionHandler<Int, in A>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun write(src: ByteBuffer?, position: Long): Future<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <A : Any?> read(dst: ByteBuffer?, position: Long, attachment: A, handler: CompletionHandler<Int, in A>?) {
        if (position >= slice.endInclusive) {
            handler?.completed(-1,attachment)
        }
        if (dst != null && (dst.remaining() + positionBase + position) > slice.endInclusive) {
            val subDst = dst.slice().limit((slice.endInclusive - (positionBase + position)).toInt()) as ByteBuffer
            delegateFileChannel.read(subDst,positionBase + position,attachment,handler)
        } else {
            delegateFileChannel.read(dst, positionBase + position, attachment, handler)
        }
    }

    override fun read(dst: ByteBuffer?, position: Long): Future<Int> {
        if (position >= slice.endInclusive) {
            return FutureTask<Int>{-1}
        }
        if (dst != null && (dst.remaining() + positionBase + position) > slice.endInclusive) {
            val subDst = dst.slice().limit((slice.endInclusive - (positionBase + position)).toInt()) as ByteBuffer
            return delegateFileChannel.read(subDst, positionBase + position)
        }
        return delegateFileChannel.read(dst, positionBase + position)
    }
}
