package com.example.huggingface

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.regex.Pattern

class ApiHelper {
    private val sentimentApiUrl = "https://api-inference.huggingface.co/models/cardiffnlp/twitter-xlm-roberta-base-sentiment"
    private val sentimentApiToken = "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"

    private val youtubeApiUrl = "https://youtube-video-subtitles-list.p.rapidapi.com/"

    private val client = OkHttpClient()

    suspend fun performSentimentAnalysis(input: String): JSONArray {
        val payload = JSONObject()
        payload.put("inputs", input)

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), payload.toString())
        val request = buildRequest(sentimentApiUrl, sentimentApiToken, requestBody)

        return performApiRequest(request)
    }

    suspend fun fetchYouTubeCaptions(videoId: String): String {
        val request = buildRequest(youtubeApiUrl, "c625f8d054mshf1ad27456f28955p1e5836jsn6afc84eccd6d", null, videoId)

        val response: JSONArray=performApiRequest(request)
        var captions=""
        if (response.length() > 0) {
                val firstObject = response.getJSONObject(0)
                val baseUrl = firstObject.getString("baseUrl")
                Log.e("Base url", baseUrl)
                captions=fetchAndProcessCaptionsFromBaseUrl(baseUrl)
        }
        else{
            captions="Invalid Video link or No english subtitle found!"
        }
        return captions
    }

    suspend fun fetchAndProcessCaptionsFromBaseUrl(baseUrl: String): String {
        val request = Request.Builder()
            .url(baseUrl)
            .get()
            .build()

        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                val pattern = Pattern.compile("<text[^>]*>(.*?)</text>", Pattern.DOTALL)
                val matcher = pattern.matcher(responseBody)

                val outputBuilder = StringBuilder()
                while (matcher.find()) {
                    outputBuilder.append(matcher.group(1).trim()).append(" ")
                }
                val finalCaption = outputBuilder.toString().trim()

                Log.v("Final Captions", finalCaption)

                finalCaption
            } catch (e: IOException) {
                Log.e("ApiHelper", "Error fetching captions: ${e.message}")
                ""
            }
        }
    }

    private fun buildRequest(
        apiUrl: String,
        apiKey: String,
        requestBody: RequestBody?,
        videoId: String? = null
    ): Request {
        val requestBuilder = Request.Builder()
            .url("$apiUrl${videoId?.let { "?videoId=$it&locale=en" } ?: ""}")
            .addHeader("X-RapidAPI-Key", apiKey)
            .addHeader("X-RapidAPI-Host", apiUrl.substringAfter("https://").substringBefore("/"))

        if (requestBody != null) {
            requestBuilder.post(requestBody)
        }

        return requestBuilder.build()
    }

    private suspend fun performApiRequest(request: Request): JSONArray {
        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            try {
                JSONArray(responseBody ?: "[]")
            } catch (e: JSONException) {
                Log.e("ApiHelper", "Error parsing JSON response: ${e.message}")
                JSONArray() // Return an empty JSON array in case of parsing error
            }
        }
    }

}