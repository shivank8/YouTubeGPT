package com.example.huggingface

import ChatAdapter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.huggingface.databinding.ActivityChatBinding
import kotlin.random.Random

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val chatMessages = mutableListOf<ChatMessageModel>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
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
            val aiResponse = getAnswer(userInput) // Call your function to get AI response
            addAiResponse(aiResponse)
            binding.messageInput.text.clear()
        }
    }

    private fun addUserMessage(message: String) {
        chatMessages.add(ChatMessageModel(message, true))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        binding.chatRecyclerView.scrollToPosition(chatMessages.size - 1)
    }

    private fun addAiResponse(message: String) {
        chatMessages.add(ChatMessageModel(message, false))
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        binding.chatRecyclerView.scrollToPosition(chatMessages.size - 1)
    }



    private fun getAnswer(question: String): String {
        val randomWords = listOf("conversation", "language", "generation", "virtual", "intelligence", "response")
        val randomIndex = Random.nextInt(randomWords.size)
        val randomWord = randomWords[randomIndex]

        return "$question What about $randomWord?"
    }
}