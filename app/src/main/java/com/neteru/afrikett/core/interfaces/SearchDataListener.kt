package com.neteru.afrikett.core.interfaces

import com.neteru.afrikett.core.enums.PostType
import com.neteru.afrikett.core.models.RemoteDB.Post
import com.neteru.afrikett.core.models.RemoteDB.Showcase

interface SearchDataListener {

    fun onShowcasesDataReady(showcases: List<Showcase>)
    fun onPostsDataReady(posts: List<Post>, postType: PostType)

}
