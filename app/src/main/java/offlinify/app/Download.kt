package offlinify.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Download(
    @PrimaryKey val id: String,
    val thumbnail: String?,
    val title: String?,
    val uploader: String?,
    val date: Long?)