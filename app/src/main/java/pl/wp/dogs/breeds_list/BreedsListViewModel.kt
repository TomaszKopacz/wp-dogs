package pl.wp.dogs.breeds_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BreedsListViewModel @Inject constructor(
    private val getBreedsListUseCase: GetBreedsListUseCase,
) : ViewModel() {

    init {
        viewModelScope.launch {
            getBreedsListUseCase()
                .flowOn(Dispatchers.IO)
                .catch {
                    withContext(Dispatchers.Main) {
                        // TODO: view.showError(it)
                    }
                    reportError(it)
                }
                .catch {
                    // TODO: view.showError(it)
                }
                .collect { breeds ->
                    // TODO view.showBreeds(breeds)
                }
        }
    }

    private fun reportError(error: Throwable): Flow<Unit> =
        getBreedsListUseCase.reportError(error)
            .flowOn(Dispatchers.IO)
            .catch {
                withContext(Dispatchers.Main) {
                    // TODO: view.showError(it)
                }
                emit(Unit)
            }
}