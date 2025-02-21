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
    abstract fun bindRouting(routing: BreedsListRouting): BreedsListContract.Routing

    companion object {
        @Provides
        fun provideGetBreedsListUseCase(
            okHttpClient: OkHttpClient,
            json: Json
        ): GetBreedsListUseCase {
            return GetBreedsListUseCase(okHttpClient, json)
        }

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
