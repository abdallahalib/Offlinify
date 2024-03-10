package offlinify.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.room.Room
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import offlinify.app.databinding.FragmentDownloadsBinding
import java.io.File

class DownloadsFragment : Fragment() {

    private lateinit var binding: FragmentDownloadsBinding
    private lateinit var database: DownloadsDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_downloads, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = Room.databaseBuilder(requireContext(), DownloadsDatabase::class.java, "offlinify_99").build()
        database.databaseDao().getAll().observe(viewLifecycleOwner) { downloads ->
            val adapter = DownloadsAdapter(downloads, onItemClick = { download ->
                val file = getVideoFile(download)
                if (file.exists()) {
                    openFile(file.toUri())
                } else {
                    lifecycleScope.launch(Dispatchers.IO) {
                        database.databaseDao().delete(download)
                        Snackbar.make(binding.root, R.string.file_not_found, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }, onItemLongClick = { download ->
                lifecycleScope.launch(Dispatchers.IO) {
                    val file = getVideoFile(download)
                    if (file.delete()) {
                        database.databaseDao().delete(download)
                        Snackbar.make(binding.root, R.string.deleted_successfully, Snackbar.LENGTH_SHORT).show()
                    }
                }
            })
            binding.recyclerView.adapter = adapter
            val layoutManager = GridLayoutManager(requireContext(), 2)
            binding.recyclerView.layoutManager = layoutManager
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openFile(uri: Uri) {
        Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "video/*")
            startActivity(this)
        }
    }


}