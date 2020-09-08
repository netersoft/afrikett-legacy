package com.neteru.afrikett.core.interfaces

import com.neteru.afrikett.core.models.RemoteDB.RemoteContactModel

interface RemoteContactListener {
    fun onGetUsersCompleted()
    fun onGetRemoteContactCompleted(list: List<RemoteContactModel>)
    fun onTaskCompleted()
    fun onGetAnything()
    fun onTaskFailure()
}
