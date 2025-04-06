import com.example.deepmediq.models.Context

data class ChatListItem(
    val answer: String,
    val context: List<Context> = emptyList(),  // Default empty list since your example shows Array(0)
    val input: String
)

// If you need the context structure (even though empty in this example)
data class Context(
    val metadata: Metadata? = null,
    val page_content: String? = null
)

// If you need metadata details
data class Metadata(
    val author: String? = null,
    val year: Int? = null,
    val journal: String? = null,
    val title: String? = null
)