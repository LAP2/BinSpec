package org.karch

import java.util.Random
import kotlin.streams.asSequence

internal fun randomString(length: Long): String = String(Random()
        .ints(length,
                33,
                127
        ).asSequence()
        .map { it.toByte() }
        .toList().toByteArray())
