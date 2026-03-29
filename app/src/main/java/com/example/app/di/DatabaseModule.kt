package com.example.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.app.data.db.AppDatabase
import com.example.app.data.db.dao.ArticleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context = context,
        klass = AppDatabase::class.java,
        name = "app_database"
    )
       // .addMigrations(MIGRATION_1_2)
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideNewsDao(
        database: AppDatabase
    ): ArticleDao = database.articleDao()

}


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Example: Adding a 'category' column to the 'Article' table
        db.execSQL("ALTER TABLE Article ADD COLUMN category TEXT NOT NULL DEFAULT ''")
    }
}