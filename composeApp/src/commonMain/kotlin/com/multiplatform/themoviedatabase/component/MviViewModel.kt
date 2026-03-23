package com.multiplatform.themoviedatabase.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

interface State
interface Intent
interface Reducer<S: State, A: Intent> {
    /**
     * Generates a new instance of the [State] based on the [Intent]
     *
     * @param state the current [State]
     * @param intent the [Intent] to reduce the [State] with
     * @return the reduced [State]
     */
    suspend fun reduce(state: S, intent: A): S
}

interface Dispatcher<A: Intent> {
    fun dispatch(action: A)
}

abstract class MiddleWare<S: State, I: Intent>() {
    private lateinit var dispatcher: Dispatcher<I>

    abstract suspend fun process(state: S, intent: I)

    protected fun dispatch(intent: I) = dispatcher.dispatch(intent)

    internal fun setDispatcher(
        dispatcher: Dispatcher<I>,
    ) {
        this.dispatcher = dispatcher
    }
}



open class MviViewModel<S: State, I: Intent>(
    private val reducer: Reducer<S, I>,
    initialState: S,
    private val coroutineScope: CoroutineScope? = null,
): ViewModel(), Dispatcher<I> {

    private val actions = MutableSharedFlow<I>(extraBufferCapacity = 64)

    var state: S by mutableStateOf(initialState)
        private set

    private val scope: CoroutineScope get() = coroutineScope ?: viewModelScope

    init {
        scope.launch {
            actions.collect {
                state = reducer.reduce(state, it)
            }
        }
    }

    override fun dispatch(action: I) {
        val success = actions.tryEmit(action)
        if (!success) error("MVI action buffer overflow")
    }
}