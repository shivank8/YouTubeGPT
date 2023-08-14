package com.example.huggingface

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var youtubeLink: EditText
    private lateinit var displayTextView: TextView
    private lateinit var captionTextView: TextView
    private val ApiHelper = ApiHelper()
    lateinit var captions:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//
          youtubeLink = findViewById(R.id.youtubeLinkInput)
//        displayTextView = findViewById(R.id.displayTextView)
//        captionTextView = findViewById(R.id.captionTextView)
    }


    fun onFetchCaptionsButtonClicked(view: View) {
        val videoIds = "Ggao9SKhWjU"//"klTvEwg3oJ4"

        CoroutineScope(Dispatchers.Main).launch {
            val captions: String = ApiHelper.fetchYouTubeCaptions(videoIds)
            val response: JSONArray = ApiHelper.getSummary(captions)

            if (response.length() > 0) {
                val resultArray = response.getJSONObject(0)
                val summary=resultArray.getString("summary_text")
                Log.v("res", resultArray.toString())
                displayTextView.text = summary
            } else {
                displayTextView.text = "No results"
            }

        }
    }

    fun onNextButtonClick(view: View) {
        val videoId = youtubeLink.text.toString()
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("videoId", videoId)
        startActivity(intent)
    }
}