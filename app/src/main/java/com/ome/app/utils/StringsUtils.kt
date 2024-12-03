package com.ome.app.utils

fun String.applyMaskToEmail(): String = replace(
    "(?<=.)[^@](?=[^@]*?@)".toRegex(),
    "*"
)
