package pl.wp.dogs.breeds_list

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import dagger.hilt.android.qualifiers.ActivityContext
import pl.wp.dogs.breed_details.BreedFragment
import pl.wp.dogs.model.Breed
import javax.inject.Inject
import javax.inject.Provider

class NavigationManager @Inject constructor(
    @ActivityContext private val context: Context,
    private val breedsListFragment: Provider<BreedsListFragment>,
    private val breedFragment: Provider<BreedFragment>
) {

    fun start(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, breedsListFragment.get())
            .commit()
    }

    fun goToBreed(dog: Breed) {
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
