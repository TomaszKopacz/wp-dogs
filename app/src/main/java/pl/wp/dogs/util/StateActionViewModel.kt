package pl.wp.dogs.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

open class StateActionViewModel<STATE, ACTION>(
    initialState: STATE
) : ViewModel() {

    private var _state = MutableStateFlow(initialState)
    val state: Flow<STATE> = _state

    private var _action = MutableSharedFlow<ACTION>()
    val action: Flow<ACTION> = _action

    protected fun emitState(state: STATE) =
        viewModelScope.launch(Dispatchers.Main) {
            _state.value = state
        }

    protected fun emitAction(action: ACTION) =
        viewModelScope.launch(Dispatchers.Main) {
            _action.emit(action)
        }
}
