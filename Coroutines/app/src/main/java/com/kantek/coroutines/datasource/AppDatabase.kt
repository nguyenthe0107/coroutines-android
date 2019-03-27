package com.kantek.coroutines.datasource

import android.arch.persistence.room.*
import com.kantek.coroutines.models.Todo

@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}

@Dao
interface TodoDao {
    @Query("select * from Todo where userId=:userId")
    fun gets(userId: String): MutableList<Todo>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(value: MutableList<Todo>)

    @Update
    fun save(it: Todo)

    @Query("select exists(select 1 from Todo where userId=:userId)")
    fun hasUser(userId: String): Boolean
}