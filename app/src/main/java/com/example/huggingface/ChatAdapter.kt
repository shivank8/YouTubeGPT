import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.huggingface.ChatMessageModel
import com.example.huggingface.R
import com.example.huggingface.databinding.ItemChatMessageBinding

class ChatAdapter(private val chatMessages: List<ChatMessageModel>) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = chatMessages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    inner class ViewHolder(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatMessage: ChatMessageModel) {
            binding.messageText.text = chatMessage.message

            if (chatMessage.isUserMessage) {
                binding.messageText.setBackgroundResource(R.drawable.bg_chat_bubble_user)
                binding.root.layoutDirection = View.LAYOUT_DIRECTION_RTL
            } else {
                binding.messageText.setBackgroundResource(R.drawable.bg_chat_bubble_ai)
                binding.root.layoutDirection = View.LAYOUT_DIRECTION_LTR
            }
            val screenWidth = Resources.getSystem().displayMetrics.widthPixels
            val maxWidth = (screenWidth * 0.7).toInt()
            binding.messageText.maxWidth = maxWidth

            if (!chatMessage.sectionLink.isNullOrEmpty()) {
                binding.goToSectionButton.visibility = View.VISIBLE
                binding.goToSectionButton.setOnClickListener {
                    openYouTubeApp(itemView.context, chatMessage.sectionLink)
                }
            } else {
                binding.goToSectionButton.visibility = View.GONE
            }
        }
        private fun openYouTubeApp(context: Context, videoLink: String) {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoLink))
                intent.setPackage("com.google.android.youtube")
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.e("Youtube app","Not found!")
                // YouTube app is not installed, handle accordingly (e.g., open in browser)
            }
        }
    }
}