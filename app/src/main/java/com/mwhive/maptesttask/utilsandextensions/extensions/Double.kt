package com.mwhive.maptesttask.utilsandextensions.extensions

/**
 * Crop double to [length] digits after comma and return as string
 */

fun Double.cropToString(length:Int=2): String {
    return "%.${length}f".format(this)
}