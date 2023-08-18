package com.shivank.youtubeGPT

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.regex.Pattern

class ApiHelper {
    private val secrets=Secrets()
    private val summaryApiUrl = "https://api-inference.huggingface.co/models/facebook/bart-large-cnn"
    private val apiAccessToken = secrets.accessToken2

    private val youtubeApiUrl = "https://youtube-video-subtitles-list.p.rapidapi.com/"
    private val youtubeApiKey=secrets.youtubeApiKey

    private val client = OkHttpClient()

    private var dictionary = mutableMapOf<String, Int>()
    private lateinit var dataList: List<String>

    suspend fun getSummary(input: String): JSONArray {
        val payload = JSONObject()
        payload.put("inputs", input)

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), payload.toString())
        val request = buildRequest(summaryApiUrl, apiAccessToken, requestBody)

        return performApiRequest(request)
    }

    suspend fun fetchYouTubeCaptions(videoId: String): String {
        val request = buildRequest(youtubeApiUrl, youtubeApiKey, null, videoId)

        val response: JSONArray=performApiRequest(request)
        var captions=""
        if (response.length() > 0) {
                val firstObject = response.getJSONObject(0)
                val baseUrl = firstObject.getString("baseUrl")
                Log.v("Base url", baseUrl)
                captions=fetchAndProcessCaptionsFromBaseUrl(baseUrl)
        }
        else{
            captions="Invalid Video link or No english subtitle found!"
        }
        return captions
    }

    private suspend fun fetchAndProcessCaptionsFromBaseUrl(baseUrl: String): String {
        val request = Request.Builder()
            .url(baseUrl)
            .get()
            .build()

        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            try {
                val response = client.newCall(request).execute()
                var responseBody = response.body?.string() ?: ""
                responseBody=responseBody.substring(39)
                val pattern = Pattern.compile("""<text start="(\d+)" dur="\d+">([^<]+)</text>""")
                val matcher = pattern.matcher(responseBody)

                //val dictionary = mutableMapOf<String, Int>()

                while (matcher.find()) {
                    val start = matcher.group(1).toInt()
                    val text = matcher.group(2)
                    dictionary[text as String] = start
                }
                println(dictionary)
                val captions = dictionary.keys.joinToString(" ")
                dataList = dictionary.keys.toList()
                println(dataList)

                Log.v("Final Captions", captions)

                captions
            } catch (e: IOException) {
                Log.e("ApiHelper caption error", "Error fetching captions: ${e.message}")
                ""
            }
        }
    }


    suspend fun getVectorEmbeddings(inputQuery: String): List<String> {
        val payload = JSONObject()
        payload.put(
            "inputs",
            JSONObject(mapOf("source_sentence" to inputQuery, "sentences" to dataList))
        )
        val API_URL = "https://api-inference.huggingface.co/models/sentence-transformers/all-MiniLM-L6-v2"

        val requestBody = payload.toString().toRequestBody("application/json".toMediaTypeOrNull())
        val request = buildRequest(API_URL, apiAccessToken, requestBody)

        val output = performApiRequest(request)
        println(output)
        val embeddings = mutableListOf<Double>()
        for (i in 0 until output.length()) {
            embeddings.add(output.getDouble(i))
        }
        val topKEmbeddings = getTopKValues(embeddings, 3)

        for ((index, value) in topKEmbeddings) {
            println("Index: $index, Value: $value")// Index: 14, Value: 0.5087682604789734
            val text = dataList[index] // Look at this. If you can see this, it is very light purple. Looks very nice.
            println(text) // actual caption line
            println(dictionary[text])// gives time, 56
        }
        val resultList = mutableListOf<String>()

        if(topKEmbeddings.isNotEmpty()) {
            val firstIndex = topKEmbeddings[0].first
            val text = dataList[firstIndex]
            val sectionTime = (dictionary[text]).toString()
            resultList.add(text)
            resultList.add(sectionTime)
        }
        else{
            resultList.add("Something went wrong. Please try again!")
            resultList.add("-1s")
        }
        return resultList
    }
    private fun getTopKValues(values: List<Double>, top_k: Int): List<Pair<Int, Double>> {

        if (top_k >= values.size) {
            return values.mapIndexed { index, value -> Pair(index, value) }
        }

        val indexedValues = values.mapIndexed { index, value -> Pair(index, value) }
        val sortedValues = indexedValues.sortedByDescending { it.second }

        return sortedValues.subList(0, top_k)
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
