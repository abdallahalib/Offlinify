package offlinify.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import offlinify.app.databinding.ItemDownloadBinding

class DownloadsAdapter(private var downloads: List<Download>, private val onItemClick: (Download) -> Unit, private val onItemLongClick: (Download) -> Unit) :
    RecyclerView.Adapter<DownloadsAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemDownloadBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Download, onItemClick: (Download) -> Unit, onItemLongClick: (Download) -> Unit){
            binding.download = item
            binding.card.setOnClickListener { onItemClick(item) }
            binding.card.setOnLongClickListener {
                onItemLongClick(item)
                true
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemDownloadBinding.inflate(inflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = downloads[position]
        viewHolder.bind(item, onItemClick, onItemLongClick)
    }

    override fun getItemCount() = downloads.size
}
