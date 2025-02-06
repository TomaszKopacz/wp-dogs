package pl.wp.dogs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import pl.wp.dogs.breeds_list.BreedsListStarter
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var breedsListStarter: BreedsListStarter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        breedsListStarter.start(supportFragmentManager)
    }
}
