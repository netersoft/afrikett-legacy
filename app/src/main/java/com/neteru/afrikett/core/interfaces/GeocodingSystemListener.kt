package com.neteru.afrikett.core.interfaces

import android.location.Address

interface GeocodingSystemListener {
    fun onTaskStarted()
    fun onTaskCompleted(addresses: Address)
    fun onErrorOccurred()
}
