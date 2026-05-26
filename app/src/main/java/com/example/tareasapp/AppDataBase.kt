package com.example.tareasapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Database(
    entities = [TaskEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        private val TAREAS_INICIALES = listOf(
            TaskEntity(
                title = "Configurar repositorio en GitHub",
                isCompleted = true
            ),
            TaskEntity(
                title = "Implementar base de datos con Room",
                isCompleted = true
            ),

            TaskEntity(
                title = "Construir UI con Jetpack Compose",
                isCompleted = true
            ),
            TaskEntity(
                title = "Feed, Share and Delete visualizations",
                isCompleted = true
            ),
            TaskEntity(
                title = "Share and Post visualization",
                isCompleted = true
            ),
            TaskEntity(
                title = "Create activity center screen",
                isCompleted = false
            ),
            TaskEntity(
                title = "Notifications",
                isCompleted = false
            ),
            TaskEntity(
                title = "Real-Time Updates",
                isCompleted = false
            )
        )

        // Agrega las tuyas reales

            fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room
                    .databaseBuilder(
                        context.applicationContext,
                        AppDataBase::class.java,
                        "tasks_db"
                    )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getInstance(context).taskDao()
                                TAREAS_INICIALES.forEach { tarea ->
                                    dao.insert(tarea)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}