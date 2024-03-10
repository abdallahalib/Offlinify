package offlinify.app

import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.squareup.picasso.Picasso

@BindingAdapter("setImageUrl")
fun setImageUrl(imageView: ImageView, url: String?) {
    imageView.apply {
        Picasso.get().load(url).into(this)
    }
}

@BindingAdapter("isVisible")
fun isVisible(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter("setProgress")
fun setProgress(progressIndicator: LinearProgressIndicator, progress: Int) {
    if (progress != -1) {
        progressIndicator.isIndeterminate = false
        progressIndicator.setProgress(progress, true)
    } else {
        progressIndicator.isIndeterminate = true
    }
}