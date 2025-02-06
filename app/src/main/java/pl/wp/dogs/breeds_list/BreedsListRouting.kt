package pl.wp.dogs.breeds_list

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ActivityContext
import io.reactivex.Completable
import pl.wp.dogs.breed_details.BreedFragment
import javax.inject.Inject
import javax.inject.Provider

class BreedsListRouting @Inject constructor(
    @ActivityContext private val context: Context,
    private val breedFragment: Provider<BreedFragment>
) : BreedsListContract.Routing {
    override fun goToBreed(dog: Breed) = Completable.fromCallable {
        if (context is FragmentActivity) {
            context.supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, breedFragment.get().apply {
                    arguments = Bundle().apply { putString("breed_name", dog.name) }
                })
                .addToBackStack("breed_fragment")
                .commit()
        } else {
            throw IllegalStateException("Context is not a FragmentActivity")
        }
    }

}
