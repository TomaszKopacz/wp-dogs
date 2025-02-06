package pl.wp.dogs.breeds_list

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

@Serializable
private data class ResponseBody(
    val message: Map<String, List<String>>
)

class BreedsListInteractor @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json
) :
    BreedsListContract.Interactor {
    override fun getBreeds() = Single.just(buildRequest())
        .observeOn(Schedulers.io())
        .map { okHttpClient.newCall(it).execute().body!!.string() }
        .map<ResponseBody>(json::decodeFromString)
        .map { it.message.keys.map(::Breed) }

    override fun reportError(error: Throwable) = Completable.fromCallable {
        Log.e("BreedsList", "Error", error)
    }

    private fun buildRequest() = Request.Builder()
        .get()
        .url("https://dog.ceo/api/breeds/list/all")
        .build()
}
