package com.example.phonerepair.model

class DeviceNotFoundException : Exception()

class DeviceLoadException(
    val canRetry: Boolean,
    cause: Throwable
) : Exception(cause)
