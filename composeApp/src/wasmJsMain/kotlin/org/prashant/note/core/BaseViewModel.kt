package org.prashant.note.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

actual abstract class BaseViewModel actual constructor() {
    private val job = SupervisorJob()
    actual val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.Main + job)
    actual protected open fun onCleared() {
        job.cancel()
    }
}