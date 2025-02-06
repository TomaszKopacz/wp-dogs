package pl.wp.dogs.breeds_list

import androidx.fragment.app.FragmentManager
import javax.inject.Inject
import javax.inject.Provider

class BreedsListStarter @Inject constructor(private val fragment: Provider<BreedsListFragment>) {
    fun start(fragmentManager: FragmentManager) {
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment.get())
            .commit()
    }
}
