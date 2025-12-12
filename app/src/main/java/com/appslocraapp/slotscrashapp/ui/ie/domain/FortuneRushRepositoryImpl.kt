package com.appslocraapp.slotscrashapp.ui.ie.domain

import android.util.Log
import com.appslocraapp.slotscrashapp.app.MainApplication.Companion.FORTUNE_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FortuneRushApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun feedMixGetClient(
        @Body jsonString: JsonObject,
    ): Call<FortuneRushEntity>
}


private const val FEED_MIX_SITELI = "https://fortunerushcasino.com/"

class FortuneRushRepositoryImpl {

    suspend fun feedMixAppGetClient(
        fortuneRushParam: FortuneRushParam,
        eggLabelConversion: MutableMap<String, Any>?
    ): FortuneRushEntity? {
        val gson = Gson()
        val api = feedMixAppAGetApi(FEED_MIX_SITELI, null)

        val eggLabelJsonObject = gson.toJsonTree(fortuneRushParam).asJsonObject
        eggLabelConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            eggLabelJsonObject.add(key, element)
        }
        return try {
            val eggLabelRequest: Call<FortuneRushEntity> = api.feedMixGetClient(
                jsonString = eggLabelJsonObject,
            )
            val eggLabelResult = eggLabelRequest.awaitResponse()
            if (eggLabelResult.code() == 200) {
                eggLabelResult.body()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.d(FORTUNE_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun feedMixAppAGetApi(url: String, client: OkHttpClient?): FortuneRushApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
