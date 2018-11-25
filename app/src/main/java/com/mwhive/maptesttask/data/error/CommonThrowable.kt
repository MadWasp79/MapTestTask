package com.mwhive.maptesttask.data.error

class CommonThrowable(
        message: String,
        val errorsMap: Map<String, String>? = null
) : Throwable(message)