/*
 * Copyright 2025 Dotanuki Labs
 * SPDX-License-Identifier: MIT
 */

package io.dotanuki.aaw.helpers

fun Any.fixtureFromResources(target: String): String = requireNotNull(javaClass.classLoader.getResource(target)).path
