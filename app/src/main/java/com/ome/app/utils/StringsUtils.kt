package com.ome.app.utils

fun String.applyMaskToEmail(): String = this.replace(
    "(?<=.)[^@](?=[^@]*?@)|(?:(?<=@.)|(?!^)\\G(?=[^@]*$)).(?=.*\\.)".toRegex(),
    "*"
)
