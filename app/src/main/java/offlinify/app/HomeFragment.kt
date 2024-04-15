package offlinify.app

import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.media.MediaScannerConnection
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.android.material.snackbar.Snackbar
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import offlinify.app.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val ytdl = YoutubeDL.getInstance()
            ytdl.init(requireContext())
                val updateWorkRequest: PeriodicWorkRequest =
                    PeriodicWorkRequestBuilder<UpdateWorker>(1, TimeUnit.DAYS)
                        .build()
                WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork("YTDL_UPDATE", ExistingPeriodicWorkPolicy.KEEP, updateWorkRequest)

        } catch (e: YoutubeDLException) {
            Log.e(TAG, e.message ?: "")
        }
        binding.paste.setOnClickListener {
            val clipboardManager: ClipboardManager =
                requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = clipboardManager.primaryClip
            if (clipData != null && clipData.itemCount > 0) {
                val item = clipData.getItemAt(0)
                binding.link.setText(item.text)
            }
        }
        binding.download.setOnClickListener {
            if (YoutubeDL.getInstance().versionName(requireContext()).isNullOrBlank()) {
                Snackbar.make(binding.root, R.string.try_again_later, Snackbar.LENGTH_SHORT).setAnchorView(binding.paste).show()
            } else {
                viewModel.createRequest()
            }
        }
        viewModel.message.observe(viewLifecycleOwner) { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).setAnchorView(binding.paste).show()
        }
        viewModel.progress.observe(viewLifecycleOwner) { progress ->
            if (progress == 100) {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.info.value?.let { info ->
                        val database = Room.databaseBuilder(requireContext(), DownloadsDatabase::class.java, "offlinify_99").build()
                        val download = Download(
                            id = info.id ?: Random.nextLong().toString(),
                            thumbnail = info.thumbnail,
                            title = info.title,
                            uploader = info.uploader,
                            date = Date().time)
                        database.databaseDao().insert(download)
                        val path = getVideoFile(download).absolutePath
                        MediaScannerConnection.scanFile(requireContext(), arrayOf(path), arrayOf("video/mp4"), null)
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "YTDL"
    }
}