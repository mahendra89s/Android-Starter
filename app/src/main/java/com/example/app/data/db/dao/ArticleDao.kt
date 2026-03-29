package com.example.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.app.data.db.entities.ArticleEntity

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticle(articleEntity: ArticleEntity)

    @Query("SELECT * FROM articles")
    fun getAllArticles(): List<ArticleEntity>
}