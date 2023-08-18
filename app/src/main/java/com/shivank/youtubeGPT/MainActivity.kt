package com.shivank.youtubeGPT

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    private lateinit var youtubeLink: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
          youtubeLink = findViewById(R.id.youtubeLinkInput)
    }

    fun onNextButtonClick(view: View) {
        val ytLink = youtubeLink.text.toString()
        val videoId=getYouTubeVideoId(ytLink)
        if (videoId != null) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("videoId", videoId)
        startActivity(intent)}
        else{
            Toast.makeText(this, "Invalid YouTube video link. Try again!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getYouTubeVideoId(link: String): String? {
        val pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/|youtu.be\\/|v\\/|\\/v\\/|watch\\?v=|embed\\/|youtu.be\\/|v\\/|\\/v\\/|watch\\?v=|embed\\/|youtu.be\\/|v\\/|\\/v\\/|\\u200C\\u200B\\u200D\\uFEFF|\\u200C\\u200B\\u200D\\uFEFF)[^#\\&\\?\\n]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(link)
        return if (matcher.find()) {
            matcher.group()
        } else {
            null
        }

    }
}