package com.aescolar.apimodule

class TimeProvider {
    val time: Long
        get() = System.currentTimeMillis()
}