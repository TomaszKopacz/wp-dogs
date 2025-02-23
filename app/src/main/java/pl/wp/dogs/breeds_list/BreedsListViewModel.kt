package pl.wp.dogs.breeds_list

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import pl.wp.dogs.breeds_list.BreedsListAction.GoToBreedDetails
import pl.wp.dogs.breeds_list.BreedsListIntent.BreedSelected
import pl.wp.dogs.breeds_list.BreedsListState.Error
import pl.wp.dogs.breeds_list.BreedsListState.Loading
import pl.wp.dogs.breeds_list.BreedsListState.Success
import pl.wp.dogs.model.Breed
import pl.wp.dogs.util.StateActionViewModel
import javax.inject.Inject

internal sealed class BreedsListState {
    data object Loading : BreedsListState()
    data class Success(val breeds: List<Breed> = emptyList()) : BreedsListState()
    data object Error : BreedsListState()
}

internal sealed class BreedsListIntent {
    data class BreedSelected(val breed: Breed) : BreedsListIntent()
}

internal sealed class BreedsListAction {
    data class GoToBreedDetails(val breed: Breed) : BreedsListAction()
}

@HiltViewModel
internal class BreedsListViewModel @Inject constructor(
    private val getBreedsListUseCase: GetBreedsListUseCase,
) : StateActionViewModel<BreedsListState, BreedsListAction>(initialState = Loading) {

    fun fetchBreedsList() = viewModelScope.launch {
        getBreedsListUseCase()
            .catch { error ->
                emitState(Error)
                reportBreedsListError(error)
            }
            .catch {
                emitState(Error)
            }
            .collect { breeds ->
                emitState(Success(breeds))
            }
    }

    fun onIntent(intent: BreedsListIntent) = when (intent) {
        is BreedSelected -> emitAction(GoToBreedDetails(intent.breed))
    }

    private fun reportBreedsListError(error: Throwable): Flow<Unit> =
        getBreedsListUseCase.reportError(error)
            .catch {
                emitState(Error)
                emit(Unit)
            }
            .flowOn(Dispatchers.IO)
}
