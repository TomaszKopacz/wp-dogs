package pl.wp.dogs.breeds_list

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import pl.wp.dogs.R
import pl.wp.dogs.breeds_list.BreedsListAction.GoToBreedDetails
import pl.wp.dogs.breeds_list.BreedsListIntent.BreedSelected

@AndroidEntryPoint
class BreedsListFragment : Fragment(R.layout.fragment_breeds_list) {

    private val viewModel: BreedsListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val composeView = view.findViewById<ComposeView>(R.id.compose_view)

        composeView.setContent {
            BreedsListScreen(viewModel)
        }
    }
}

@Composable
private fun BreedsListScreen(
    viewModel: BreedsListViewModel,
) {
    val state by viewModel.state.collectAsState(BreedsListState())
    val action by viewModel.action.collectAsStateWithLifecycle(null)

    LaunchedEffect(action) {
        when (action) {
            is GoToBreedDetails -> {}
            null -> {}
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(state.breeds.count()) { index ->
            Text(
                modifier = Modifier
                    .clickable {
                        viewModel.onIntent(BreedSelected(state.breeds[index]))
                    }
                    .padding(36.dp),
                text = state.breeds[index].name
            )
        }
    }
}
