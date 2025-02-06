package pl.wp.dogs.breeds_list

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

data class Breed(val name: String)

object BreedsListContract {
    interface View {
        val breedClicks: Observable<Breed>
        fun showBreeds(dogs: List<Breed>)
        fun showError(error: Throwable)
    }

    interface Presenter {
        fun onAttach(view: View)
        fun onDetach()
    }

    interface Routing {
        fun goToBreed(dog: Breed): Completable
    }

    interface Interactor {
        fun getBreeds(): Single<List<Breed>>
        fun reportError(error: Throwable): Completable
    }
}
