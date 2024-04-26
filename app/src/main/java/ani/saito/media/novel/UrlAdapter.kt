package ani.saito.media.novel

import android.annotation.SuppressLint
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ani.saito.FileUrl
import ani.saito.copyToClipboard
import ani.saito.databinding.ItemUrlBinding
import ani.saito.parsers.Book
import ani.saito.setSafeOnClickListener
import ani.saito.tryWith

class UrlAdapter(
    private val urls: List<FileUrl>,
    val book: Book,
    val novel: String,
    val callback: BookDialog.Callback?
) :
    RecyclerView.Adapter<UrlAdapter.UrlViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrlViewHolder {
        return UrlViewHolder(
            ItemUrlBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UrlViewHolder, position: Int) {
        val binding = holder.binding
        val url = urls[position]
        binding.urlQuality.text = url.url
        binding.urlQuality.maxLines = 4
        binding.urlDownload.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int = urls.size

    inner class UrlViewHolder(val binding: ItemUrlBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setSafeOnClickListener {
                tryWith(true) {
                    binding.urlDownload.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    callback?.onDownloadTriggered(book.links[bindingAdapterPosition].url)
                    /*download(
                        itemView.context,
                        book,
                        bindingAdapterPosition,
                        novel
                    )*/

                }
            }
            itemView.setOnLongClickListener {
                val file = urls[bindingAdapterPosition]
                copyToClipboard(file.url, true)
                true
            }
        }
    }
}