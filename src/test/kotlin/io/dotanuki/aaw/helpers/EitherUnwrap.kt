/*
 * Copyright 2023 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.helpers

import arrow.core.Either

fun <E, V> Either<E, V>.unwrapValue(): V = fold({ throw AssertionError("Should not be Left!\n$it") }, { it })

fun <E, V> Either<E, V>.unwrapError(): E = fold({ it }, { throw AssertionError("Should not be Right!") })
