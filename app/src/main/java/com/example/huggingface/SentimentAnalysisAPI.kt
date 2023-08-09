package com.example.huggingface

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject

class SentimentAnalysisAPI {
    private val API_URL = "https://api-inference.huggingface.co/models/cardiffnlp/twitter-xlm-roberta-base-sentiment"
    private val token = "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"

    suspend fun query(input: String): JSONArray {
        val client = OkHttpClient()

        val payload = JSONObject()
        payload.put("inputs", input)

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), payload.toString())
        val request = Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Bearer $token")
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: "[]"
            JSONArray(responseBody)
        }
    }
}