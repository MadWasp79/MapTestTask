package com.mwhive.maptesttask.utilsandextensions.extensions

fun Boolean?.falseIfNull() = this ?: false

fun Boolean?.toInt() = if (this == true) 1 else 0