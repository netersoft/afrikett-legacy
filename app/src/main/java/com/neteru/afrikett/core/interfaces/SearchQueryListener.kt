package com.neteru.afrikett.core.interfaces

interface SearchQueryListener {

    fun onQueryTextSubmit(query: String)
    fun onQueryTextChange(newText: String)

}
