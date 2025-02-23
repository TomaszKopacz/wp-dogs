package pl.wp.dogs.breeds_list

import app.cash.turbine.test
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import pl.wp.dogs.MainDispatcherRule
import pl.wp.dogs.breeds_list.BreedsListAction.GoToBreedDetails
import pl.wp.dogs.breeds_list.BreedsListIntent.BreedSelected
import pl.wp.dogs.breeds_list.BreedsListState.Error
import pl.wp.dogs.breeds_list.BreedsListState.Loading
import pl.wp.dogs.breeds_list.BreedsListState.Success
import pl.wp.dogs.model.Breed

class BreedsListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var getBreedsListUseCase: GetBreedsListUseCase

    private lateinit var viewModel: BreedsListViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        viewModel = BreedsListViewModel(getBreedsListUseCase)

        whenever(getBreedsListUseCase()).thenReturn(flowOf(emptyList()))
        whenever(getBreedsListUseCase.reportError(any())).thenReturn(flowOf(Unit))
    }

    @Test
    fun `should emit Loading state initially`() = runTest {
        viewModel.state.test {
            assertEquals(Loading, awaitItem())
        }
    }

    @Test
    fun `should emit Success when use case returns breeds list`() = runTest {
        val breeds = listOf(
            Breed("Labrador"),
            Breed("Poodle"),
        )

        whenever(getBreedsListUseCase()).thenReturn(flowOf(breeds))

        viewModel.state.test {
            viewModel.fetchBreedsList()

            assertEquals(Loading, awaitItem())
            assertEquals(Success(breeds), awaitItem())
        }
    }

    @Test
    fun `should emit Error when use case throws an exception`() = runTest {
        whenever(getBreedsListUseCase()).thenReturn(
            flow { throw RuntimeException("Network error") }
        )

        viewModel.state.test {
            viewModel.fetchBreedsList()

            assertEquals(Loading, awaitItem())
            assertEquals(Error, awaitItem())
        }
    }

    @Test
    fun `should emit Error when use case throws an reporting exception`() = runTest {
        whenever(getBreedsListUseCase()).thenReturn(
            flow { throw RuntimeException("Network error") }
        )
        whenever(getBreedsListUseCase.reportError(any())).thenReturn(
            flow { throw RuntimeException("Reporting error") }
        )

        viewModel.state.test {
            viewModel.fetchBreedsList()

            assertEquals(Loading, awaitItem())
            assertEquals(Error, awaitItem())
        }
    }

    @Test
    fun `should emit GoToBreedDetails action when BreedSelected intent is received`() = runTest {
        val breed = Breed("Golden Retriever")
        val intent = BreedSelected(breed)

        viewModel.action.test {
            viewModel.onIntent(intent)

            assertEquals(GoToBreedDetails(breed), awaitItem())
        }
    }
}
