import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

            if (!chatMessage.sectionLink.isNullOrEmpty()) {
                binding.goToSectionButton.visibility = View.VISIBLE
                binding.goToSectionButton.setOnClickListener {
                    // Handle "Go to Section" button click here
                    Toast.makeText(itemView.context, "Go to Section button clicked", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.goToSectionButton.visibility = View.GONE
            }
        }
    }
}
