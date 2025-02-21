package pl.wp.dogs.breeds_list

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

// TODO: move to shared module
@Serializable
private data class ResponseBody(
    val message: Map<String, List<String>>
)

class GetBreedsListUseCase @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json
) {
    operator fun invoke(): Flow<List<Breed>> = flow {
        val request = buildRequest()
        val response = okHttpClient.newCall(request).execute().body!!.string()
        val responseBody = json.decodeFromString<ResponseBody>(response)

        emit(responseBody.message.keys.map(::Breed))
    }.flowOn(Dispatchers.IO)

    fun reportError(error: Throwable): Flow<Unit> = flow {
        Log.e("BreedsList", "Error", error)
    }

    private fun buildRequest() = Request.Builder()
        .get()
        .url("https://dog.ceo/api/breeds/list/all")
        .build()
}
