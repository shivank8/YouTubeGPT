package com.example.huggingface

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var inputEditText: EditText
    private lateinit var displayTextView: TextView
    private val sentimentAnalysisAPI = SentimentAnalysisAPI()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputEditText = findViewById(R.id.inputEditText)
        displayTextView = findViewById(R.id.displayTextView)
    }

    fun onSubmitButtonClicked(view: View) {
        val inputText = inputEditText.text.toString()

        CoroutineScope(Dispatchers.Main).launch {
            val response: JSONArray = sentimentAnalysisAPI.query(inputText)

            if (response.length() > 0) {
                val resultArray = response.getJSONArray(0)
                val output = resultArray.toString()
                displayTextView.text = output
            } else {
                displayTextView.text = "No results"
            }
        }
    }
}