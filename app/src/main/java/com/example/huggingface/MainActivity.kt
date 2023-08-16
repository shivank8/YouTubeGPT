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

          youtubeLink = findViewById(R.id.youtubeLinkInput)
    }

    fun onNextButtonClick(view: View) {
        val videoId = youtubeLink.text.toString()
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("videoId", videoId)
        startActivity(intent)
    }
}