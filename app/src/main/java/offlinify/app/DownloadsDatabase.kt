package offlinify.app

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Download::class], version = 1)
abstract class DownloadsDatabase : RoomDatabase() {
    abstract fun databaseDao(): DownloadsDatabaseDao
}
