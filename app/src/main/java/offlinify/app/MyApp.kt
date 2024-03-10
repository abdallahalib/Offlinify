package offlinify.app

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        val builder = VmPolicy.Builder()
        builder.detectFileUriExposure()
        StrictMode.setVmPolicy(builder.build())
    }
}