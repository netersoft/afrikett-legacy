package com.neteru.afrikett.core.interfaces

import com.neteru.afrikett.core.models.RemoteDB.LocalContactModel

interface LocalContactListener {

    fun onGetContactStart()
    fun onGetLocalContactCompleted(nameList: List<String>,
                                    phoneNumberList: List<String>,
                                    localContactList: List<LocalContactModel>)

}
