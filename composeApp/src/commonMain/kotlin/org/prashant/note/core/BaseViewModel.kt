package org.prashant.note.core

import kotlinx.coroutines.CoroutineScope

expect abstract class BaseViewModel() {
    val viewModelScope: CoroutineScope
    protected open fun onCleared()
}
