package com.multiplatform.themoviedatabase.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

interface State
interface Action
interface Reducer<S: State, A: Action> {
    /**
     * Generates a new instance of the [State] based on the [Action]
     *
     * @param state the current [State]
     * @param action the [Action] to reduce the [State] with
     * @return the reduced [State]
     */
    fun reduce(state: S, action: A): S
}

interface Dispatcher<A: Action> {
    fun dispatch(action: A)
}

abstract class MiddleWare<S: State, A: Action>() {
    private lateinit var dispatcher: Dispatcher<A>

    abstract suspend fun process(state: S, action: A)

    protected fun dispatch(action: A) = dispatcher.dispatch(action)

    internal fun setDispatcher(
        dispatcher: Dispatcher<A>,
    ) {
        this.dispatcher = dispatcher
    }
}



open class MviViewModel<S: State, A: Action>(
    private val reducer: Reducer<S, A>,
    private val middleWares: List<MiddleWare<S, A>> = emptyList(),
    initialState: S
): ViewModel(), Dispatcher<A> {

    private val actions = MutableSharedFlow<A>(extraBufferCapacity = 64)

    var state: S by mutableStateOf(initialState)
        private set

    init {
        middleWares.forEach { middleWare -> middleWare.setDispatcher(this) }
        viewModelScope.launch {
            actions.onEach { action ->
                middleWares.forEach { middleWare ->
                    middleWare.process(state, action)
                }
            }.collect()
        }
        viewModelScope.launch {
            actions.collect {
                state = reducer.reduce(state, it)
            }
        }
    }

    override fun dispatch(action: A) {
        val success = actions.tryEmit(action)
        if (!success) error("MVI action buffer overflow")
    }
}