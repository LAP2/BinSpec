package org.karch.nio

import java.nio.ByteBuffer
import java.nio.ByteBuffer.allocate
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.nio.channels.FileLock
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.Future

class AsynchronousFileChannelFake(
        @Volatile private var fileData: ByteBuffer
) : AsynchronousFileChannel() {

    private var isOpen: Boolean = true

    override fun isOpen(): Boolean = isOpen
    override fun close() {
        isOpen = false
    }

    override fun <A : Any?> lock(position: Long, size: Long, shared: Boolean, attachment: A, handler: CompletionHandler<FileLock, in A>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun lock(position: Long, size: Long, shared: Boolean): Future<FileLock> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun tryLock(position: Long, size: Long, shared: Boolean): FileLock {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun force(metaData: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun size(): Long = fileData.capacity().toLong()

    override fun truncate(size: Long): AsynchronousFileChannel {
        reallocateFileData(size.toInt())
        return this
    }

    override fun <A : Any?> read(dst: ByteBuffer?, position: Long, attachment: A, handler: CompletionHandler<Int, in A>?) {
        if (handler == null) {
            throw IllegalArgumentException("handler could not be null")
        } else {
            if (dst == null) {
                handler.failed(IllegalArgumentException("destination buffer could not be null"), attachment)
            } else {
                val src = fileData.slice().position(position.toInt()) as ByteBuffer
                if (dst.remaining() < src.remaining()) {
                    src.limit(position.toInt() + dst.remaining())
                }
                val ret = src.remaining()
                dst.put(src)
                handler.completed(ret, attachment)
            }
        }
    }

    override fun read(dst: ByteBuffer?, position: Long): Future<Int> {
        if (dst == null) {
            throw IllegalArgumentException("destination buffer could not be null")
        }
        val src = fileData.slice().position(position.toInt()) as ByteBuffer
        if (dst.remaining() < src.remaining()) {
            src.limit(dst.remaining())
        }
        val ret = src.remaining()
        dst.put(src)
        return completedFuture(ret)
    }

    private fun reallocateFileData(newSize: Int) {
        fileData = allocate(newSize)
    }

    override fun <A : Any?> write(src: ByteBuffer?, position: Long, attachment: A, handler: CompletionHandler<Int, in A>?) {
        if (handler == null) {
            throw IllegalArgumentException("handler could not be null")
        } else {
            if (src == null) {
                handler.failed(IllegalArgumentException("source buffer could not be null"), attachment)
            } else {
                val dst = fileData.slice().position(position.toInt()) as ByteBuffer
                if (dst.remaining() < src.remaining()) {
                    reallocateFileData(fileData.capacity() + (src.remaining() - dst.remaining()))
                    val ret = src.remaining()
                    (fileData.slice().position(position.toInt()) as ByteBuffer).put(src)
                    handler.completed(ret,attachment)
                } else {
                    val ret = src.remaining()
                    dst.put(src)
                    handler.completed(ret,attachment)
                }
            }
        }
    }

    override fun write(src: ByteBuffer?, position: Long): Future<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}