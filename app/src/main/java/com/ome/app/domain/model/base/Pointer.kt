package com.ome.app.domain.model.base

/**
 * Represents an Pointer value.
 *
 * This class is used as a pointer(*) to an angle value.
 */
data class Pointer<T>(var value: T? = null)