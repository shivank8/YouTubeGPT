package com.example.huggingface

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
    private lateinit var inputEditText: EditText
    private lateinit var displayTextView: TextView
    private lateinit var captionTextView: TextView
    private val ApiHelper = ApiHelper()
    lateinit var captions:String
    val captions_demo="it is April 7 2023 and you're watching the code report one month ago Vector database weeviate landed 16 million dollars in series a funding last week Pinecone DB just got a check for 28 million at a 700 million valuation and yesterday chroma an open source project with only 1.2 GitHub Stars raised 18 million for its embeddings database and I just launched my own Vector database this morning we're currently pre-revenue pre-vision and pre-code and valued at 420 million dollars leave your credit card details in the comments if you'd like to invest but you might be wondering what the hell is a vector database or what the hell is a vector even well that's easy a vector is just an array of numbers but what's cool about vectors is that they can represent more complex objects like Words sentences images or audio files in a continuous High dimensional space called an embedding it's kind of like this when you go to a party all of the jocks sit around the TV and watch football meanwhile all the girls find the Dance Floor while you group together with all the programming introverts to talk about vectors in the corner notice how all the similar objects are grouped together embeddings work the same way except they map the semantic meaning of words together or similar features in virtually any other data type these embeddings can then be used for things like recommendation systems search engines and even text generation like chat GPT but once you have your embeddings the question becomes where do you store them and how do you query them quickly that's where Vector databases come in in a relational database you have rows and columns in a document database you have documents and collections but in a vector database you have arrays of numbers clustered together based on similarity which can be queried with ultra low latency making it an ideal choice for AI driven applications relational databases like postgres have tools like PG Vector to support this type of functionality and redis also has first class Vector support but a bunch of new native Vector databases are popping up like weeviate and milvis are open source options written in go then you have Pinecone which is extremely popular but is not open source then you have Chromo which is based on clickhouse under the hood and many other options from there let's jump into some code to see what it looks like here I'm using chroma and JavaScript and the first thing I I'll do is create the client then Define an embedding function in this case it will use the openai API to update the embeddings whenever a new data point is added each data point is just a document with an IDE and some text and finally we can query the database by passing a string of text just like an llm what's most interesting though is that in the query result we get the data back in addition to an array of distances with a smaller number indicating a higher degree of similarity that's pretty cool but the real reason that these databases are so hot right now is that they can extend llms with long-term memory you start with a general purpose model like open ai's gpt4 metaslama or Google's Lambda then provide your own data in a vector database when the user makes a prompt you can then query relevant documents from your own database to update the context which will customize the final response and it can also retrieve historical data to give the AI long-term memory in addition they also integrate with tools like link chain that combine multiple llms together it's all pretty crazy and I'm working on a tutorial on my second Channel with Wii V8 so make sure to subscribe over there if you really want to dive into this stuff and lastly in related news if you check out the top training repos in GitHub today they're almost all trying to create artificial general intelligence like Microsoft's Jarvis Auto GPT and baby AGI which are tools that use Vector databases and llms to prove themselves and this is terrifying as someone who just became a proofed engineer I never thought I'd become obsolet\n"
    val summary_text="A vector is just an array of numbers but they can represent more complex objects like Words sentences images or audio files in a continuous High dimensional space called an embedding. These embeddings can then be used for things like recommendation systems search engines and even text generation like chat GPT. Vector databases are so hot right now because they can extend llms with long-term memory."


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inputEditText = findViewById(R.id.inputEditText)
        displayTextView = findViewById(R.id.displayTextView)
        captionTextView = findViewById(R.id.captionTextView)
    }

    fun onSubmitButtonClicked(view: View) {
        val inputText = inputEditText.text.toString()

        CoroutineScope(Dispatchers.Main).launch {
            val response: JSONArray = ApiHelper.performSentimentAnalysis(captions)

            if (response.length() > 0) {
                val resultArray = response.getJSONObject(0)
                val res=resultArray.getString("summary_text")
                Log.e("res", resultArray.toString())
                val output = resultArray.toString()
                displayTextView.text = res
            } else {
                displayTextView.text = "No results"
            }
        }
    }
    fun onFetchCaptionsButtonClicked(view: View) {
        val videoId = "Ggao9SKhWjU"//"klTvEwg3oJ4"

        CoroutineScope(Dispatchers.Main).launch {
            val response: String = ApiHelper.fetchYouTubeCaptions(videoId)
            captions=response
            captionTextView.text = response

        }
    }
}