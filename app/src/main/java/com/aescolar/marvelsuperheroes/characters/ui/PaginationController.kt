package com.aescolar.marvelsuperheroes.characters.ui

import com.paginate.Paginate

class PaginationController(
    val onLoadMoreCallback: () -> Unit
) : Paginate.Callbacks {
    var hasLoadedAllItems: Boolean = false
    var contentsLoading: Boolean = false

    override fun onLoadMore() {
        onLoadMoreCallback()
    }

    override fun isLoading(): Boolean {
        return contentsLoading
    }

    override fun hasLoadedAllItems(): Boolean {
        return hasLoadedAllItems
    }
}
