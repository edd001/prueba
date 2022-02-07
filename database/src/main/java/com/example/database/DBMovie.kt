package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.database.dao.MovieDao
import com.example.domain.models.Movie

@Database(entities = [Movie::class], version = 1)
abstract class DBMovie: RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        @JvmStatic
        fun getInstance(context: Context): DBMovie{
            return Room.databaseBuilder(context, DBMovie::class.java, "movieDB")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}