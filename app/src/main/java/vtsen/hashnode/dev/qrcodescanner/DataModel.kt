package vtsen.hashnode.dev.qrcodescanner

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.annotation.WorkerThread

//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.HiltAndroidApp
//import dagger.hilt.android.lifecycle.HiltViewModel
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import kotlinx.coroutines.flow.Flow
//import vtsen.hashnode.dev.qrcodescanner.ui.Screen
//import javax.inject.Inject
//import javax.inject.Singleton

@Entity(tableName = "list")
data class ScannedItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uriString: String,
    val scannedAt: Long = System.currentTimeMillis()
)

//
//@Entity(tableName = "task_item_table")
//class TaskItem(
//    @ColumnInfo(name = "uriString") val uriString: String,
//    @ColumnInfo(name = "scannedAt") var Long: Long = System.currentTimeMillis(),
//    @ColumnInfo(name = "dueTimeString") var dueTimeString: String?,
//    @ColumnInfo(name = "completedDateString") var completedDateString: String?,
//    @PrimaryKey(autoGenerate = true) var id: Int = 0
//)
//{
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun completedDate(): LocalDate? = if (completedDateString == null) null else LocalDate.parse(completedDateString, dateFormatter)
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun dueTime(): LocalTime? = if (dueTimeString == null) null else LocalTime.parse(dueTimeString, timeFormatter)
////
////    @RequiresApi(Build.VERSION_CODES.O)
////    fun isCompleted() = completedDate() != null
//
//    companion object {
//        @RequiresApi(Build.VERSION_CODES.O)
//        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_TIME
//        @RequiresApi(Build.VERSION_CODES.O)
//        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE
//    }
//}



@Dao
interface ScanItemDao
{
    @Query("SELECT * FROM list ORDER BY id DESC")
    fun allTaskItems(): Flow<List<ScannedItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskItem(taskItem: ScannedItem)

    @Update
    suspend fun updateTaskItem(taskItem: ScannedItem)

    @Delete
    suspend fun deleteTaskItem(taskItem: ScannedItem)
}


@Database(entities = [ScannedItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanDao(): ScanItemDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room
                    .databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "scans.db"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}












//
//
//class TaskItemRepository(private val taskItemDao: TaskItemDao)
//{
//    val allTaskItems: Flow<List<TaskItem>> = taskItemDao.allTaskItems()
//
//    @WorkerThread
//    suspend fun insertTaskItem(taskItem: TaskItem)
//    {
//        taskItemDao.insertTaskItem(taskItem)
//    }
//
//    @WorkerThread
//    suspend fun updateTaskItem(taskItem: TaskItem)
//    {
//        taskItemDao.updateTaskItem(taskItem)
//    }
//}
















val URIList = mutableStateListOf<ScannedItem>()
//
//@Database(entities = [ScannedItem::class], version = 1)
//abstract class UserDatabase : RoomDatabase() {
//    abstract fun userDao(): ScanDao
//
//}
//
//@HiltAndroidApp
//class MyApp : Application()
//
//
//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//
//    @Provides @Singleton
//    fun provideDb(@ApplicationContext ctx: Context): UserDatabase =
//        Room.databaseBuilder(ctx, UserDatabase::class.java, "scans.db").build()
//
//    @Provides
//    fun provideDao(db: UserDatabase): ScanDao = db.userDao()
//}
//
//class UserRepository @Inject constructor(
//    private val userDao: ScanDao
//) {
//
//    fun getAllUsers(): List<ScannedItem> {
//        return userDao.getAllItems()
//    }
//
//    fun insertUser(user: ScannedItem) {
//        userDao.insertList(user)
//    }
//
//    fun deleteUser(user: ScannedItem) {
//        userDao.deleteList(user)
//    }
//}
//
//@HiltViewModel
//class UserViewModel @Inject constructor(
//    private val userRepository: UserRepository
//) : ViewModel() {
//
//    fun getAllUsers(): List<ScannedItem> {
//        return userRepository.getAllUsers()
//    }
//
//    fun insertUser(user: ScannedItem) {
//        userRepository.insertUser(user)
//    }
//
//    fun deleteUser(user: ScannedItem) {
//        userRepository.deleteUser(user)
//    }
//}