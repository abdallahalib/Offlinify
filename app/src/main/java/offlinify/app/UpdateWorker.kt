package offlinify.app

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yausername.youtubedl_android.YoutubeDL

class UpdateWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    override fun doWork(): Result {
        Log.i(HomeFragment.TAG, "Checking for updates")
        val ytdl = YoutubeDL.getInstance()
        ytdl.updateYoutubeDL(applicationContext, YoutubeDL.UpdateChannel.STABLE)
        Log.i(HomeFragment.TAG, ytdl.versionName(applicationContext)?:"")
        return Result.success()
    }
}
