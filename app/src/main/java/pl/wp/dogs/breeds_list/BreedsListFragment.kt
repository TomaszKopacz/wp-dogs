package pl.wp.dogs.breeds_list

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import pl.wp.dogs.R
import pl.wp.dogs.breeds_list.BreedsListAction.GoToBreedDetails
import pl.wp.dogs.breeds_list.BreedsListIntent.BreedSelected
import pl.wp.dogs.breeds_list.BreedsListState.Error
import pl.wp.dogs.breeds_list.BreedsListState.Loading
import pl.wp.dogs.breeds_list.BreedsListState.Success
import pl.wp.dogs.model.Breed
import javax.inject.Inject

@AndroidEntryPoint
class BreedsListFragment @Inject constructor() : Fragment(R.layout.fragment_breeds_list) {

    private val viewModel: BreedsListViewModel by viewModels()

    @Inject
    lateinit var navigationManager: NavigationManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val composeView = view.findViewById<ComposeView>(R.id.compose_view)

        composeView.setContent {
            BreedsListScreen(viewModel, navigationManager)
        }
    }
}

@Composable
private fun BreedsListScreen(
    viewModel: BreedsListViewModel,
    navigationManager: NavigationManager,
) {
    val state by viewModel.state.collectAsState(Loading)
    val action by viewModel.action.collectAsStateWithLifecycle(null)

    LaunchedEffect(action) {
        action?.let {
            when (it) {
                is GoToBreedDetails -> navigationManager.goToBreed(it.breed)
            }
        }
    }

    when (state) {
        is Loading -> BreedsListLoading()
        is Success -> BreedsListContent(
            breeds = (state as Success).breeds,
            onBreedSelected = { viewModel.onIntent(BreedSelected(it)) },
        )

        is Error -> BreedsListError()
    }
}

@Composable
private fun BreedsListLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(36.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun BreedsListContent(
    breeds: List<Breed>,
    onBreedSelected: (Breed) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(breeds.count()) { index ->
            val breed = breeds[index]

            Text(
                modifier = Modifier
                    .clickable { onBreedSelected(breed) }
                    .fillMaxWidth()
                    .padding(36.dp),
                text = breed.name
            )
        }
    }
}

@Composable
private fun BreedsListError() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(36.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Failed to load breeds list",
            style = TextStyle().copy(color = Color.Red)
        )
    }
}
