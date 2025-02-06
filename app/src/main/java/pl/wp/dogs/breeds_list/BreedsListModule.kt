package pl.wp.dogs.breeds_list

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

@Module
@InstallIn(ActivityComponent::class)
abstract class BreedsListModule {
    @Binds
    abstract fun bindPresenter(presenter: BreedsListPresenter): BreedsListContract.Presenter

    @Binds
    abstract fun bindRouting(routing: BreedsListRouting): BreedsListContract.Routing

    @Binds
    abstract fun bindInteractor(interactor: BreedsListInteractor): BreedsListContract.Interactor

    companion object {
        @Provides
        fun provideOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .build()
        }

        @Provides
        fun provideJson(): Json {
            return Json {
                ignoreUnknownKeys = true
            }
        }
    }
}
