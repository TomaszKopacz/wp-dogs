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
import pl.wp.dogs.model.Breed
import javax.inject.Inject

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
        val response = okHttpClient.newCall(request).execute()

        response.body?.use { responseBody ->
            val rawResponse = responseBody.string()
            val parsedResponse = json.decodeFromString<ResponseBody>(rawResponse)

            emit(parsedResponse.message.keys.map(::Breed))
        } ?: throw IllegalStateException("Response body is null")
    }.flowOn(Dispatchers.IO)

    fun reportError(error: Throwable): Flow<Unit> = flow {
        Log.e("BreedsList", "Error", error)
    }

    private fun buildRequest() = Request.Builder()
        .get()
        .url("https://dog.ceo/api/breeds/list/all")
        .build()
}
