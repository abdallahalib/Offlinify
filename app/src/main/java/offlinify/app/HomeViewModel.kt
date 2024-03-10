package offlinify.app

import android.content.ContentResolver
import android.content.ContentValues
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.mapper.VideoInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random


class HomeViewModel : ViewModel() {

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> get() = _progress

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _info = MutableLiveData<VideoInfo?>()
    val info: LiveData<VideoInfo?> get() = _info

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private var link = ""

    private var processId = ""

    fun setLink(link: String) {
        this.link = link
    }

    private fun download(request: YoutubeDLRequest) {
        Log.i(TAG, "download called")
        CoroutineScope(Dispatchers.IO).launch {
            if (isActive) {
                processId = Random.nextLong().toString()
                runCatching {
                    YoutubeDL.getInstance().execute(request, processId) { progress, eta, message ->
                        Log.i(TAG, message)
                        if (message.contains("100%")) {
                            _progress.updateValue(100)
                        } else {
                            _progress.updateValue(progress.toInt())
                        }
                    }
                }.onSuccess { response ->
                    YoutubeDL.getInstance().destroyProcessById(processId)
                }.onFailure { throwable ->
                    Log.i(TAG, throwable.message ?: "onFailure")
                    YoutubeDL.getInstance().destroyProcessById(processId)
                    _message.postValue(throwable.message ?: "")
                }
            }
        }
    }

    fun createRequest() {
        CoroutineScope(Dispatchers.IO).launch {
            Log.i(TAG, "createRequest called")
            val youtubeDLDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val request = YoutubeDLRequest(link)
            request.addOption("-o", youtubeDLDir.absolutePath + "/%(id)s.mp4")
            request.addOption("--no-mtime")
            request.addOption("-f", "best[ext=mp4]")
            request.addOption("--no-write-info-json")
            request.addOption("--ignore-errors")
            request.addOption("--verbose")
            request.addOption("--output-na-placeholder")
            request.addOption("--restrict-filenames")
            request.addOption("--encoding", "UTF8")
            download(request)
            fetchInfo(link)
        }
    }

    private fun fetchInfo(url: String) {
        Log.i(TAG, "fetchInfo called")
        viewModelScope.launch {
        _loading.updateValue(true)
        _info.updateValue(null)
        _progress.updateValue(-1)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val info = YoutubeDL.getInstance().getInfo(url)
                _info.updateValue(info)
            } catch (exception: YoutubeDLException) {
                _message.updateValue(exception.message)
            }
            _loading.postValue(false)
        }
            }
    }

    companion object {
        const val TAG = "YTDL"
    }
}