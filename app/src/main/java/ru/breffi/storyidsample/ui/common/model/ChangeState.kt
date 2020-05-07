package ru.breffi.storyidsample.ui.common.model

class ChangeState {

    private val items = mutableMapOf<String, Boolean>()
    private var listener: ((Boolean) -> Unit)? = null

    fun itemChanged(tag: String, changed: Boolean) {
        items[tag] = changed
        listener?.invoke(isChanged())
    }

    fun reset() {
        items.clear()
    }

    fun setChangeListener(listener: (Boolean) -> Unit) {
        this.listener = listener
    }

    fun isChanged(): Boolean {
        return items.values.any { it }
    }
}