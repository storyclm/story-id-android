package ru.breffi.storyidsample.ui.passport.adapter

import androidx.annotation.NonNull
import ru.breffi.storyidsample.ui.common.recycler.MultiDiffAdapter
import ru.breffi.storyidsample.ui.passport.binder.PageItemViewBinder

class PageAdapter(@NonNull listener: Listener) : MultiDiffAdapter() {

    interface Listener : PageItemViewBinder.Listener

    init {
        addBinder(TYPE_PAGE_ITEM, PageItemViewBinder(listener))
    }

    companion object {

        val TYPE_PAGE_ITEM = 10
    }

}