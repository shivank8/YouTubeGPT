package com.shivank.youtubeGPT

import ChatAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.shivank.youtubeGPT.databinding.ActivityChatBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val chatMessages = mutableListOf<ChatMessageModel>()
    private lateinit var chatAdapter: ChatAdapter
    private val ApiHelper = ApiHelper()
    lateinit var videoId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        videoId = intent.getStringExtra("videoId").toString()
        println(videoId)
        Log.e("videoId", videoId)
        getSummary(videoId)
    }


    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(chatMessages)
        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }
    }

    fun onSendButtonClick(view: View) {
        val userInput = binding.messageInput.text.toString()
        if (userInput.isNotEmpty()) {
            addUserMessage(userInput)
            getAnswer(userInput) // Call your function to get AI response
            binding.messageInput.text.clear()
        }
    }

    private fun addUserMessage(message: String) {
        chatMessages.add(ChatMessageModel(message, true))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        binding.chatRecyclerView.scrollToPosition(chatMessages.size - 1)
    }

    private fun addAiResponse(message: String,sectionTiming:String="-1s") {
        if(sectionTiming=="-1s")
            chatMessages.add(ChatMessageModel(message, false,))
        else{
            val sectionUrl="https://www.youtube.com/watch?v=$videoId&t=$sectionTiming"
            println(sectionUrl)
            chatMessages.add(ChatMessageModel(message, false,sectionUrl))
        }
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        binding.chatRecyclerView.scrollToPosition(chatMessages.size - 1)
    }

    private fun getSummary(videoId: String?) {
        CoroutineScope(Dispatchers.Main).launch {
            val captions: String = ApiHelper.fetchYouTubeCaptions(videoId!!)
            val response: JSONArray = ApiHelper.getSummary(captions)
            if (captions.isNotEmpty()) {
                if (response.length() > 0) {
                    val resultArray = response.getJSONObject(0)
                    var summary = "The summary of the video is: \n"
                    summary += resultArray.getString("summary_text")
                    Log.v("res", resultArray.toString())
                    addAiResponse(summary)
                    addAiResponse("Try asking some questions related to this video.")
                } else {
                    addAiResponse("Hey there, \nTry asking some questions related to this video.")
                }
            } else {
                Log.e("captions","No captions")
            }
        }
    }

    private fun getAnswer(question: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val response = ApiHelper.getVectorEmbeddings(question)
            addAiResponse(response[0],response[1])
        }
    }

}