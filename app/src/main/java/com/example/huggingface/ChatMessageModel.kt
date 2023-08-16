package com.example.huggingface

data class ChatMessageModel (
    val message: String,
    val isUserMessage: Boolean, // true if user's message, false if AI's response
    val sectionLink: String? = null // link to the section for the "Go to Section" button

)