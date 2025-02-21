package pl.wp.dogs.breeds_list

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

data class Breed(val name: String)

object BreedsListContract {
    interface Routing {
        fun goToBreed(dog: Breed): Completable
    }
}
