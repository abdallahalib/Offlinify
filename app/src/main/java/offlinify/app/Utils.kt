package offlinify.app

import android.os.Environment
import androidx.lifecycle.MutableLiveData
import java.io.File

fun <T> MutableLiveData<T>.updateValue(newValue: T?) {
    if (value != newValue) {
        postValue(newValue)
    }
}

fun getVideoFile(download: Download): File {
    val youtubeDLDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    return youtubeDLDir.resolve("${download.id}.mp4")
}