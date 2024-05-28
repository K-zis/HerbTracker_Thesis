package com.example.herbtracker.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Base64.DEFAULT
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.nio.ByteBuffer


@Database(entities = [HerbPoint::class], version = 2, exportSchema = false)
abstract class HerbDatabase : RoomDatabase() {

    abstract fun herbDao(): HerbPointDao

}