package pl.wp.dogs.breeds_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import pl.wp.dogs.breeds_list.BreedsListAction.GoToBreedDetails
import pl.wp.dogs.breeds_list.BreedsListIntent.BreedSelected
import javax.inject.Inject

internal data class BreedsListState(
    val breeds: List<Breed> = emptyList(),
    val isError: Boolean = false,
)

internal sealed class BreedsListIntent {
    data class BreedSelected(val breed: Breed) : BreedsListIntent()
}

internal sealed class BreedsListAction {
    data class GoToBreedDetails(val breed: Breed) : BreedsListAction()
}

@HiltViewModel
internal class BreedsListViewModel @Inject constructor(
    private val getBreedsListUseCase: GetBreedsListUseCase,
) : ViewModel() {

    private var _state = MutableStateFlow(BreedsListState())
    val state: Flow<BreedsListState> = _state

    private var _action = MutableSharedFlow<BreedsListAction>()
    val action: Flow<BreedsListAction> = _action

    init {
        viewModelScope.launch {
            getBreedsListUseCase()
                .flowOn(Dispatchers.IO)
                .catch { error ->
                    emitState { state -> state.copy(isError = true) }
                    reportBreedsListError(error)
                }
                .catch {
                    emitState { state -> state.copy(isError = true) }
                }
                .collect { breeds ->
                    emitState { state -> state.copy(breeds = breeds) }
                }
        }
    }

    fun onIntent(intent: BreedsListIntent) = when (intent) {
        is BreedSelected -> emitAction(GoToBreedDetails(intent.breed))
    }

    private fun reportBreedsListError(error: Throwable): Flow<Unit> =
        getBreedsListUseCase.reportError(error)
            .flowOn(Dispatchers.IO)
            .catch {
                emitState { it.copy(isError = true) }
                emit(Unit)
            }

    private fun emitState(transform: (BreedsListState) -> BreedsListState) =
        viewModelScope.launch(Dispatchers.Main) {
            _state.emit(transform(_state.value))
        }

    private fun emitAction(action: BreedsListAction) =
        viewModelScope.launch(Dispatchers.Main) {
            _action.emit(action)
        }
}
