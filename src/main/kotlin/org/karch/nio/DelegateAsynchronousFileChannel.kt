package org.karch.nio

import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.nio.channels.FileLock
import java.util.concurrent.Future

open class DelegateAsynchronousFileChannel(
        protected val delegateFileChannel: AsynchronousFileChannel
) : AsynchronousFileChannel() {
    override fun <A : Any?> lock(
            position: Long,
            size: Long,
            shared: Boolean,
            attachment: A,
            handler: CompletionHandler<FileLock, in A>?
    ): Unit = delegateFileChannel.lock(
            position,
            size,
            shared,
            attachment,
            handler
    )

    override fun lock(position: Long, size: Long, shared: Boolean): Future<FileLock> =
            delegateFileChannel.lock(position,size, shared)

    override fun isOpen(): Boolean = delegateFileChannel.isOpen()

    override fun tryLock(position: Long, size: Long, shared: Boolean): FileLock =
            delegateFileChannel.tryLock(position, size, shared)

    override fun size(): Long = delegateFileChannel.size()

    override fun <A : Any?> write(
            src: ByteBuffer?,
            position: Long,
            attachment: A,
            handler: CompletionHandler<Int, in A>?
    ): Unit = delegateFileChannel.write(
            src,
            position,
            attachment,
            handler
    )

    override fun write(src: ByteBuffer?, position: Long): Future<Int> =
            delegateFileChannel.write(src,position)

    override fun force(metaData: Boolean): Unit = delegateFileChannel.force(metaData)

    override fun close(): Unit = delegateFileChannel.close()

    override fun truncate(size: Long): AsynchronousFileChannel = delegateFileChannel.truncate(size)

    override fun <A : Any?> read(
            dst: ByteBuffer?,
            position: Long,
            attachment: A,
            handler: CompletionHandler<Int, in A>?
    ): Unit = delegateFileChannel.read(
            dst,
            position,
            attachment,
            handler
    )

    override fun read(dst: ByteBuffer?, position: Long): Future<Int> =
            delegateFileChannel.read(dst, position)

}