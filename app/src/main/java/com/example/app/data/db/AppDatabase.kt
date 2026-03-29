package com.example.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.app.data.db.dao.ArticleDao
import com.example.app.data.db.entities.ArticleEntity

@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
}
