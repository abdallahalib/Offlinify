package offlinify.app

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DownloadsDatabaseDao {
    @Query("SELECT * FROM download ORDER BY date DESC")
    fun getAll(): LiveData<List<Download>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(download: Download)

    @Delete
    fun delete(download: Download)

    @Query("DELETE FROM download")
    fun deleteAll()
}
