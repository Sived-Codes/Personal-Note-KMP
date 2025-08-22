package org.prashant.note.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope as androidScope
import kotlinx.coroutines.CoroutineScope

actual abstract class BaseViewModel actual constructor() : ViewModel() {
    actual val viewModelScope: CoroutineScope get() = androidScope
    actual override fun onCleared() {
        super.onCleared()
    }
}
